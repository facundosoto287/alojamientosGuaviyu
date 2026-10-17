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

        <button type="button"
                class="menu-hamburguesa"
                id="menuHamburguesa"
                aria-label="Abrir menú"
                aria-expanded="false">

            <span></span>
            <span></span>
            <span></span>

        </button>

        <div class="menu-responsive"
             id="menuNavegacion">

            <div class="menu-logo-mobile">

                <img src="${ctx}/img/logoAlojamientosGuaviyu.png"
                     alt="Alojamientos Guaviyú"
                     class="logo-menu-mobile">

                <div class="brand-texto-menu">
                    <span>Alojamientos</span>
                    <span>Guaviyú</span>
                </div>
            </div>


            <nav class="main-nav">

                <a href="${ctx}/inicio">

                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M3 11l9-8 9 8"/>
                        <path d="M5 10v10h14V10"/>
                        <path d="M9 20v-6h6v6"/>
                    </svg>

                    <span>Inicio</span>
                </a>


                <a href="${ctx}/mapa">

                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M9 18l-6 3V6l6-3 6 3 6-3v15l-6 3-6-3z"/>
                        <path d="M9 3v15"/>
                        <path d="M15 6v15"/>
                    </svg>

                    <span>Mapa</span>
                </a>


                <a href="${ctx}/sobre-nosotros">

                    <svg viewBox="0 0 24 24" aria-hidden="true">
                         <circle cx="12" cy="8" r="4"/>
                         <path d="M4 21c1.5-4 4-6 8-6s6.5 2 8 6"/>
                    </svg>

                    <span>Sobre nosotros</span>
                </a>

            </nav>


            <div class="header-user">

                <c:choose>

                    <c:when test="${not empty sessionScope.clienteSesion}">

                        <div class="header-usuario-dropdown">

                            <button type="button"
                                    class="header-usuario-boton"
                                    id="headerUsuarioBoton"
                                    aria-expanded="false">

                                <span class="header-usuario-nombre">
                                    Hola,
                                    <c:out value="${sessionScope.clienteSesion.primerNombre}"/>
                                </span>

                                <span class="header-usuario-icono">
                                    <svg viewBox="0 0 24 24" aria-hidden="true">
                                        <circle cx="12" cy="8" r="4"></circle>
                                        <path d="M4 21c0-4 3.6-7 8-7s8 3 8 7"></path>
                                    </svg>
                                </span>

                                <svg class="header-flecha"
                                     viewBox="0 0 24 24"
                                     aria-hidden="true">
                                    <path d="M6 9l6 6 6-6"></path>
                                </svg>

                            </button>

                             <div class="header-dropdown-menu">

                                 <a href="${ctx}/perfil"
                                    class="header-dropdown-item">

                                     <svg viewBox="0 0 24 24" aria-hidden="true">
                                         <circle cx="12" cy="8" r="4"></circle>
                                         <path d="M4 21c0-4 3.6-7 8-7s8 3 8 7"></path>
                                     </svg>

                                     Mi perfil

                                 </a>

                                 <div class="header-dropdown-separador"></div>

                                 <form action="${ctx}/logout"
                                       method="post"
                                       class="header-dropdown-logout-form">

                                     <button type="submit"
                                             class="header-dropdown-item header-dropdown-salir">

                                         Cerrar sesión

                                     </button>

                                 </form>

                             </div>

                        </div>

                    </c:when>


                    <c:otherwise>

                        <a href="${ctx}/login"
                           class="header-login-link">

                            <span class="header-usuario-icono">

                                <svg viewBox="0 0 24 24" aria-hidden="true">
                                    <circle cx="12" cy="8" r="4"></circle>
                                    <path d="M4 21c0-4 3.6-7 8-7s8 3 8 7"></path>
                                </svg>

                            </span>

                            <span>
                                Iniciar sesión
                            </span>

                        </a>

                    </c:otherwise>

                </c:choose>

            </div>

        </div>
    </div>
    <script src="${ctx}/js/header.js" defer></script>
</header>