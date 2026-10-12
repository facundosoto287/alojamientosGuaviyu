<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="robots" content="noindex, nofollow">
    <title>Ingreso | Panel Guaviyú</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/admin.css">
</head>
<body class="admin admin-ingreso">

<main class="ingreso-caja">
    <img src="${ctx}/img/logoAlojamientosGuaviyu.png" alt="Alojamientos Guaviyú" class="ingreso-logo">
    <h1>Panel de administración</h1>
    <p class="muted">Acceso solo para personal autorizado.</p>

    <c:if test="${not empty error}">
        <p class="aviso aviso-error" role="alert"><span><c:out value="${error}"/></span></p>
    </c:if>

    <form action="${base}/login" method="post" autocomplete="off">
        <label for="adm-email">Correo electrónico</label>
        <input type="email" id="adm-email" name="email" maxlength="100" required autofocus
               value="${fn:escapeXml(emailEscrito)}">

        <label for="adm-pass">Contraseña</label>
        <input type="password" id="adm-pass" name="contrasena" required autocomplete="current-password">

        <button type="submit" class="btn btn-primario btn-ancho">Ingresar</button>
    </form>
</main>

</body>
</html>
