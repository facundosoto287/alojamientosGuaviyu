<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alojamientos | Termas de Guaviyú</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alojamientos.css">
</head>
<body>

<%@ include file="components/header.jsp" %>

<main class="contenido">
    <section class="intro">
        <h1>Alojamientos</h1>
        <p>Conocé las opciones disponibles para tu estadía.</p>
    </section>

    <c:choose>
        <c:when test="${not empty alojamientos}">
            <section class="grid" aria-label="Listado de alojamientos">
                <c:forEach var="alojamiento" items="${alojamientos}">
                    <article class="card">
                        <h2><c:out value="${alojamiento.nombre}"/></h2>
                        <p class="description"><c:out value="${alojamiento.descripcion}"/></p>
                        <div class="facts">
                            <div class="fact"><span>Capacidad</span><strong><c:out value="${alojamiento.capacidad}"/> personas</strong></div>
                            <div class="fact"><span>Precio</span><strong><c:out value="${alojamiento.precio}"/></strong></div>
                            <div class="fact"><span>Ubicación</span><strong><c:out value="${alojamiento.ubicacion}"/></strong></div>
                            <div class="fact"><span>Distancia a piscina</span><strong><c:out value="${alojamiento.distanciaApiscina}"/> m</strong></div>
                        </div>
                    </article>
                </c:forEach>
            </section>
        </c:when>
        <c:otherwise>
            <p class="empty">Todavía no hay alojamientos para mostrar.</p>
        </c:otherwise>
    </c:choose>
</main>

<footer class="site-footer">Termas de Guaviyú</footer>
<%@ include file="components/footer.jsp" %>
</body>
</html>
