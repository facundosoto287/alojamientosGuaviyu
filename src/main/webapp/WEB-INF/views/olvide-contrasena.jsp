<%--
  Created by IntelliJ IDEA.
  User: facuw
  Date: 31/10/2026
  Time: 02:22 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Recuperar contraseña | Alojamientos Guaviyú</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>

<body class="pagina-login">
<%@ include file="components/header.jsp" %>

<main class="auth-main">

    <section class="auth-card" style="max-width:520px; margin:50px auto; padding:32px;">

        <h2>Recuperar contraseña</h2>

        <p class="muted">
            Ingresá el correo electrónico asociado a tu cuenta.
            Te enviaremos un enlace para crear una nueva contraseña.
        </p>

        <c:if test="${not empty error}">
            <p class="alerta" role="alert">
                <c:out value="${error}"/>
            </p>
        </c:if>

        <c:if test="${not empty mensaje}">
            <p class="alerta-exito">
                <c:out value="${mensaje}"/>
            </p>
        </c:if>

        <form action="${pageContext.request.contextPath}/olvide-contrasena"
              method="post">

            <label for="email">
                Correo electrónico
            </label>

            <div class="input-icon">

                <input
                        type="email"
                        id="email"
                        name="email"
                        placeholder="tu@correo.com"
                        autocomplete="email"
                        maxlength="100"
                        required
                >

            </div>

            <button type="submit"
                    class="btn-primario">
                Enviar enlace
            </button>

        </form>

        <div style="
            text-align:center;
            margin-top:22px;
        ">

            <a href="${pageContext.request.contextPath}/login">
                Volver a iniciar sesión
            </a>

        </div>

    </section>

</main>
<%@ include file="/WEB-INF/views/components/loading.jsp" %>
<script src="${pageContext.request.contextPath}/js/loading.js"></script>
</body>
</html>
