<%--
  Created by IntelliJ IDEA.
  User: facuw
  Date: 13/10/2026
  Time: 13:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>
        Mis reservas | Alojamientos Guaviyú
    </title>

    <%@ include file="/WEB-INF/views/components/favicon.jsp" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/loading.css">

    <link rel="preconnect"
          href="https://fonts.googleapis.com">

    <link rel="preconnect"
          href="https://fonts.gstatic.com"
          crossorigin>


    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap"
          rel="stylesheet">


    <link rel="stylesheet"
          href="${ctx}/css/header.css">

    <link rel="stylesheet"
          href="${ctx}/css/mis-reservas.css">

    <link rel="stylesheet"
          href="${ctx}/css/footer.css">

</head>


<body>


<%@ include file="components/header.jsp" %>


<main class="reservas-pagina">


    <!--BANNER-->

    <div class="volver-perfil-contenedor">
        <div class="volver-perfil-inner">
            <a class="boton-volver"
               href="${ctx}/perfil">
                ← Volver a mi perfil
            </a>
        </div>
    </div>

    <section class="reservas-banner">

        <div class="reservas-banner-contenido">

            <p class="reservas-etiqueta">
                Alojamientos Guaviyú
            </p>


            <h1>
                Mis reservas
            </h1>


            <p>
                Consultá tus próximas estadías y el historial de tus reservas.
            </p>

        </div>

    </section>



    <div class="reservas-contenedor">


        <!--LISTADO-->

        <section class="reservas-panel">


            <div class="reservas-panel-cabecera">


                <div class="reservas-icono">

                    <svg viewBox="0 0 24 24"
                         aria-hidden="true">

                        <rect x="3"
                              y="5"
                              width="18"
                              height="16"
                              rx="2">
                        </rect>

                        <path d="M8 3v4M16 3v4M3 10h18"></path>

                    </svg>

                </div>


                <div>

                    <h2>
                        Mis reservas
                    </h2>

                    <p>
                        Administrá tus próximas estadías y revisá el historial.
                    </p>

                </div>


            </div>



            <!--FILTROS-->

            <div class="reservas-filtros">


                <button type="button"
                        class="filtro-reserva activo"
                        data-filtro="TODAS">

                    Todas

                </button>


                <button type="button"
                        class="filtro-reserva"
                        data-filtro="PENDIENTE">

                    Pendientes

                </button>


                <button type="button"
                        class="filtro-reserva"
                        data-filtro="CONFIRMADA">

                    Confirmadas

                </button>


                <button type="button"
                        class="filtro-reserva"
                        data-filtro="CANCELADA">

                    Canceladas

                </button>


            </div>



            <!--SIN RESERVAS-->

            <c:if test="${empty reservas}">

                <div class="reservas-vacio">

                    <svg viewBox="0 0 24 24"
                         aria-hidden="true">

                        <rect x="3"
                              y="5"
                              width="18"
                              height="16"
                              rx="2">
                        </rect>

                        <path d="M8 3v4M16 3v4M3 10h18"></path>

                    </svg>


                    <h3>
                        Todavía no tenés reservas
                    </h3>


                    <p>
                        Cuando realices una solicitud aparecerá acá.
                    </p>

                </div>

            </c:if>



            <!--LISTADO REAL-->

            <c:if test="${not empty reservas}">


                <div class="reservas-tabla-cabecera">

                    <span>
                        Alojamiento
                    </span>

                    <span>
                        Fecha ingreso
                    </span>

                    <span>
                        Fecha salida
                    </span>

                    <span>
                        Huéspedes
                    </span>

                    <span>
                        Estado
                    </span>

                    <span></span>

                </div>



                <div class="reservas-listado">


                    <c:forEach var="reserva"
                               items="${reservas}">


                        <article
                                class="reserva-fila"

                                data-estado="${reserva.estado}"

                                data-nombre="${reserva.nombreAlojamiento}"

                                data-ubicacion="${reserva.ubicacionAlojamiento}"

                                data-ingreso="${reserva.fechaIngreso}"

                                data-salida="${reserva.fechaSalida}"

                                data-huespedes="${reserva.cantidadHuespedes}"

                                data-estado-reserva="${reserva.estado}"

                                data-noches="${reserva.cantidadNoches}"

                                data-precio="${reserva.precioPorNoche}"

                                data-total="${reserva.precioTotal}"

                                data-medio-dia="${reserva.medioDiaExtra}">



                            <!--ALOJAMIENTO-->

                            <div class="reserva-alojamiento-fila">


                                <div class="reserva-miniatura">

                                    <svg viewBox="0 0 24 24"
                                         aria-hidden="true">

                                        <path d="M3 11l9-7 9 7"></path>

                                        <path d="M5 10v10h14V10"></path>

                                        <path d="M9 20v-6h6v6"></path>

                                    </svg>

                                </div>


                                <div>

                                    <strong>
                                        <c:out value="${reserva.nombreAlojamiento}"/>
                                    </strong>


                                    <span>
                                        <c:out value="${reserva.ubicacionAlojamiento}"/>
                                    </span>

                                </div>


                            </div>



                            <!--INGRESO-->

                            <div class="reserva-celda">

                                <span class="reserva-celda-titulo">
                                    Fecha de ingreso
                                </span>


                                <strong>
                                    <fmt:formatDate value="${reserva.fechaIngreso}" pattern="dd/MM/yyyy"/>
                                </strong>

                            </div>



                            <!--SALIDA-->

                            <div class="reserva-celda">

                                <span class="reserva-celda-titulo">
                                    Fecha de salida
                                </span>


                                <strong>
                                    <fmt:formatDate value="${reserva.fechaSalida}" pattern="dd/MM/yyyy"/>
                                </strong>

                            </div>



                            <!--HUESPEDES-->

                            <div class="reserva-celda">

                                <span class="reserva-celda-titulo">
                                    Huéspedes
                                </span>


                                <strong>
                                    <c:out value="${reserva.cantidadHuespedes}"/>
                                </strong>

                            </div>



                            <!--ESTADO-->

                            <div class="reserva-celda">

                                <span class="reserva-celda-titulo">
                                    Estado
                                </span>


                                <c:choose>


                                    <c:when test="${reserva.estado == 'PENDIENTE'}">

                                        <span class="estado estado-pendiente">
                                            Pendiente
                                        </span>

                                    </c:when>


                                    <c:when test="${reserva.estado == 'CONFIRMADA'}">

                                        <span class="estado estado-confirmada">
                                            Confirmada
                                        </span>

                                    </c:when>


                                    <c:otherwise>

                                        <span class="estado estado-cancelada">
                                            Cancelada
                                        </span>

                                    </c:otherwise>


                                </c:choose>
                            </div>



                            <!--BOTON-->

                            <div class="reserva-celda reserva-accion">

                                <button type="button"
                                        class="btn-ver-detalle">

                                    Ver detalle

                                    <span aria-hidden="true">
                                        →
                                    </span>

                                </button>

                            </div>


                        </article>


                    </c:forEach>
                </div>



                <!--SIN RESULTADOS DEL FILTRO-->
                <div id="filtroVacio"
                     class="reservas-vacio"
                     hidden>


                    <svg viewBox="0 0 24 24"
                         aria-hidden="true">

                        <rect x="3"
                              y="5"
                              width="18"
                              height="16"
                              rx="2">
                        </rect>

                        <path d="M8 3v4M16 3v4M3 10h18"></path>

                    </svg>


                    <h3 id="filtroVacioTitulo">
                        No tenés reservas
                    </h3>


                    <p>
                        No encontramos reservas con este estado
                    </p>


                </div>


            </c:if>


        </section>



        <!--DETALLE-->
        <aside class="detalle-reserva"
               id="detalleReserva">


            <!--CABECERA-->

            <div class="detalle-cabecera">


                <div class="detalle-icono">

                    <svg viewBox="0 0 24 24"
                         aria-hidden="true">

                        <rect x="3"
                              y="5"
                              width="18"
                              height="16"
                              rx="2">
                        </rect>

                        <path d="M8 3v4M16 3v4M3 10h18"></path>

                    </svg>

                </div>


                <div>

                    <h2>
                        Detalle de la solicitud
                    </h2>


                    <span id="detalleEstado"
                          class="estado">
                    </span>

                </div>


            </div>



            <!--SIN RESERVA SELECCIONADA-->

            <div id="detalleVacio"
                 class="detalle-vacio">

                <p>
                    Seleccioná una reserva para ver sus datos.
                </p>

            </div>



            <!--CONTENIDO DEL DETALLE-->

            <div id="detalleContenido"
                 hidden>



                <!--IMAGEN-->

                <div class="detalle-imagen-placeholder">

                    <svg viewBox="0 0 24 24"
                         aria-hidden="true">

                        <path d="M3 11l9-7 9 7"></path>

                        <path d="M5 10v10h14V10"></path>

                        <path d="M9 20v-6h6v6"></path>

                    </svg>

                </div>



                <!--ALOJAMIENTO-->

                <h3 id="detalleNombre">
                </h3>


                <p id="detalleUbicacion"
                   class="detalle-ubicacion">
                </p>



                <!--DATOS DE LA RESERVA-->

                <div class="detalle-grid">


                    <div>

                        <span>
                            Fecha de ingreso
                        </span>

                        <strong id="detalleIngreso">
                        </strong>

                    </div>



                    <div>

                        <span>
                            Fecha de salida
                        </span>

                        <strong id="detalleSalida">
                        </strong>

                    </div>

                    <div>

                        <span>
                            Horario de entrada
                        </span>

                        <strong>
                            12:00 pm
                        </strong>

                    </div>

                    <div>

                        <span>
                            Horario de salida
                        </span>

                        <strong id="detalleHorarioSalida">
                        </strong>

                    </div>



                    <div>

                        <span>
                            Huéspedes
                        </span>

                        <strong id="detalleHuespedes">
                        </strong>

                    </div>



                    <div>

                        <span>
                            Noches
                        </span>

                        <strong id="detalleNoches">
                        </strong>

                    </div>



                    <div>

                        <span>
                            Medio día adicional
                        </span>

                        <strong id="detalleMedioDia">
                        </strong>

                    </div>


                </div>



                <!--PRECIOS-->

                <div class="detalle-precios">


                    <h4>
                        Resumen de precios
                    </h4>



                    <!--PRECIO POR NOCHE-->

                    <div class="fila-precio">

                        <span>
                            Precio por noche
                        </span>

                        <strong id="detallePrecioNoche">
                        </strong>

                    </div>



                    <!--SUBTOTAL NOCHES-->

                    <div class="fila-precio">

                        <span id="detalleCalculoNoches">
                        </span>

                        <strong id="detalleSubtotal">
                        </strong>

                    </div>



                    <!--MEDIO DIA-->

                    <div class="fila-precio"
                         id="detalleFilaMedioDia"
                         hidden>

                        <span>
                            Medio día adicional
                        </span>

                        <strong id="detallePrecioMedioDia">
                        </strong>

                    </div>



                    <!--TOTAL-->

                    <div class="fila-precio total">

                        <span>
                            Total estimado
                        </span>

                        <strong id="detalleTotal">
                        </strong>

                    </div>


                </div>


            </div>


        </aside>


    </div>


</main>
<%@ include file="/WEB-INF/views/components/loading.jsp" %>
<script src="${pageContext.request.contextPath}/js/loading.js"></script>

<%@ include file="components/footer.jsp" %>


<script src="${ctx}/js/mis-reservas.js"></script>


</body>

</html>

