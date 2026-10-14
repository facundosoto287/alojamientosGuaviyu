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
                        <path d="M3 10.5 12 3l9 7.5"/>
                        <path d="M5 9.5V21h14V9.5"/>
                    </svg>

                    <span>Inicio</span>
                </a>


                <a href="${ctx}/mapa">

                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M12 21s6-5.7 6-11a6 6 0 1 0-12 0c0 5.3 6 11 6 11z"/>
                        <circle cx="12" cy="10" r="2.5"/>
                    </svg>

                    <span>Mapa</span>
                </a>


                <a href="${ctx}/sobre-nosotros">

                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <circle cx="12" cy="12" r="9"/>
                        <path d="M12 10v6"/>
                        <circle cx="12" cy="7" r="1"/>
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