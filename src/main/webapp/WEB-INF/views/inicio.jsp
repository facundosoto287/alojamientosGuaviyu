<%--
  Created by IntelliJ IDEA.
  User: facuw
  Date: 9/10/2026
  Time: 11:07
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="es">
<head>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Inicio | Alojamientos Guaviyú</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,500;1,500&family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/footer.css">
      <link rel="stylesheet"
            href="${pageContext.request.contextPath}/css/inicio.css?v=2">
</head>

<body>
<%@ include file="components/header.jsp" %>

<section class="banner">
    <div class="banner-content">

        <h1>El confort y la tranquilidad en un solo lugar </h1>

        <%-- Características destacadas del destino --%>
        <div class="banner-detalles">

            <%-- A metros de las termas --%>
            <div class="banner-detalle detalle-azul">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M3 16c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0"/>
                    <path d="M3 20c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0"/>
                    <path d="M8 14V7a3 3 0 0 1 6 0"/>
                </svg>

                <span>A metros de las termas</span>
            </div>


            <%-- Naturaleza --%>
            <div class="banner-detalle detalle-verde">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M5 19C5 10 11 4 20 4c0 9-6 15-15 15z"/>
                    <path d="M5 19l8-8"/>
                </svg>

                <span>Naturaleza todo el año</span>
            </div>


            <%-- Familias, parejas y grupos --%>
            <div class="banner-detalle detalle-naranja">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M9 11a3 3 0 1 0 0-6 3 3 0 0 0 0 6"/>
                    <path d="M17 11a2.5 2.5 0 1 0 0-5"/>
                    <path d="M3 19c0-3 2.7-5 6-5s6 2 6 5"/>
                    <path d="M16 14c2.8 0 5 1.6 5 4"/>
                </svg>

                <span>Alojamientos para familias y parejas </span>
            </div>

        </div>

    </div>
</section>

<section class="alojamientos-inicio">
<div class="alojamientos-contenido">

       <form class="buscador-inicio"
             action="${pageContext.request.contextPath}/inicio"
             method="get">

           <div class="campo-buscador">
               <label for="fechaIngreso">Check-in</label>

               <div class="input-con-icono">
                   <svg viewBox="0 0 24 24" aria-hidden="true">
                       <rect x="3" y="5" width="18" height="16" rx="2"/>
                       <path d="M8 3v4"/>
                       <path d="M16 3v4"/>
                       <path d="M3 10h18"/>
                   </svg>

                   <input type="text"
                          id="fechaIngreso"
                          name="fechaIngreso"
                          value="${param.fechaIngreso}"
                          placeholder="Seleccionar fecha"
                          autocomplete="off"
                          required>
               </div>
           </div>

           <div class="campo-buscador">
               <label for="fechaSalida">Check-out</label>

               <div class="input-con-icono">
                   <svg viewBox="0 0 24 24" aria-hidden="true">
                       <rect x="3" y="5" width="18" height="16" rx="2"/>
                       <path d="M8 3v4"/>
                       <path d="M16 3v4"/>
                       <path d="M3 10h18"/>
                   </svg>

                   <input type="text"
                           id="fechaSalida"
                           name="fechaSalida"
                           value="${param.fechaSalida}"
                           placeholder="Seleccionar fecha"
                           autocomplete="off"
                           required>
               </div>
           </div>

           <div class="campo-buscador">
               <label for="huespedes">Huéspedes</label>

               <div class="input-con-icono">
                   <svg viewBox="0 0 24 24" aria-hidden="true">
                       <circle cx="12" cy="8" r="3"/>
                       <path d="M6 20c0-3.5 2.7-6 6-6s6 2.5 6 6"/>
                   </svg>

                   <input type="number"
                          id="huespedes"
                          name="huespedes"
                          min="1"
                          placeholder="2"
                          value="${param.huespedes}"
                          required>
               </div>
           </div>

           <div class="campo-buscador">
               <label for="precioMax">Precio máximo</label>

               <div class="input-con-icono">
                   <svg viewBox="0 0 24 24" aria-hidden="true">
                       <path d="M20 13l-7 7-9-9V4h7z"/>
                       <circle cx="8.5" cy="8.5" r="1.5"/>
                   </svg>

                   <input type="number"
                          id="precioMax"
                          name="precioMax"
                          min="0"
                          placeholder="$"
                          value="${param.precioMax}"
                          required>
               </div>
           </div>

           <button type="submit" class="boton-buscar">
               <svg viewBox="0 0 24 24" aria-hidden="true">
                   <circle cx="11" cy="11" r="7"/>
                   <path d="M20 20l-4-4"/>
               </svg>

               Buscar alojamientos
           </button>

           <a class="boton-limpiar"
              href="${pageContext.request.contextPath}/inicio">

               <svg viewBox="0 0 24 24" aria-hidden="true">
                   <path d="M4 4v6h6"/>
                   <path d="M20 11a8 8 0 1 0-2.3 5.7"/>
               </svg>

               Limpiar filtros
           </a>
       </form>

    <div class="alojamientos-titulo">
        <span class="titulo-etiqueta">Alojamientos Guaviyú</span>

        <h2>Conocé nuestros alojamientos</h2>

        <p>¡Las mejores opciones para disfrutar tu estadía en las termas!</p>
    </div>

    <div class="alojamientos-grid">

        <c:forEach var="alojamiento" items="${alojamientos}">
            <article class="alojamiento-card">


            <%-- Imagen fija para el alojamiento 6 --%>
            <c:if test="${alojamiento.id == 5}">
                <div class="alojamiento-card-imagen">
                    <img src="${pageContext.request.contextPath}/img/grandiosa1.jpg"
                         alt="Imagen de ${alojamiento.nombre}">
                </div>
            </c:if>

            <%-- Imagen por defecto para los demás --%>
            <c:if test="${alojamiento.id != 5}">
                <div class="alojamiento-card-imagen">
                    <img src="${pageContext.request.contextPath}/img/alojamiento-default.jpg"
                         alt="Imagen de ${alojamiento.nombre}">
                </div>
            </c:if>

                <div class="alojamiento-card-contenido">
                    <h3>
                        <c:out value="${alojamiento.nombre}"/>
                    </h3>

                    <p class="descripcion">
                        <c:choose>
                            <c:when test="${fn:length(alojamiento.descripcion) > 90}">
                                <c:out value="${fn:substring(alojamiento.descripcion, 0, 90)}"/>...
                            </c:when>

                            <c:otherwise>
                                <c:out value="${alojamiento.descripcion}"/>
                            </c:otherwise>
                        </c:choose>
                    </p>


                    <%--logos de los servicios--%>
                    <div class="servicios-card">

                        <%-- Capacidad --%>
                        <span class="servicio-item">
                            <svg viewBox="0 0 24 24" aria-hidden="true">
                                <path d="M9 11a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm8 0a2.5 2.5 0 1 0 0-5M3 19c0-3 2.7-5 6-5s6 2 6 5M16 14c2.8 0 5 1.6 5 4"/>
                            </svg>

                            <c:out value="${alojamiento.capacidad}"/> personas
                        </span>

                        <%-- Servicios asociados al alojamiento --%>
                        <c:set var="servicios"
                               value="${serviciosPorAlojamiento[alojamiento.id]}" />

                        <c:forEach var="servicio"
                                   items="${servicios}"
                                   begin="0"
                                   end="2">

                            <span class="servicio-item">

                                <c:choose>

                                    <%-- Wifi --%>
                                    <c:when test="${servicio.nombre == 'Wifi'}">
                                        <svg viewBox="0 0 24 24" aria-hidden="true">
                                            <path d="M5 12.5a10 10 0 0 1 14 0"/>
                                            <path d="M8 16a6 6 0 0 1 8 0"/>
                                            <path d="M11 19a2 2 0 0 1 2 0"/>
                                        </svg>
                                    </c:when>

                                    <%-- Vajilla --%>
                                    <c:when test="${servicio.nombre == 'Vajilla'}">
                                        <svg viewBox="0 0 24 24" aria-hidden="true">
                                            <circle cx="10" cy="12" r="5"/>
                                            <path d="M17 4v16"/>
                                            <path d="M19 4v6"/>
                                            <path d="M15 4v6"/>
                                        </svg>
                                    </c:when>

                                    <%-- Aire acondicionado --%>
                                    <c:when test="${servicio.nombre == 'Aire acondicionado'}">
                                        <svg viewBox="0 0 24 24" aria-hidden="true">
                                            <path d="M12 2v20"/>
                                            <path d="M4 7l16 10"/>
                                            <path d="M20 7L4 17"/>
                                        </svg>
                                    </c:when>

                                    <%-- Parrilla --%>
                                    <c:when test="${servicio.nombre == 'Parrilla'}">
                                        <svg viewBox="0 0 24 24" aria-hidden="true">
                                            <path d="M5 10h14"/>
                                            <path d="M7 10c0 5 2 7 5 7s5-2 5-7"/>
                                            <path d="M9 17l-2 4"/>
                                            <path d="M15 17l2 4"/>
                                            <path d="M9 5c0-1 1-2 1-3"/>
                                            <path d="M14 5c0-1 1-2 1-3"/>
                                        </svg>
                                    </c:when>

                                    <%-- Estacionamiento --%>
                                    <c:when test="${servicio.nombre == 'Estacionamiento'}">
                                        <svg viewBox="0 0 24 24" aria-hidden="true">
                                            <rect x="4" y="3" width="16" height="18" rx="2"/>
                                            <path d="M9 17V7h4a3 3 0 0 1 0 6H9"/>
                                            <path d="M9 13h4"/>
                                        </svg>
                                    </c:when>

                                    <%-- Ropa de cama --%>
                                    <c:when test="${servicio.nombre == 'Ropa de cama'}">
                                        <svg viewBox="0 0 24 24" aria-hidden="true">
                                            <path d="M3 18V9"/>
                                            <path d="M3 14h18"/>
                                            <path d="M21 18V11"/>
                                            <path d="M6 9h5v5H6z"/>
                                            <path d="M11 10h7a3 3 0 0 1 3 3v1"/>
                                        </svg>
                                    </c:when>

                                    <%-- Televisión --%>
                                    <c:when test="${servicio.nombre == 'Televisión'}">
                                        <svg viewBox="0 0 24 24" aria-hidden="true">
                                            <rect x="3" y="6" width="18" height="12" rx="2"/>
                                            <path d="M9 21h6"/>
                                            <path d="M12 18v3"/>
                                        </svg>
                                    </c:when>

                                    <%-- Servicio no reconocido --%>
                                    <c:otherwise>
                                        <svg viewBox="0 0 24 24" aria-hidden="true">
                                            <circle cx="12" cy="12" r="8"/>
                                            <path d="M8 12l3 3 5-6"/>
                                        </svg>
                                    </c:otherwise>

                                </c:choose>

                                <c:out value="${servicio.nombre}"/>

                            </span>

                        </c:forEach>

                        <%-- Distancia a la piscina --%>
                        <span class="servicio-item">
                            <svg viewBox="0 0 24 24" aria-hidden="true">
                                <path d="M3 16c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0"/>
                                <path d="M3 20c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0"/>
                                <path d="M8 14V7a3 3 0 0 1 6 0"/>
                            </svg>
                            <c:out value="${alojamiento.distanciaApiscina}"/> m de la piscina
                        </span>

                    </div>

                    <div class="card-footer">
                        <div class="precio-card">
                            <strong>
                                $<fmt:formatNumber value="${alojamiento.precio}" pattern="#,##0"/> UY
                            </strong>
                            <span>por noche</span>
                        </div>
                        <a class="boton-detalle"
                           href="${pageContext.request.contextPath}/alojamiento-detalle?id=${alojamiento.id}">
                            Ver detalle
                        </a>

                    </div>
            </article>

        </c:forEach>

    </div>
</div>
</section>
<%@ include file="components/footer.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/l10n/es.js"></script>

<script src="${pageContext.request.contextPath}/js/inicio.js"></script>
</body>

