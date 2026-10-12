<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<header class="site-header">
<link href="https://fonts.googleapis.com/css2?family=Libre+Baskerville:wght@400;700&display=swap" rel="stylesheet">
    <div class="header-container">

    <a href="${ctx}/inicio" class="brand">
        <img src="${ctx}/img/logoAlojamientosGuaviyu.png"
             alt="Alojamientos Guaviyú"
             class="logo-header-img">

        <div class="brand-texto">
            <span>Alojamientos</span>
            <span>Guaviyú</span>
        </div>
    </a>

        <nav class="main-nav">
            <a href="${ctx}/inicio">Inicio</a>
            <a href="${ctx}/mapa">Mapa</a>
            <a href="${ctx}/mis-reservas">Mis reservas</a>
            <a href="${ctx}/sobre-nosotros">Sobre nosotros</a>
        </nav>

        <div class="header-user">
            <c:choose>
                <c:when test="${not empty sessionScope.clienteSesion}">
                    <span class="header-saludo">Hola, <c:out value="${sessionScope.clienteSesion.primerNombre}"/></span>
                    <a href="${ctx}/cambiar-contrasena">Cambiar contraseña</a>
                    <form action="${ctx}/logout" method="post" class="logout-form">
                        <button type="submit" class="link-button">Cerrar sesión</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <a href="${ctx}/login">Iniciar sesión</a>
                </c:otherwise>
            </c:choose>
        </div>

    </div>
</header>
