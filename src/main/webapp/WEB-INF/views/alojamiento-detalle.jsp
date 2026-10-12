<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>${alojamiento.nombre} | Alojamientos Guaviyú</title>

    <!-- Libreria -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">

    <!-- Fuentes -->
    <link rel="preconnect" href="https://fonts.googleapis.com">

    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

    <link href="https://fonts.googleapis.com/css2?family=Libre+Baskerville:wght@400;700&family=Poppins:wght@400;600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">


    <!-- CSS propio -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alojamiento-detalle.css">


    <!-- CSS compartidos -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">

</head>

<body>

<%@ include file="components/header.jsp" %>


<main class="detalle-pagina">

<a href="${pageContext.request.contextPath}/inicio"
               class="boton-volver">

                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M15 18l-6-6 6-6"/>
                </svg>

                Volver al inicio
            </a>

    <div class="detalle-contenedor">

        <!-- PARTE IZQUIERDA -->

        <div class="detalle-principal">


            <section class="galeria-principal">

                <!-- Foto principal -->
                <div class="imagen-grande">
                    <img src="${pageContext.request.contextPath}/img/grandiosa1.jpg"
                         alt="Imagen principal de ${alojamiento.nombre}"
                         class="foto-galeria" data-indice="0">
                </div>


                <!-- Fotos secundarias -->
                <div class="imagenes-secundarias">

                    <div class="miniatura">
                        <img src="${pageContext.request.contextPath}/img/grandiosa3.jpg"
                             alt="Imagen de ${alojamiento.nombre}"
                             class="foto-galeria" data-indice="1">
                    </div>

                    <div class="miniatura">
                        <img src="${pageContext.request.contextPath}/img/grandiosa4.jpg"
                             alt="Imagen de ${alojamiento.nombre}"
                             class="foto-galeria" data-indice="2">
                    </div>

                    <div class="miniatura">
                        <img src="${pageContext.request.contextPath}/img/grandiosa5.jpg"
                             alt="Imagen de ${alojamiento.nombre}"
                             class="foto-galeria" data-indice="3">
                    </div>

                    <div class="miniatura">
                        <img src="${pageContext.request.contextPath}/img/grandiosa4.jpg"
                             alt="Imagen de ${alojamiento.nombre}"
                             class="foto-galeria" data-indice="4">
                    </div>


                    <!-- Quinta miniatura.
                         Si existen más imágenes, mostramos cuántas quedan -->
                    <div class="miniatura miniatura-mas">

                        <img src="${pageContext.request.contextPath}/img/grandiosa5.jpg"
                             alt="Más imágenes de ${alojamiento.nombre}">

                        <div class="miniatura-overlay">
                            +6
                        </div>

                    </div>

                </div>

            </section>


            <!-- Información del alojamiento -->
            <section class="informacion-alojamiento">

                <span class="detalle-etiqueta">
                    Alojamientos Guaviyú
                </span>

                <h1>${alojamiento.nombre}</h1>


                <!-- Ubicación -->
                <div class="detalle-ubicacion">

                    <svg viewBox="0 0 24 24"
                         aria-hidden="true">
                        <path d="M12 21s6-5.2 6-11a6 6 0 1 0-12 0c0 5.8 6 11 6 11z"/>
                        <circle cx="12" cy="10" r="2"/>
                    </svg>

                    <span>${alojamiento.ubicacion}</span>

                </div>


                <!-- Descripción completa -->
                <p class="detalle-descripcion">
                    ${alojamiento.descripcion}
                </p>


                <!-- Características principales -->
                <div class="detalle-caracteristicas">

                    <!-- Capacidad -->
                    <div class="caracteristica">

                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <circle cx="9" cy="8" r="3"/>
                            <circle cx="17" cy="9" r="2.5"/>
                            <path d="M3 19c0-3 2.7-5 6-5s6 2 6 5"/>
                            <path d="M16 14c2.8 0 5 1.6 5 4"/>
                        </svg>

                        <div>
                            <span>Hasta</span>
                            <strong>${alojamiento.capacidad} personas</strong>
                        </div>

                    </div>


                    <!-- Distancia a la piscina -->
                    <div class="caracteristica">

                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M3 16c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0"/>
                            <path d="M3 20c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0"/>
                            <path d="M8 14V7a3 3 0 0 1 6 0"/>
                        </svg>

                        <div>
                            <span>A</span>
                            <strong>${alojamiento.distanciaApiscina} m de la piscina</strong>
                        </div>

                    </div>

                </div>

                   <!-- Servicios incluidos -->
                   <section class="servicios-detalle">

                       <h2>Servicios incluidos</h2>

                       <div class="servicios-detalle-grid">

                       <c:forEach var="servicio" items="${servicios}">

                           <div class="servicio-detalle-item">

                               <c:choose>

                                   <c:when test="${servicio.nombre == 'Wifi'}">
                                       <svg viewBox="0 0 24 24" aria-hidden="true">
                                           <path d="M5 12.5a10 10 0 0 1 14 0"/>
                                           <path d="M8 16a6 6 0 0 1 8 0"/>
                                           <path d="M11 19a2 2 0 0 1 2 0"/>
                                       </svg>
                                   </c:when>

                                   <c:when test="${servicio.nombre == 'Aire acondicionado'}">
                                       <svg viewBox="0 0 24 24" aria-hidden="true">
                                           <path d="M12 2v20"/>
                                           <path d="M4 7l16 10"/>
                                           <path d="M20 7L4 17"/>
                                       </svg>
                                   </c:when>

                                   <c:when test="${servicio.nombre == 'Parrilla'}">
                                       <svg viewBox="0 0 24 24" aria-hidden="true">
                                           <path d="M5 10h14"/>
                                           <path d="M7 10c0 5 2 7 5 7s5-2 5-7"/>
                                           <path d="M9 17l-2 4"/>
                                           <path d="M15 17l2 4"/>
                                       </svg>
                                   </c:when>

                                   <c:when test="${servicio.nombre == 'Vajilla'}">
                                       <svg viewBox="0 0 24 24" aria-hidden="true">
                                           <circle cx="10" cy="12" r="5"/>
                                           <path d="M17 4v16"/>
                                           <path d="M19 4v6"/>
                                           <path d="M15 4v6"/>
                                       </svg>
                                   </c:when>

                                   <c:when test="${servicio.nombre == 'Ropa de cama'}">
                                       <svg viewBox="0 0 24 24" aria-hidden="true">
                                           <path d="M3 18V9"/>
                                           <path d="M3 14h18"/>
                                           <path d="M21 18V11"/>
                                           <path d="M6 9h5v5H6z"/>
                                       </svg>
                                   </c:when>

                                   <c:when test="${servicio.nombre == 'Televisión'}">
                                       <svg viewBox="0 0 24 24" aria-hidden="true">
                                           <rect x="3" y="6" width="18" height="12" rx="2"/>
                                           <path d="M9 21h6"/>
                                           <path d="M12 18v3"/>
                                       </svg>
                                   </c:when>

                                   <c:when test="${servicio.nombre == 'Estacionamiento'}">
                                       <svg viewBox="0 0 24 24" aria-hidden="true">
                                           <rect x="4" y="3" width="16" height="18" rx="2"/>
                                           <path d="M9 17V7h4a3 3 0 0 1 0 6H9"/>
                                           <path d="M9 13h4"/>
                                       </svg>
                                   </c:when>

                               </c:choose>

                               <span>
                                   <c:out value="${servicio.nombre}"/>
                               </span>

                           </div>

                       </c:forEach>

                   </div>

                   </section>

            </section>


            <!-- Ubicación -->
            <section class="ubicacion-seccion">

                <h2>Ubicación en el complejo</h2>

                <div class="ubicacion-contenido">

                    <div class="ubicacion-texto">

                        <p>
                            Este alojamiento se encuentra dentro del
                            complejo Termas de Guaviyú, en Paysandú.
                        </p>


                        <div class="ubicacion-tarjeta">

                            <svg viewBox="0 0 24 24"
                                 aria-hidden="true">
                                <path d="M12 21s6-5.2 6-11a6 6 0 1 0-12 0c0 5.8 6 11 6 11z"/>
                                <circle cx="12" cy="10" r="2"/>
                            </svg>

                            <div>
                                <strong>
                                    Complejo Termas de Guaviyú
                                </strong>

                                <span>
                                    Paysandú, Uruguay
                                </span>
                            </div>

                        </div>

                    </div>


                    <!-- Más adelante acá ponemos Leaflet -->
                    <div class="mapa-placeholder">

                        <svg viewBox="0 0 24 24"
                             aria-hidden="true">
                            <path d="M12 21s6-5.2 6-11a6 6 0 1 0-12 0c0 5.8 6 11 6 11z"/>
                            <circle cx="12" cy="10" r="2"/>
                        </svg>

                        <strong>Ubicación del alojamiento</strong>

                        <span>
                            Acá vamos a colocar el mapa interactivo
                        </span>

                    </div>

                </div>

            </section>

        </div>


        <!--COLUMNA DE RESERVA-->

        <aside class="reserva-panel">


            <!-- Precio -->
            <div class="reserva-precio">

                <strong>
                    $<fmt:formatNumber
                        value="${alojamiento.precio}"
                        pattern="#,##0"/> UY
                </strong>

                <span>por noche</span>

            </div>


            <!-- Disponibilidad -->
            <div class="disponibilidad">

                <h3>Disponibilidad</h3>


                <!-- Calendario -->
                <div class="calendario-reserva">
                    <input type="text"
                           id="calendarioReserva"
                           class="calendario-reserva-input"
                           aria-label="Seleccionar fechas de reserva">
                </div>

                <div id="mensajeReserva"
                     class="mensaje-reserva"
                     hidden>
                </div>

                <!-- Fechas ocupadas enviadas desde Java -->
                <div id="reservasConfirmadas" hidden>

                    <c:forEach var="reserva"
                               items="${reservasConfirmadas}">

                        <span class="reserva-confirmada"
                              data-ingreso="${reserva.fechaIngreso}"
                              data-salida="${reserva.fechaSalida}">
                        </span>

                    </c:forEach>

                </div>

                <!-- Referencias -->
                <div class="calendario-leyenda">

                    <div>
                        <span class="leyenda-color disponible"></span>
                        Disponible
                    </div>

                    <div>
                        <span class="leyenda-color seleccionado"></span>
                        Seleccionado
                    </div>

                    <div>
                        <span class="leyenda-color ocupado"></span>
                        Ocupado
                    </div>

                </div>

            </div>


            <!-- Formulario visual -->
            <div class="reserva-formulario">


                <div class="reserva-fechas">

                    <div class="reserva-campo">

                        <label for="fechaIngreso">
                            Fecha de ingreso
                        </label>

                        <input type="text"
                               id="fechaIngreso"
                               name="fechaIngreso"
                               placeholder="Seleccioná una fecha"
                               readonly>

                    </div>


                    <div class="reserva-campo">

                        <label for="fechaSalida">
                            Fecha de salida
                        </label>

                        <input type="text"
                               id="fechaSalida"
                               name="fechaSalida"
                               placeholder="Seleccioná una fecha"
                               readonly>

                    </div>

                </div>


                <div class="reserva-campo">

                    <label for="cantidadHuespedes">
                        Huéspedes
                    </label>

                    <input type="number"
                           id="cantidadHuespedes"
                           name="cantidadHuespedes"
                           min="1"
                           max="${alojamiento.capacidad}"
                           value="1">

                </div>


                <!-- Por ahora no envía nada -->
                <button type="button"
                        class="boton-solicitar">

                    Solicitar reserva

                </button>

            </div>

        </aside>

    </div>

</main>

<!-- Visor de imágenes -->
<div class="visor-imagen" id="visorImagen">

    <button type="button"
            class="visor-cerrar"
            id="cerrarVisor">
        ×
    </button>

    <button type="button"
            class="visor-flecha visor-anterior"
            id="imagenAnterior">
        ‹
    </button>

    <img id="imagenAmpliada"
         src=""
         alt="Imagen ampliada del alojamiento">

    <button type="button"
            class="visor-flecha visor-siguiente"
            id="imagenSiguiente">
        ›
    </button>

</div>

<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/es.js"></script>
<script src="${pageContext.request.contextPath}/js/alojamiento-detalle.js"></script>
<%@ include file="components/footer.jsp" %>
</body>
</html>