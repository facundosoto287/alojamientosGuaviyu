<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cambiar contraseña | Alojamientos Guaviyú</title>
    <%@ include file="/WEB-INF/views/components/favicon.jsp" %>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/header.css">
    <link rel="stylesheet" href="${ctx}/css/login.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/loading.css">
    <style>
        .cambio-wrap { min-height: calc(100vh - 100px); display: grid; place-items: center; padding: 2rem 1.25rem; }
        .cambio-card { width: 100%; max-width: 460px; }
        .cambio-card h1 { margin: 0 0 .4rem; font-size: 1.6rem; }
        .cambio-card .aviso-info { margin: 1rem 0 0; padding: .75rem .9rem; border-radius: 10px;
            background: var(--azul-100); color: var(--azul-900); font-weight: 600; font-size: .92rem; }
    </style>
</head>
<body class="pagina-login">

<%@ include file="components/header.jsp" %>

<main class="cambio-wrap">

<div class="volver-perfil-contenedor">
            <div class="volver-perfil-inner">
                <a class="boton-volver"
                   href="${ctx}/perfil">
                    ← Volver a mi perfil
                </a>
            </div>
        </div>
    <section class="auth-card cambio-card">

        <h1>Cambiar contraseña</h1>
        <p class="muted">Elegí una contraseña nueva para tu cuenta.</p>

        <c:if test="${obligatorio}">
            <p class="aviso-info" role="status">
                Tu cuenta fue creada con una contraseña inicial. Por seguridad, tenés que cambiarla antes de seguir.
            </p>
        </c:if>

        <c:if test="${not empty error}">
            <p class="alerta" role="alert"><c:out value="${error}"/></p>
        </c:if>

        <form action="${ctx}/cambiar-contrasena" method="post" novalidate>
        <input type="hidden" name="origen" value="${param.origen}">

            <label for="cc-actual">Contraseña actual</label>
            <div class="input-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 11h12v9H6zM8.5 11V8a3.5 3.5 0 0 1 7 0v3"/></svg>
                <input type="password" id="cc-actual" name="actual" autocomplete="current-password" required>
            </div>

            <label for="cc-nueva">Contraseña nueva</label>
            <div class="input-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 11h12v9H6zM8.5 11V8a3.5 3.5 0 0 1 7 0v3"/></svg>
                <input type="password" id="cc-nueva" name="nueva" placeholder="Mínimo 8 caracteres"
                       autocomplete="new-password" minlength="8" required>
            </div>

            <label for="cc-confirmacion">Repetí la contraseña nueva</label>
            <div class="input-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 11h12v9H6zM8.5 11V8a3.5 3.5 0 0 1 7 0v3"/></svg>
                <input type="password" id="cc-confirmacion" name="confirmacion" autocomplete="new-password" required>
            </div>

            <button type="submit" class="btn-primario">Guardar contraseña <span aria-hidden="true">→</span></button>
        </form>
    </section>
</main>
<%@ include file="/WEB-INF/views/components/loading.jsp" %>
<script src="${pageContext.request.contextPath}/js/loading.js"></script>
<%@ include file="components/footer.jsp" %>
</body>
</html>
