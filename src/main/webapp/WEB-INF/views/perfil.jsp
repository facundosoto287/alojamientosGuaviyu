<%--
  Created by IntelliJ IDEA.
  User: facuw
  Date: 13/10/2026
  Time: 10:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Mi perfil | Alojamientos Guaviyú</title>
    <%@ include file="/WEB-INF/views/components/favicon.jsp" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/loading.css">

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${ctx}/css/header.css">
    <link rel="stylesheet" href="${ctx}/css/perfil.css">
    <link rel="stylesheet" href="${ctx}/css/footer.css">
</head>

<body>

<%@ include file="components/header.jsp" %>

<main class="perfil-pagina">

    <!-- CABECERA -->
    <section class="perfil-encabezado">
        <div class="perfil-encabezado-contenido">

            <p class="perfil-etiqueta">
                Alojamientos Guaviyú
            </p>

            <h1>
                Mi perfil
            </h1>

            <p>
                Administrá tus datos personales, reservas y reseñas.
            </p>

        </div>

    </section>

    <div class="perfil-contenedor">


        <!--MENÚ LATERAL-->

        <aside class="perfil-menu">
            <div class="perfil-usuario">
                <div class="perfil-avatar">
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <circle cx="12" cy="8" r="4"></circle>
                        <path d="M4 21c0-4.4 3.6-7 8-7s8 2.6 8 7"></path>
                    </svg>
                </div>

                <div class="perfil-usuario-datos">

                    <strong>
                        <c:out value="${cliente.nombre}"/>
                    </strong>

                    <span>
                        <c:out value="${cliente.email}"/>
                    </span>

                </div>

            </div>

            <nav class="perfil-nav">

                <a href="#datos" class="activo">

                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <circle cx="12" cy="8" r="4"></circle>
                        <path d="M4 21c0-4.4 3.6-7 8-7s8 2.6 8 7"></path>
                    </svg>

                    Mi perfil
                </a>

                <a href="${ctx}/mis-reservas" class="${paginaActiva == 'mis-reservas' ? 'activo' : ''}">
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="3" y="5" width="18" height="16" rx="2"/>
                        <path d="M8 3v4M16 3v4M3 10h18"/>
                    </svg>
                    <span>Mis reservas</span>
                </a>

                <a href="#resenas">

                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M12 3l2.8 5.7 6.2.9-4.5 4.4 1.1 6.2L12 17.3 6.4 20.2 7.5 14 3 9.6l6.2-.9z"></path>
                    </svg>

                    Mis reseñas
                </a>

                <a href="${ctx}/cambiar-contrasena?origen=perfil">

                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="5" y="10" width="14" height="11" rx="2"></rect>
                        <path d="M8 10V7a4 4 0 0 1 8 0v3"></path>
                    </svg>
                    Cambiar contraseña
                </a>

            </nav>

            <form action="${ctx}/logout"
                  method="post"
                  class="perfil-logout">

                <button type="submit">

                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M9 5H5v14h4"></path>
                        <path d="M13 8l4 4-4 4"></path>
                        <path d="M17 12H9"></path>
                    </svg>

                    Cerrar sesión
                </button>

            </form>

        </aside>


        <!--CONTENIDO CENTRAL-->

        <section class="perfil-principal">

            <!-- DATOS PERSONALES -->

            <section class="perfil-tarjeta" id="datos">

                <div class="perfil-tarjeta-cabecera">

                    <div>
                        <h2>
                            Datos personales
                        </h2>

                        <p>
                            Consultá y actualizá la información de tu cuenta.
                        </p>
                    </div>
                </div>

                <form action="${ctx}/perfil"
                      method="post"
                      class="perfil-formulario">

                    <div class="perfil-grid">

                        <div class="perfil-campo">

                            <label for="nombre">
                                Nombre completo
                            </label>

                            <input type="text"
                                   id="nombre"
                                   value="<c:out value='${cliente.nombre}'/>"
                                   readonly>

                        </div>

                        <div class="perfil-campo">

                            <label for="documento">
                                Documento
                            </label>

                            <input type="text"
                                   id="documento"
                                   value="<c:out value='${cliente.documento}'/>"
                                   readonly>

                        </div>

                        <div class="perfil-campo">

                            <label for="telefono">
                                Teléfono
                            </label>

                            <input type="text"
                                   id="telefono"
                                   name="telefono"
                                   value="<c:out value='${cliente.telefono}'/>"
                                   required>

                        </div>

                        <div class="perfil-campo">

                            <label for="email">
                                Correo electrónico
                            </label>

                            <input type="email"
                                   id="email"
                                   name="email"
                                   value="<c:out value='${cliente.email}'/>"
                                   required>
                        </div>

                        <div class="perfil-campo perfil-campo-completo">

                            <label for="procedencia">
                                Procedencia
                            </label>
                            <p class="ayuda-campo">Departamento, provincia o país donde vivís.</p>

                            <select id="procedencia"
                                    name="procedencia"
                                    required>

                                <option value="">
                                    Seleccioná tu procedencia
                                </option>

                                <c:forEach var="opcion" items="${procedencias}">
                                    <option value="${opcion}"
                                            ${cliente.procedencia == opcion ? 'selected' : ''}>
                                        <c:out value="${opcion}"/>
                                    </option>
                                </c:forEach>

                            </select>

                        </div>

                        <div class="perfil-campo perfil-campo-completo">

                            <label for="localidad">
                                Localidad
                            </label>
                            <p class="ayuda-campo">Ciudad o pueblo donde vivís (por ejemplo: Paysandú, Salto o Guichón).</p>
                            <c:if test="${empty cliente.localidad}">
                                <p class="ayuda-campo ayuda-falta">Todavía no cargaste tu localidad: completala, es un dato obligatorio.</p>
                            </c:if>

                            <input type="text"
                                   id="localidad"
                                   name="localidad"
                                   list="lista-localidades"
                                   maxlength="100"
                                   placeholder="Tu ciudad o pueblo"
                                   value="<c:out value='${cliente.localidad}'/>"
                                   required>

                            <datalist id="lista-localidades">
                                <c:forEach var="sugerida" items="${localidades}">
                                    <option value="<c:out value='${sugerida}'/>"></option>
                                </c:forEach>
                            </datalist>

                        </div>
                    </div>

                    <c:if test="${not empty mensajePerfil}">

                        <p class="perfil-mensaje correcto">
                            <c:out value="${mensajePerfil}"/>
                        </p>

                    </c:if>

                    <c:if test="${not empty errorPerfil}">

                        <p class="perfil-mensaje error">
                            <c:out value="${errorPerfil}"/>
                        </p>

                    </c:if>

                    <div class="perfil-acciones">

                        <button type="submit"
                                class="perfil-btn-guardar">
                            Guardar cambios
                        </button>
                    </div>
                </form>
            </section>

        </section>

        <!--RESEÑAS-->

        <aside class="perfil-resenas"
               id="resenas">

            <div class="perfil-resenas-cabecera">

                <h2>
                    Mis reseñas
                </h2>

                <p>
                    Tus opiniones sobre los alojamientos
                </p>

            </div>

            <c:choose>

                <c:when test="${empty resenas}">

                    <div class="perfil-vacio">

                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M12 3l2.8 5.7 6.2.9-4.5 4.4 1.1 6.2L12 17.3 6.4 20.2 7.5 14 3 9.6l6.2-.9z"></path>
                        </svg>

                        <p>
                            Todavía no realizaste ninguna reseña.
                        </p>

                    </div>

                </c:when>

                <c:otherwise>

                    <c:forEach var="resena"
                               items="${resenas}">

                        <article class="resena-card">

                            <div class="resena-cabecera">

                                <strong>
                                    <c:out value="${resena.nombreAlojamiento}"/>
                                </strong>

                                <span class="resena-fecha">
                                    <c:out value="${resena.fecha}"/>
                                </span>

                            </div>

                            <div class="resena-estrellas">

                                <c:forEach begin="1"
                                           end="5"
                                           var="estrella">

                                    <span class="${estrella <= resena.puntuacion ? 'activa' : ''}">
                                        ★
                                    </span>

                                </c:forEach>

                            </div>

                            <p class="resena-comentario">

                                <c:out value="${resena.comentario}"/>

                            </p>

                        </article>

                    </c:forEach>

                </c:otherwise>

            </c:choose>

        </aside>

    </div>

</main>
<%@ include file="/WEB-INF/views/components/loading.jsp" %>
<script src="${pageContext.request.contextPath}/js/loading.js"></script>
<%@ include file="components/footer.jsp" %>
</body>
</html>
