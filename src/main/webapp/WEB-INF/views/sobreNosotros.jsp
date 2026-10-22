<%--
  Created by IntelliJ IDEA.
  User: Sofía_Melnik
  Date: 9/10/2026
  Time: 15:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sobre nosotros | Alojamientos Guaviyú</title>
    <%@ include file="/WEB-INF/views/components/favicon.jsp" %>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <!-- Caveat para el sello; Poppins y Nunito Sans son las del header y el footer (igual que en el inicio) -->
    <link href="https://fonts.googleapis.com/css2?family=Caveat:wght@700&family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/header.css">
    <link rel="stylesheet" href="${ctx}/css/footer.css">
    <!-- ?v=2 obliga al navegador a bajar el CSS nuevo (subilo cuando lo cambies) -->
    <link rel="stylesheet" href="${ctx}/css/sobreNosotros.css?v=4">
</head>
<body>
<%@ include file="components/header.jsp" %>
<main>
    <!-- ===== Portada ===== -->
    <section class="portada">
        <div class="contenedor portada__interno">
            <h1>Nuestra historia<br>también es tu destino</h1>
        </div>
        <span class="sello">Disfrutá<br>Guaviyú
            <svg class="sello__sol" viewBox="0 0 24 24" aria-hidden="true">
                <circle cx="12" cy="12" r="4"/>
                <path d="M12 2v3M12 19v3M2 12h3M19 12h3M4.9 4.9l2.1 2.1M17 17l2.1 2.1M4.9 19.1 7 17M17 7l2.1-2.1"/>
            </svg>
        </span>
    </section>

    <!-- ===== Quiénes somos + valores ===== -->
    <section class="contenedor nosotros">
        <div class="nosotros__texto">
            <span class="etiqueta">Sobre nosotros</span>
            <h2>Una familia, un mismo propósito</h2>
            <p>Somos los fundadores de Alojamientos Guaviyú, una familia comprometida, una empresa nacida
               de la pasión por la hospitalidad y el deseo de conectar a las personas con lugares únicos y
               auténticos. Con más de 20 años de experiencia en las termas, hemos consolidado un enfoque que
               combina la personalización, la calidad y un profundo compromiso con la cultura local y el
               desarrollo de las comunidades.</p>
            <p>Desde Alojamientos Guaviyú, nos dedicamos a ofrecer espacios donde cada estancia sea más que
               una visita: sea una experiencia significativa. Trabajamos siendo anfitriones de vuestra
               experiencia de un turismo genuino, donde la comodidad y la autenticidad conviven para crear
               recuerdos duraderos. Nuestros alojamientos reflejan la identidad de cada lugar, ofreciendo a
               los viajeros una conexión auténtica con el destino.</p>
            <p>En Guaviyú, nuestra misión es clara: crear puentes que conecten a las personas con lo mejor de
               cada destino. Creemos en el valor de lo auténtico, en el poder del turismo responsable y en la
               importancia de devolver a las comunidades locales.</p>
        </div>

        <div class="valores">
            <article class="valor">
                <span class="valor__icono valor__icono--amarillo">
                    <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m12 2.5 2.9 6 6.6.8-4.9 4.6 1.3 6.5L12 17.2l-5.9 3.2 1.3-6.5L2.5 9.3l6.6-.8z"/></svg>
                </span>
                <div>
                    <h3>Más de 20 años de experiencia</h3>
                    <p>Conocemos y vivimos las termas de Guaviyú, ofreciendo un servicio de calidad y confianza.</p>
                </div>
            </article>
            <article class="valor">
                <span class="valor__icono valor__icono--azul">
                    <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="7.5" r="3.2"/><circle cx="5.5" cy="9" r="2.4"/><circle cx="18.5" cy="9" r="2.4"/><path d="M12 12c-3.3 0-6 1.8-6 4.2V19h12v-2.8C18 13.8 15.3 12 12 12zM5 13.2c-2.2.2-4 1.4-4 3V19h3.5v-2.8c0-1.1.2-2.1 1.5-3zM19 13.2c1.3.9 1.5 1.9 1.5 3V19H23v-2.8c0-1.6-1.8-2.8-4-3z"/></svg>
                </span>
                <div>
                    <h3>Hospitalidad auténtica</h3>
                    <p>Te recibimos como en casa, con un trato cercano y personalizado.</p>
                </div>
            </article>
            <article class="valor">
                <span class="valor__icono valor__icono--verde">
                    <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M20 3C10 3 4 8.5 4 15.5c0 1.3.3 2.6.8 3.6L3 21l1.4 1.4 1.9-1.9c1.1.6 2.4.9 3.7.9C16.5 21.4 21 15 20 3zM8.5 17.5l-1.4-1.4C10 13 13 10.5 16.5 8.5c-3 2.5-5.6 5.4-8 9z"/></svg>
                </span>
                <div>
                    <h3>Compromiso con la comunidad</h3>
                    <p>Apoyamos el desarrollo local y promovemos un turismo responsable y sostenible.</p>
                </div>
            </article>
        </div>
    </section>

    <!-- ===== Equipo (los datos vienen del servlet) ===== -->
    <section class="equipo">
        <div class="contenedor">
            <div class="equipo__titulo">
                <svg class="equipo__estrella" viewBox="0 0 24 24" aria-hidden="true"><path d="m12 2.5 2.9 6 6.6.8-4.9 4.6 1.3 6.5L12 17.2l-5.9 3.2 1.3-6.5L2.5 9.3l6.6-.8z"/></svg>
                <div>
                    <h2>Nuestro equipo</h2>
                    <p>Las personas que hacen posible Alojamientos Guaviyú</p>
                </div>
            </div>

            <div class="equipo__grilla">
                <c:forEach var="miembro" items="${equipo}">
                    <article class="tarjeta">
                        <img src="${ctx}/${miembro.foto}" alt="Foto de ${miembro.nombre}" class="tarjeta__foto">
                        <div>
                            <h3>${miembro.nombre}</h3>
                            <p class="tarjeta__cargo">${miembro.cargo}</p>
                            <p class="tarjeta__lugar">${miembro.lugar}</p>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </div>
    </section>
</main>

<%@ include file="components/footer.jsp" %>
</body>
</html>
