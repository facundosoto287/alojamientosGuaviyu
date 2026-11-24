<%--
  Created by IntelliJ IDEA.
  User: facuw
  Date: 31/10/2026
  Time: 02:30 p.m.
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

    <title>Restablecer contraseña | Alojamientos Guaviyú</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap"
          rel="stylesheet">

    <link rel="stylesheet" href="${ctx}/css/header.css">
    <link rel="stylesheet" href="${ctx}/css/login.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/loading.css">
</head>

<body class="pagina-login">
<%@ include file="components/header.jsp" %>

<main class="auth-main">

    <section
            class="auth-card"
            style="
                max-width:520px;
                margin:50px auto;
                padding:32px;
            "
    >

        <h2>
            Crear nueva contraseña
        </h2>

        <p class="muted">
            Ingresá tu nueva contraseña y confirmala.
        </p>


        <c:if test="${not empty error}">

            <p
                    class="alerta"
                    role="alert"
            >
                <c:out value="${error}"/>
            </p>

        </c:if>


        <c:if test="${not empty token}">

            <form
                    action="${pageContext.request.contextPath}/restablecer-contrasena"
                    method="post"
            >

                <input
                        type="hidden"
                        name="token"
                        value="<c:out value='${token}'/>"
                >


                <label for="contrasena">
                    Nueva contraseña
                </label>

                <div class="input-icon">

                    <input
                            type="password"
                            id="contrasena"
                            name="contrasena"
                            autocomplete="new-password"
                            minlength="8"
                            required
                    >

                </div>


                <label for="confirmacion">
                    Confirmar contraseña
                </label>

                <div class="input-icon">

                    <input
                            type="password"
                            id="confirmacion"
                            name="confirmacion"
                            autocomplete="new-password"
                            minlength="8"
                            required
                    >

                </div>


                <button
                        type="submit"
                        class="btn-primario"
                >
                    Guardar nueva contraseña
                </button>

            </form>

        </c:if>


        <div
                style="
                    text-align:center;
                    margin-top:22px;
                "
        >

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
