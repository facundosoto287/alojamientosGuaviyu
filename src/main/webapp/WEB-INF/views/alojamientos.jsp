<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
<link rel="stylesheet"
     href="${pageContext.request.contextPath}/css/header.css">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Alojamientos | Termas de Guaviyú</title>
    <style>
        :root { color-scheme: light; --ink: #18302b; --muted: #60736d; --green: #176b55; --line: #dce8e2; --paper: #fff; --page: #f3f7f4; }
        * { box-sizing: border-box; }
        body { margin: 0; background: var(--page); color: var(--ink); font: 16px/1.5 system-ui, "Segoe UI", sans-serif; }

        main { max-width: 1100px; margin: 0 auto; padding: 2.2rem 1.25rem 3rem; }
        .intro { margin-bottom: 1.5rem; }
        h1 { margin: 0 0 .35rem; font-size: clamp(1.8rem, 4vw, 2.5rem); }
        .intro p, .muted { color: var(--muted); }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 1rem; }
        .card { background: var(--paper); border: 1px solid var(--line); border-radius: 12px; padding: 1.25rem; box-shadow: 0 4px 14px #123c3209; }
        .card h2 { margin: 0 0 .55rem; font-size: 1.25rem; }
        .description { min-height: 3rem; color: var(--muted); }
        .facts { display: grid; gap: .45rem; margin-top: 1rem; padding-top: .9rem; border-top: 1px solid var(--line); }
        .fact { display: flex; justify-content: space-between; gap: 1rem; }
        .fact strong { text-align: right; }
        .empty { background: var(--paper); border: 1px dashed #aabdb4; border-radius: 12px; padding: 1.4rem; color: var(--muted); }
        footer { text-align: center; padding: 1rem; color: var(--muted); }
    </style>
</head>
<body>
<%@ include file="components/header.jsp" %>

<main>
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
                            <div class="fact"><span>Distancia a piscina</span><strong><c:out value="${alojamiento.distanciaApiscina}"/></strong></div>
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
<footer>Termas de Guaviyú</footer>
</body>
</html>
