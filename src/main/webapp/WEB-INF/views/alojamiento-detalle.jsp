<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>${alojamiento.nombre} | Alojamientos Guaviyú</title>

    <!-- Librerias -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">

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

                <c:set var="fotos" value="${alojamiento.listaImagenes}"/>
                <c:set var="totalFotos" value="${fn:length(fotos)}"/>

                <c:choose>

                    <%-- Fotos del alojamiento (Cloudinary): la primera es la principal y hasta 5 miniaturas.
                         Las que sobran quedan ocultas para poder recorrerlas con las flechas del visor. --%>
                    <c:when test="${totalFotos > 0 and not empty imagenBase}">

                        <div class="imagen-grande">
                            <img src="${imagenBase},w_1200,c_limit/${fotos[0]}"
                                 data-grande="${imagenBase},w_2000,c_limit/${fotos[0]}"
                                 alt="Imagen principal de <c:out value='${alojamiento.nombre}'/>"
                                 class="foto-galeria" data-indice="0">
                        </div>

                        <c:if test="${totalFotos > 1}">
                            <div class="imagenes-secundarias">

                                <c:forEach var="foto" items="${fotos}" begin="1" end="5" varStatus="posicion">
                                    <c:set var="hayMas" value="${posicion.index == 5 and totalFotos > 6}"/>
                                    <div class="miniatura ${hayMas ? 'miniatura-mas' : ''}">
                                        <img src="${imagenBase},w_400,h_300,c_fill/${foto}"
                                             data-grande="${imagenBase},w_2000,c_limit/${foto}"
                                             alt="Imagen ${posicion.index + 1} de <c:out value='${alojamiento.nombre}'/>"
                                             class="foto-galeria" data-indice="${posicion.index}">
                                        <c:if test="${hayMas}">
                                            <div class="miniatura-overlay" style="pointer-events:none">+${totalFotos - 6}</div>
                                        </c:if>
                                    </div>
                                </c:forEach>

                                <c:forEach var="foto" items="${fotos}" begin="6" varStatus="posicion">
                                    <img hidden
                                         src="${imagenBase},w_400,c_limit/${foto}"
                                         data-grande="${imagenBase},w_2000,c_limit/${foto}"
                                         alt="Imagen ${posicion.index + 1} de <c:out value='${alojamiento.nombre}'/>"
                                         class="foto-galeria" data-indice="${posicion.index}">
                                </c:forEach>

                            </div>
                        </c:if>

                    </c:when>

                    <%-- Sin fotos propias: las fijas de "La Grandiosa" (id 5) --%>
                    <c:when test="${alojamiento.id == 5}">

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

                    </c:when>

                    <%-- Sin fotos: imagen por defecto --%>
                    <c:otherwise>

                        <div class="imagen-grande">
                            <img src="${pageContext.request.contextPath}/img/alojamiento-default.svg"
                                 alt="Imagen de ${alojamiento.nombre}"
                                 class="foto-galeria" data-indice="0">
                        </div>

                    </c:otherwise>

                </c:choose>

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

                       <!-- MASCOTAS -->

                       <div class="servicio-detalle-item">

                           <svg viewBox="0 0 24 24" aria-hidden="true">
                               <circle cx="8" cy="7" r="2"/>
                               <circle cx="16" cy="7" r="2"/>
                               <circle cx="5" cy="12" r="2"/>
                               <circle cx="19" cy="12" r="2"/>
                               <path d="M8 18c1-4 7-4 8 0"/>
                           </svg>

                           <span>
                               <c:choose>
                                   <c:when test="${alojamiento.permiteMascotas}">
                                       Mascotas permitidas
                                   </c:when>

                                   <c:otherwise>
                                       No se permiten mascotas
                                   </c:otherwise>
                               </c:choose>
                           </span>

                       </div>

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


                    <c:choose>

                        <%-- El alojamiento tiene coordenadas: se muestra el mapa --%>
                        <c:when test="${not empty alojamiento.latitud and not empty alojamiento.longitud}">
                            <div class="mapa-alojamiento-caja">
                                <div id="mapaAlojamiento"
                                     class="mapa-alojamiento"
                                     data-lat="${alojamiento.latitud}"
                                     data-lng="${alojamiento.longitud}"
                                     aria-label="Mapa con la ubicación de ${alojamiento.nombre}">
                                </div>

                                <a href="${pageContext.request.contextPath}/mapa?alojamiento=${alojamiento.id}"
                                   class="mapa-alojamiento-enlace">
                                    Ver en el mapa completo
                                </a>
                            </div>
                        </c:when>

                        <%-- Todavía no se cargaron las coordenadas --%>
                        <c:otherwise>
                            <div class="mapa-placeholder">

                                <svg viewBox="0 0 24 24"
                                     aria-hidden="true">
                                    <path d="M12 21s6-5.2 6-11a6 6 0 1 0-12 0c0 5.8 6 11 6 11z"/>
                                    <circle cx="12" cy="10" r="2"/>
                                </svg>

                                <strong>Ubicación del alojamiento</strong>

                                <span>
                                    Todavía no está cargada la ubicación en el mapa
                                </span>

                            </div>
                        </c:otherwise>

                    </c:choose>

                </div>

            </section>


            <!-- ===== RESEÑAS ===== -->
            <section class="resenas-seccion" id="resenas">

                <h2>Reseñas</h2>


                <!-- Resumen: promedio + estrellas + cantidad -->
                <c:if test="${not empty resenas}">

                    <div class="resenas-resumen">

                        <strong>
                            <fmt:formatNumber value="${promedioResenas}"
                                              minFractionDigits="1"
                                              maxFractionDigits="1"/>
                        </strong>

                        <div class="estrellas">
                            <c:forEach begin="1" end="5" var="i">
                                <span class="${i <= promedioResenas + 0.5 ? 'activa' : ''}">★</span>
                            </c:forEach>
                        </div>

                        <span>
                            ${fn:length(resenas)} ${fn:length(resenas) == 1 ? 'reseña' : 'reseñas'}
                        </span>

                    </div>

                </c:if>


                <!-- Mensaje después de enviar el formulario -->
                <c:choose>

                    <c:when test="${param.resena == 'ok'}">
                        <div class="resena-aviso ok">
                            ¡Gracias! Tu reseña se publicó.
                        </div>
                    </c:when>

                    <c:when test="${param.resena == 'repetida'}">
                        <div class="resena-aviso error">
                            Ya dejaste tu reseña de tu última estadía. Vas a poder opinar de nuevo cuando vuelvas.
                        </div>
                    </c:when>

                    <c:when test="${param.resena == 'sin-estadia'}">
                        <div class="resena-aviso error">
                            Solo pueden opinar quienes ya se hospedaron en este alojamiento.
                        </div>
                    </c:when>

                    <c:when test="${param.resena == 'datos'}">
                        <div class="resena-aviso error">
                            Elegí de 1 a 5 estrellas (y un comentario de hasta 500 caracteres).
                        </div>
                    </c:when>

                </c:choose>


                <!-- Qué ve cada persona -->
                <c:choose>

                    <%-- No inició sesión --%>
                    <c:when test="${not logueado}">
                        <div class="resena-aviso">
                            <a href="${pageContext.request.contextPath}/login?destino=/alojamiento-detalle?id=${alojamiento.id}">Iniciá sesión</a>
                            para dejar tu reseña si ya te hospedaste en este alojamiento.
                        </div>
                    </c:when>

                    <%-- Tiene una estadía terminada que todavía no reseñó: formulario --%>
                    <c:when test="${puedeResenar}">

                        <form class="resena-formulario"
                              method="post"
                              action="${pageContext.request.contextPath}/resena">

                            <input type="hidden" name="alojamientoId" value="${alojamiento.id}">

                            <h3>Contanos cómo fue tu estadía</h3>

                            <!-- Van de 5 a 1 a propósito: el CSS las da vuelta -->
                            <div class="elegir-estrellas">
                                <c:forEach begin="1" end="5" var="i">
                                    <c:set var="valor" value="${6 - i}"/>
                                    <input type="radio"
                                           id="estrella${valor}"
                                           name="puntuacion"
                                           value="${valor}"
                                           required>
                                    <label for="estrella${valor}" title="${valor} estrellas">★</label>
                                </c:forEach>
                            </div>

                            <textarea name="comentario"
                                      maxlength="500"
                                      placeholder="¿Qué te gustó? ¿Qué mejorarías? (opcional)"></textarea>

                            <button type="submit" class="boton-solicitar boton-resena">
                                Publicar reseña
                            </button>

                        </form>

                    </c:when>

                    <%-- Ya opinó sobre su última estadía --%>
                    <c:when test="${tieneEstadia}">
                        <div class="resena-aviso">
                            Ya dejaste tu reseña de tu última estadía. ¡Gracias! Cuando vuelvas, vas a poder opinar de nuevo.
                        </div>
                    </c:when>

                    <%-- Inició sesión pero nunca terminó una estadía acá --%>
                    <c:otherwise>
                        <div class="resena-aviso">
                            Vas a poder dejar una reseña cuando termine tu estadía en este alojamiento.
                        </div>
                    </c:otherwise>

                </c:choose>


                <!-- Lista de reseñas -->
                <c:choose>

                    <c:when test="${empty resenas}">
                        <p class="resenas-vacio">
                            Este alojamiento todavía no tiene reseñas.
                        </p>
                    </c:when>

                    <c:otherwise>
                        <c:forEach var="resena" items="${resenas}">

                            <article class="resena-item">

                                <div class="resena-item-cabecera">
                                    <strong><c:out value="${resena.nombreCliente}"/></strong>
                                    <span><c:out value="${resena.fechaFormateada}"/></span>
                                </div>

                                <div class="estrellas">
                                    <c:forEach begin="1" end="5" var="i">
                                        <span class="${i <= resena.puntuacion ? 'activa' : ''}">★</span>
                                    </c:forEach>
                                </div>

                                <c:if test="${not empty resena.comentario}">
                                    <p><c:out value="${resena.comentario}"/></p>
                                </c:if>

                            </article>

                        </c:forEach>
                    </c:otherwise>

                </c:choose>

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

                        <div class="reserva-confirmada"
                             data-ingreso="${reserva.fechaIngreso}"
                             data-salida="${reserva.fechaSalida}"
                             data-medio-dia="${reserva.medioDiaExtra}">
                        </div>

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
            <form class="reserva-formulario"
                  action="${ctx}/solicitar-reserva"
                  method="get">

                <input type="hidden"
                       name="alojamientoId"
                       value="${alojamiento.id}">


                <div class="reserva-fechas">

                   <div class="reserva-campo">

                       <label for="fechaIngresoVisible">
                           Fecha de ingreso
                       </label>

                       <input type="text"
                              id="fechaIngresoVisible"
                              placeholder="Seleccioná una fecha"
                              readonly>

                       <input type="hidden"
                              id="fechaIngreso"
                              name="fechaIngreso">

                   </div>


                   <div class="reserva-campo">

                       <label for="fechaSalidaVisible">
                           Fecha de salida
                       </label>

                       <input type="text"
                              id="fechaSalidaVisible"
                              placeholder="Seleccioná una fecha"
                              readonly>

                       <input type="hidden"
                              id="fechaSalida"
                              name="fechaSalida">

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
                           value="1"
                           required>

                </div>


                <button type="submit" class="boton-solicitar">

                    Solicitar reserva

                </button>

            </form>

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

<!-- Mapa de la ubicación (Leaflet) -->
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script>
    (function () {
        const contenedor = document.getElementById("mapaAlojamiento");
        if (!contenedor) {
            return; // el alojamiento no tiene coordenadas
        }

        const posicion = [
            parseFloat(contenedor.dataset.lat),
            parseFloat(contenedor.dataset.lng)
        ];

        // scrollWheelZoom en false: así la ruedita baja la página y no hace zoom sin querer
        const mapa = L.map(contenedor, { scrollWheelZoom: false })
            .setView(posicion, 17);

        // Foto satelital, igual que en la página del mapa
        L.tileLayer("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}", {
            maxNativeZoom: 18, maxZoom: 20,
            attribution: "Imágenes &copy; Esri, Maxar, Earthstar Geographics"
        }).addTo(mapa);

        const icono = L.divIcon({
            className: "",
            html: '<div class="pin-alojamiento"><span>🏠</span></div>',
            iconSize: [44, 44],
            iconAnchor: [22, 44]
        });

        L.marker(posicion, { icon: icono }).addTo(mapa);
    })();

    const formularioReserva = document.querySelector(".reserva-formulario");

    formularioReserva.addEventListener("submit", (evento) => {

        if (!inputFechaIngreso.value || !inputFechaSalida.value) {

            evento.preventDefault();
            mensajeReserva.textContent = "Debes seleccionar una fecha en el calendario";
            mensajeReserva.hidden = false;

            return;
        }
    });

</script>
<%@ include file="components/footer.jsp" %>
</body>
</html>