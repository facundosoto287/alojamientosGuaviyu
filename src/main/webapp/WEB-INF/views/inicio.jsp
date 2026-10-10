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

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Inicio | Alojamientos Guaviyú</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/inicio.css">
</head>

<body>
<%@ include file="components/header.jsp" %>

<section class="banner">
    <div class="banner-content">
        <h1>Tu descanso te espera en Guaviyú</h1>
        <p>
            Aquí vas a encontrar el alojamiento ideal para disfrutar de las Termas de Guaviyú
        </p>
    </div>
</section>

<section class="alojamientos-inicio">

       <form class="buscador-inicio"
             action="${pageContext.request.contextPath}/inicio"
             method="get">

           <div class="campo-buscador">
               <label for="fechaIngreso">Check-in</label>
               <input type="date"
               id="fechaIngreso"
               name="fechaIngreso"
               value="${param.fechaIngreso}"
               required>
           </div>

           <div class="campo-buscador">
               <label for="fechaSalida">Check-out</label>
               <input type="date"
               id="fechaSalida"
               name="fechaSalida"
               value="${param.fechaSalida}"
               required>
           </div>

           <div class="campo-buscador">
               <label for="huespedes">Huéspedes</label>
               <input type="number"
               id="huespedes"
               name="huespedes"
               min="1"
               placeholder="2"
               value="${param.huespedes}"
               required>
           </div>

           <div class="campo-buscador">
               <label for="precioMax">Precio máximo</label>
               <input type="number"
               id="precioMax"
               name="precioMax"
               min="0"
               placeholder="$"
               value="${param.precioMax}"
               required>

           </div>

           <button type="submit" class="boton-buscar">
               Buscar alojamientos
           </button>
           <a class="boton-limpiar"
              href="${pageContext.request.contextPath}/inicio">
               Limpiar filtros
           </a>

       </form>

    <h2>Conocé nuestros alojamientos</h2>
    <p>¡Las mejores opciones para disfrutar tu estadía en las termas!</p>

    <div class="alojamientos-grid">

        <c:forEach var="alojamiento" items="${alojamientos}">
            <article class="alojamiento-card">

                <div class="alojamiento-card-imagen">
                    <img src="${pageContext.request.contextPath}/img/alojamiento-default.jpg"
                         alt="Imagen de ${alojamiento.nombre}">
                </div>

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

                    <div class="servicios-card">

                        <c:set var="servicios"
                               value="${serviciosPorAlojamiento[alojamiento.id]}" />
                        <c:forEach var="servicio"
                                   items="${servicios}"
                                   begin="0"
                                   end="3">

                          <span class="servicio-item">
                              <c:out value="${servicio.nombre}" />
                          </span>
                        </c:forEach>
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
</section>
<script src="${pageContext.request.contextPath}/js/inicio.js"></script>
</body>

