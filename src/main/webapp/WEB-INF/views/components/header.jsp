<%--
  Created by IntelliJ IDEA.
  User: facuw
  Date: 9/10/2026
  Time: 10:26
  To change this template use File | Settings | File Templates.
--%>


<%@ page pageEncoding="UTF-8" %>

<header class="site-header">
    <div class="header-container">

        <a href="${pageContext.request.contextPath}/alojamientos" class="brand">
            Alojamientos Guaviyú
        </a>

        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/inicio">Inicio</a>
            <a href="${pageContext.request.contextPath}/alojamientos">Alojamientos</a>
            <a href="${pageContext.request.contextPath}/mapa">Mapa</a>
            <a href="${pageContext.request.contextPath}/mis-reservas">Mis reservas</a>
            <a href="${pageContext.request.contextPath}/sobre-nosotros">Sobre nosotros</a>
        </nav>

        <div class="header-user">
            <a href="${pageContext.request.contextPath}/cliente/login">
                Iniciar sesión
            </a>
        </div>

    </div>
</header>
