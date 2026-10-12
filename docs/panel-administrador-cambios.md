# Panel de administrador: cambios y casos de uso corregidos

Documento de trabajo. Compara los casos de uso del administrador con lo programado en los DAO.

## 1. Cambios de código previstos (huecos encontrados)

| # | Archivo | Problema | Cambio |
|---|---|---|---|
| 1 | `ReservaDao.cancelarReserva` | Solo cancela reservas PENDIENTE; el caso 14 pide cancelar también las CONFIRMADA. | Aceptar estados PENDIENTE y CONFIRMADA (no una ya CANCELADA). |
| 2 | `ReservaDao.confirmarReserva` | No comprueba que las fechas sigan libres (caso 13). | Antes de confirmar, comprobar que no se superponga con otra reserva CONFIRMADA del mismo alojamiento, excluyendo la propia. `existeSuperposicion` no sirve tal cual: cuenta PENDIENTE y la propia reserva. Hace falta una variante nueva. |
| 3 | `ClienteDAO.eliminarCliente` | No comprueba reservas asociadas (caso 22). | Rechazar con mensaje claro si el cliente tiene filas en `cliente_reserva`. |
| 4 | `ServicioDAO.eliminarServicio` | No borra antes `alojamiento_servicio`; falla por clave foránea si está asociado. No cierra conexiones. | Borrar las asociaciones y el servicio en una transacción, con try-with-resources. Alternativa: bloquear si está en uso. |
| 5 | `AlojamientoDAO.eliminarAlojamiento` | No contempla reservas; las dos operaciones no son una transacción. | Bloquear la eliminación si el alojamiento tiene reservas. Si no, borrar asociaciones y alojamiento en una transacción. |
| 6 | `ReservaDao` (listados) | `Reserva` no trae el cliente; el panel necesita mostrarlo. | Consultas con JOIN a `cliente_reserva` y `cliente` que devuelvan nombre y documento. |
| 7 | `ReservaDao` / creación admin | La reserva del admin nace PENDIENTE, así que hay que confirmarla aparte, y la reserva y el vínculo con el cliente no son atómicos. | Permitir crearla directamente CONFIRMADA y hacer reserva + `cliente_reserva` en una transacción. |
| 8 | `Administrador` | Constructor asignaba `contrasenia = contrasenia` (ya corregido). | Hecho. |
| 9 | `AdministradorDAO.iniciarSesion` | Compara la contraseña en texto plano dentro del SQL. | Buscar por email y verificar con BCrypt; límite de intentos y bloqueo temporal. |
| 10 | `ClienteDAO.registrarCliente` | Pide contraseña del cliente. | Variante para el admin que use la contraseña por defecto (ver sección 3). |

## 2. Casos de uso corregidos

**1. Iniciar sesión.** Flujo: el administrador entra a la dirección reservada del panel, ingresa correo y contraseña. El sistema busca por correo, verifica el hash BCrypt y abre una sesión de administrador separada de la de clientes. Tras varios intentos fallidos seguidos, bloquea la cuenta unos minutos. Mensaje de error genérico. Postcondición: administrador autenticado; la sesión expira por inactividad.

**3. Eliminar alojamiento.** Precondición adicional: el alojamiento no debe tener reservas. Flujo: el administrador selecciona el alojamiento y confirma. El sistema comprueba que no tenga reservas; si las tiene, informa y no elimina. Si no, borra sus servicios asociados y el alojamiento en una sola operación.

**7. Eliminar servicio.** Flujo: el administrador selecciona el servicio y confirma. Si está asociado a alojamientos, el sistema lo informa y pide confirmar que también se quitarán esas asociaciones (o bloquea la eliminación). Postcondición: el servicio y sus asociaciones dejan de existir, sin errores de integridad.

**12. Crear reserva (incluye reservas por otros medios de contacto).** Actor: administrador. Descripción: permite registrar una reserva de un cliente que reservó por teléfono, WhatsApp u otro medio, bloqueando así las fechas. Precondición: el cliente y el alojamiento existen (si el cliente no existe, se registra antes con el caso 17). Flujo: elige cliente y alojamiento, ingresa fechas y huéspedes, y elige el estado inicial: CONFIRMADA (reserva ya acordada, bloquea las fechas) o PENDIENTE (solicitud a decidir). El sistema valida fechas, capacidad y que no haya superposición, y guarda la reserva y su vínculo con el cliente en una sola operación. Postcondición: reserva guardada; si es CONFIRMADA, las fechas quedan ocupadas.

**13. Confirmar reserva.** Precondición: la reserva está PENDIENTE y sus fechas no se superponen con otra reserva CONFIRMADA del mismo alojamiento. Flujo: selecciona la solicitud y confirma. El sistema vuelve a comprobar la disponibilidad; si hay superposición, informa y no confirma. Postcondición: reserva CONFIRMADA y fechas ocupadas.

**14. Cancelar reserva.** Precondición: la reserva existe y está PENDIENTE o CONFIRMADA. Flujo: selecciona la reserva y confirma. Postcondición: estado CANCELADA; si estaba confirmada, las fechas quedan libres (la disponibilidad se calcula con las confirmadas).

**17. Registrar cliente.** Flujo: el administrador ingresa nombre, documento, teléfono, correo y procedencia. El sistema valida cédula y celular, comprueba que correo y documento no existan, y crea la cuenta con una contraseña por defecto (almacenada con hash). Postcondición: cliente registrado; al ingresar por primera vez debe cambiar la contraseña por defecto (ver sección 3).

**21. Cerrar sesión.** Flujo: el administrador pulsa cerrar sesión; el sistema invalida la sesión de administrador y lo lleva a la pantalla de ingreso del panel. Postcondición: no puede acceder a ninguna página del panel sin volver a autenticarse.

**22. Eliminar cliente.** Flujo: selecciona el cliente y confirma. El sistema comprueba que exista y que no tenga reservas asociadas; si las tiene, informa que no se puede eliminar. Si no, lo elimina.

**23. Buscar reserva por cliente.** Se corrige el texto: el administrador ingresa nombre o documento del cliente y el sistema muestra las reservas asociadas. (El flujo original hablaba de buscar por ID de reserva; el código y el título buscan por cliente.)

## 3. Contraseña por defecto de los clientes registrados por el administrador

La contraseña por defecto es `alojamientosguaviyu`, igual para todos, según lo definido.

Riesgo: cualquiera que conozca el correo de un cliente y la contraseña por defecto (que queda escrita en el repositorio) puede entrar a su cuenta hasta que la cambie. Mitigación recomendada: una columna `debe_cambiar_contrasena` (no es una tabla nueva) en `cliente`, que se marca al crear la cuenta desde el panel y fuerza a cambiar la contraseña en el primer ingreso. Requiere una pantalla de "cambiar contraseña" para el cliente, que hoy no existe.
