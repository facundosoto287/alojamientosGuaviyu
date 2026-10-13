<%--
  Created by IntelliJ IDEA.
  User: facuw
  Date: 11/10/2026
  Time: 15:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page pageEncoding="UTF-8" %>

<footer class="site-footer">
    <div class="footer-contenido">

        <!-- Marca -->
        <div class="footer-marca">
            <div class="footer-logo-bloque">
                <img src="${pageContext.request.contextPath}/img/logoAlojamientosGuaviyu.png"
                     alt="Alojamientos Guaviyú"
                     class="footer-logo">

                <div class="footer-nombre">
                    <span>Alojamientos</span>
                    <strong>Guaviyú</strong>
                </div>
            </div>

            <p>
                Encontrá el alojamiento ideal para disfrutar
                de tu estadía en las Termas de Guaviyú.
            </p>
        </div>

        <!-- Navegación -->
        <div class="footer-columna">
            <h4>Navegación</h4>

            <a href="${pageContext.request.contextPath}/inicio">Inicio</a>
            <a href="${pageContext.request.contextPath}/mi-perfil">Mi perfil</a>
            <a href="#">Mapa</a>
            <a href="${pageContext.request.contextPath}/sobre-nosotros">Sobre nosotros</a>
        </div>

        <!-- Ubicación -->
        <div class="footer-columna">
            <h4>Ubicación</h4>

            <div class="footer-item">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M12 21s6-5.2 6-11a6 6 0 1 0-12 0c0 5.8 6 11 6 11z"/>
                    <circle cx="12" cy="10" r="2"/>
                </svg>

                <span>Paysandú, Uruguay</span>
            </div>

            <div class="footer-item">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M3 16c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0"/>
                    <path d="M3 20c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0"/>
                    <path d="M8 14V7a3 3 0 0 1 6 0"/>
                </svg>

                <span>Termas de Guaviyú</span>
            </div>
        </div>

        <!-- Contacto -->
        <div class="footer-columna">
            <h4>Contacto</h4>

            <a href="#"
               class="footer-item footer-red"
               target="_blank"
               rel="noopener">

                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <rect x="3" y="3" width="18" height="18" rx="5"></rect>
                    <circle cx="12" cy="12" r="4"></circle>
                    <circle cx="17.5" cy="6.5" r="1"></circle>
                </svg>

                <span>Instagram</span>
            </a>


            <a href="#"
               class="footer-item footer-red"
               target="_blank"
               rel="noopener">

                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <circle cx="12" cy="12" r="9"></circle>
                    <path d="M8.5 8.5c.5 3.3 2.7 5.5 6 6"></path>
                    <path d="M8.7 8.4l1.4 2-1 1"></path>
                    <path d="M14.2 12.8l1.9.9-.8 1.4"></path>
                </svg>

                <span>WhatsApp</span>
            </a>


            <a href="mailto:contacto@alojamientosguaviyu.com"
               class="footer-item footer-red">

                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <rect x="3" y="5" width="18" height="14" rx="2"></rect>
                    <path d="M3 7l9 6 9-6"></path>
                </svg>

                <span>contacto@alojamientosguaviyu.com</span>
            </a>

        </div>

    </div>

    <div class="footer-inferior">
        <p>© 2026 Alojamientos Guaviyú. Todos los derechos reservados.</p>
        <p>Sitio web desarrollado por Brandon Machuca - Sofía Melnik - Facundo Soto</p>
    </div>
</footer>
