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

## 4. Propuesta: fotos de los alojamientos (pendiente de decidir, sin programar)

### 4.1 Situación actual
- Ninguno de los 29 casos de uso del administrador cubre las fotos, así que se agregan los casos 30 a 32.
- El sitio público tiene las fotos escritas a mano en las JSP (`grandiosa1.jpg`, `grandiosa3.jpg`, etc. en `inicio.jsp` y `alojamiento-detalle.jsp`): todos los alojamientos muestran las mismas.
- `inicio.jsp` pide `img/alojamiento-default.jpg`, pero ese archivo no existe en `webapp/img`.

### 4.2 Casos de uso nuevos

**30. Agregar fotos a un alojamiento**
- Actor: administrador.
- Descripción: permite subir una o varias fotos al crear un alojamiento y también agregar fotos a uno que ya existe, para que los clientes las vean en el sitio.
- Precondición: el administrador está autenticado; para agregar a uno existente, el alojamiento existe.
- Flujo principal: el administrador abre el formulario de alojamiento (nuevo o edición), elige uno o más archivos y guarda. El sistema valida cada archivo (tipo real de imagen, tamaño máximo y cantidad máxima de fotos por alojamiento), los guarda con un nombre generado y los asocia al alojamiento.
- Flujos alternativos: si un archivo no es válido se informa cuál y los demás se guardan igual; si se supera el máximo de fotos se rechaza el excedente.
- Postcondición: las fotos quedan asociadas al alojamiento y se muestran en el sitio; la primera es la portada.

**31. Eliminar foto de un alojamiento**
- Actor: administrador. Precondición: autenticado y la foto existe.
- Flujo principal: el administrador abre el alojamiento, elige una foto, confirma. El sistema quita la referencia y borra el archivo.
- Postcondición: la foto deja de mostrarse en el sitio.

**32. Elegir la foto de portada**
- Actor: administrador. Precondición: el alojamiento tiene al menos dos fotos.
- Flujo principal: el administrador marca una foto como portada. El sistema la pasa al primer lugar.
- Postcondición: la portada se usa en el listado de inicio; el resto, en la galería del detalle.

**Efecto en el sitio público** (los clientes ven fotos, sin caso de uso nuevo): `inicio.jsp` muestra la portada y `alojamiento-detalle.jsp` la galería; si un alojamiento no tiene fotos se usa una imagen por defecto.

### 4.3 Dónde guardar las fotos (a decidir)

| Opción | Cómo | A favor | En contra |
|---|---|---|---|
| **A. Columna con nombres + archivos en una carpeta** | Columna `imagenes` (TEXT) con los nombres separados por `\|`; los archivos van a una carpeta fuera de la aplicación, configurable por variable de entorno. | Una sola columna, sin tablas nuevas; la base queda liviana; es la forma habitual cuando el sitio corre en un servidor. | Los archivos viven en el disco de quien ejecuta la aplicación: con la base remota compartida, una foto subida desde una PC no se ve en la de otro compañero hasta que se publique. No hay clave foránea. |
| **B. Tabla `alojamiento_imagen` con las fotos dentro de la base** | Una fila por foto, con el contenido en MEDIUMBLOB y el orden. | Las fotos viajan con la base, se ven iguales para todo el equipo; integridad referencial; portada y orden son una columna. | Es una tabla nueva; la base engorda; hay que servir las fotos desde un servlet que lea la base. |
| **C. Una sola columna MEDIUMBLOB** | Solo una foto por alojamiento. | La más simple. | No permite varias fotos. |

**Recomendación:** A, porque pediste una sola columna y no más tablas, y es lo correcto cuando se publique. Para que el equipo no vea imágenes rotas mientras tanto, la aplicación mostraría la imagen por defecto si el archivo no está en esa PC. Si preferís que el equipo comparta las fotos ya mismo, la B es la que lo resuelve.

### 4.4 Cambios previstos si se elige A
- Base de datos: `ALTER TABLE alojamiento ADD COLUMN imagenes TEXT NULL;`
- Variable de entorno nueva `ALOJAMIENTOS_IMG_DIR` (carpeta de fotos), junto a `ALOJAMIENTOS_DB_PASSWORD`.
- `Alojamiento` / `AlojamientoDAO`: campo `imagenes` y métodos para agregar, quitar y reordenar nombres; al eliminar un alojamiento se borran sus archivos.
- Panel: selector de archivos en "Agregar" y "Editar" alojamiento, miniaturas con botón eliminar y "usar de portada".
- Un servlet público que entrega las fotos (`/imagenes/{nombre}`) con el tipo correcto y caché.
- `inicio.jsp` y `alojamiento-detalle.jsp`: leer las fotos del alojamiento en lugar de las escritas a mano; agregar `alojamiento-default.jpg`.
- Alojamientos existentes: quedan sin fotos (imagen por defecto) hasta que se les suban.

### 4.5 Reglas de seguridad para las fotos
- Aceptar solo JPG, PNG y WebP, comprobando el contenido real del archivo (no solo la extensión); rechazar SVG.
- Tamaño máximo por foto (propuesta: 5 MB) y máximo por alojamiento (propuesta: 8 fotos).
- Nombres generados por el sistema (UUID): nunca se usa el nombre que manda el usuario, para evitar rutas maliciosas y choques.
- La carpeta de fotos queda fuera de la aplicación, para que no se pierda al volver a desplegar ni se pueda ejecutar nada subido.
- La subida va protegida por el mismo token anti-CSRF del panel.
- Las carpetas de fotos no se suben a Git (`.gitignore`).

### 4.6 Preguntas abiertas
1. ¿Opción A, B o C?
2. ¿Máximo de fotos por alojamiento y tamaño máximo? (propuesta: 8 y 5 MB)
3. ¿Hace falta el caso 32 (portada), o alcanza con que la primera subida sea la portada?
