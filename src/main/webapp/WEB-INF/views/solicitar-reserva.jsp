<%--
  Created by IntelliJ IDEA.
  User: facuw
  Date: 14/10/2026
  Time: 13:36
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
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Solicitar reserva | Alojamientos Guaviyú</title>
    <%@ include file="/WEB-INF/views/components/favicon.jsp" %>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap"
          rel="stylesheet">

    <link rel="stylesheet" href="${ctx}/css/header.css">
    <link rel="stylesheet" href="${ctx}/css/solicitar-reserva.css?v=2">
    <link rel="stylesheet" href="${ctx}/css/footer.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/loading.css">
</head>

<body>

<%@ include file="components/header.jsp" %>


<main class="solicitud-pagina">


    <!--BANNER-->

    <section class="solicitud-banner">

        <div class="solicitud-banner-contenido">

            <p class="solicitud-etiqueta">
                RESERVAS
            </p>

            <h1>
                Solicitar reserva
            </h1>

            <p>
                Revisá los datos de tu estadía antes de enviar la solicitud.
            </p>

        </div>

    </section>


    <div class="solicitud-contenedor">


        <!--FORMULARIO-->

        <section class="solicitud-formulario">


            <div class="solicitud-cabecera">

                <div class="solicitud-icono">

                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <circle cx="12" cy="8" r="4"></circle>
                        <path d="M4 21c0-4 3.6-7 8-7s8 3 8 7"></path>
                    </svg>

                </div>


                <div>

                    <h2>
                        Datos de la solicitud
                    </h2>

                    <p>
                        Verificá que la información sea correcta.
                    </p>

                </div>

            </div>



            <form id="formSolicitudReserva"
                  action="${ctx}/solicitar-reserva"
                  method="post">


                <!-- DATOS NECESARIOS PARA DESPUÉS -->

                <input type="hidden"
                       name="alojamientoId"
                       value="${alojamiento.id}">

                <input type="hidden"
                       name="fechaIngreso"
                       value="${fechaIngreso}">

                <input type="hidden"
                       name="fechaSalida"
                       value="${fechaSalida}">

                <input type="hidden"
                       name="cantidadHuespedes"
                       value="${cantidadHuespedes}">


                <!--DATOS PERSONALES-->

                <div class="solicitud-seccion">

                    <div class="solicitud-seccion-titulo">

                        <span class="solicitud-seccion-icono">

                            <svg viewBox="0 0 24 24" aria-hidden="true">
                                <circle cx="12" cy="8" r="4"></circle>
                                <path d="M4 21c0-4 3.6-7 8-7s8 3 8 7"></path>
                            </svg>

                        </span>

                        <h3>
                            Datos personales
                        </h3>

                    </div>


                    <p class="solicitud-ayuda">
                        Estos datos fueron cargados desde tu perfil.
                    </p>


                    <div class="solicitud-grid">


                        <div class="solicitud-campo solicitud-campo-completo">

                            <label>
                                Nombre completo
                            </label>

                            <input type="text"
                                   value="${cliente.nombre}"
                                   readonly>

                        </div>


                        <div class="solicitud-campo">

                            <label>
                                Documento
                            </label>

                            <input type="text"
                                   value="${cliente.documento}"
                                   readonly>

                        </div>


                        <div class="solicitud-campo">

                            <label>
                                Teléfono
                            </label>

                            <input type="text"
                                   value="${cliente.telefono}"
                                   readonly>

                        </div>


                        <div class="solicitud-campo">

                            <label>
                                Correo electrónico
                            </label>

                            <input type="email"
                                   value="${cliente.email}"
                                   readonly>

                        </div>


                        <div class="solicitud-campo">

                            <label>
                                Procedencia
                            </label>

                            <input type="text"
                                   value="${cliente.procedencia}"
                                   readonly>

                        </div>


                    </div>

                </div>


                <!--DATOS DE LA ESTADÍA-->

                <div class="solicitud-seccion">

                    <div class="solicitud-seccion-titulo">

                        <span class="solicitud-seccion-icono">

                            <svg viewBox="0 0 24 24" aria-hidden="true">
                                <rect x="3"
                                      y="5"
                                      width="18"
                                      height="16"
                                      rx="2">
                                </rect>

                                <path d="M8 3v4M16 3v4M3 10h18"></path>
                            </svg>

                        </span>

                        <h3>
                            Datos de la estadía
                        </h3>

                    </div>


                    <div class="solicitud-grid">


                        <div class="solicitud-campo solicitud-campo-completo">

                            <label>
                                Alojamiento
                            </label>

                            <input type="text"
                                   value="${alojamiento.nombre}"
                                   readonly>

                        </div>


                        <div class="solicitud-campo">

                            <label>
                                Fecha de ingreso
                            </label>

                            <input type="text"
                                   value="${fechaIngreso.dayOfMonth < 10 ? '0' : ''}${fechaIngreso.dayOfMonth}/${fechaIngreso.monthValue < 10 ? '0' : ''}${fechaIngreso.monthValue}/${fechaIngreso.year}"
                                   readonly>

                        </div>


                        <div class="solicitud-campo">

                            <label>
                                Fecha de salida
                            </label>

                            <input type="text"
                                   value="${fechaSalida.dayOfMonth < 10 ? '0' : ''}${fechaSalida.dayOfMonth}/${fechaSalida.monthValue < 10 ? '0' : ''}${fechaSalida.monthValue}/${fechaSalida.year}"
                                   readonly>

                        </div>


                        <div class="solicitud-campo">

                            <label>
                                Huéspedes
                            </label>

                            <input type="text"
                                   value="${cantidadHuespedes}"
                                   readonly>

                        </div>


                        <div class="solicitud-campo">

                            <label>
                                Noches
                            </label>

                            <input type="text"
                                   value="${cantidadNoches}"
                                   readonly>

                        </div>


                    </div>


                    <!--MEDIO DIA EXTRA-->

                    <div class="medio-dia-opcion">

                        <label class="medio-dia-check">

                            <input type="checkbox"
                                   id="medioDia"
                                   name="medioDiaExtra"
                                   value="true">

                            <span>
                                ¿Deseás quedarte medio día más?
                            </span>

                        </label>


                        <p class="medio-dia-ayuda">
                            Podés extender tu estadía durante parte del día de salida.
                            Se cobrará el 50% del precio de una noche adicional.
                            Al seleccionar esta opción, el alojamiento quedará reservado
                            hasta las 21:00 hs del día de salida.
                        </p>

                    </div>


                </div>


                <!--EDADES-->

                <div class="solicitud-seccion">

                    <div class="solicitud-seccion-titulo">

                        <span class="solicitud-seccion-icono">

                            <svg viewBox="0 0 24 24" aria-hidden="true">

                                <circle cx="8"
                                        cy="8"
                                        r="3">
                                </circle>

                                <circle cx="16"
                                        cy="9"
                                        r="2.5">
                                </circle>

                                <path d="M2.5 20c0-3.5 2.5-6 5.5-6s5.5 2.5 5.5 6"></path>

                                <path d="M14 15c3.5 0 6 2 6 5"></path>

                            </svg>

                        </span>

                        <h3>
                            Edades de los huéspedes
                        </h3>

                    </div>


                    <p class="solicitud-ayuda">
                        Ingresá la edad de cada persona que se hospedará.
                    </p>


                    <div class="edades-grid">

                        <c:forEach begin="1"
                                   end="${cantidadHuespedes}"
                                   var="numero">

                            <div class="solicitud-campo">

                                <label for="edad${numero}">
                                    Huésped ${numero}
                                </label>

                                <input type="number"
                                       id="edad${numero}"
                                       name="edad"
                                       min="0"
                                       max="120"
                                       placeholder="Edad"
                                       required>

                                <span class="mensaje-error-edad"
                                      id="errorEdad${numero}">
                                </span>

                            </div>

                        </c:forEach>

                    </div>


                </div>


                <!--OBSERVACIONES-->

                <div class="solicitud-seccion">

                    <div class="solicitud-seccion-titulo">

                        <span class="solicitud-seccion-icono">

                            <svg viewBox="0 0 24 24" aria-hidden="true">

                                <path d="M4 5h16v11H8l-4 4z"></path>

                                <path d="M8 9h8M8 13h5"></path>

                            </svg>

                        </span>

                        <h3>
                            Observaciones
                        </h3>

                    </div>


                    <div class="solicitud-campo solicitud-campo-completo">

                        <label for="observaciones">
                            ¿Querés agregar algún comentario?
                        </label>

                        <textarea id="observaciones"
                                  name="observaciones"
                                  rows="4"
                                  maxlength="500"
                                  placeholder="Ej. horario aproximado de llegada, consulta especial, etc."></textarea>

                    </div>


                </div>


            </form>

        </section>


        <!--RESUMEN-->

        <aside class="solicitud-resumen">


            <h2>
                Resumen de la reserva
            </h2>


            <!-- ALOJAMIENTO -->

            <div class="resumen-alojamiento">

                <div class="resumen-alojamiento-icono">

                    <svg viewBox="0 0 24 24" aria-hidden="true">

                        <path d="M3 11l9-7 9 7"></path>

                        <path d="M5 10v10h14V10"></path>

                        <path d="M9 20v-6h6v6"></path>

                    </svg>

                </div>


                <div>

                    <strong>
                        <c:out value="${alojamiento.nombre}"/>
                    </strong>

                    <span>
                        <c:out value="${alojamiento.ubicacion}"/>
                    </span>

                </div>

            </div>


            <!-- DATOS RESUMEN -->

            <div class="resumen-datos">


                <div>

                    <span>
                        Ingreso
                    </span>

                    <strong>
                        <c:out value="${fechaIngreso.dayOfMonth < 10 ? '0' : ''}${fechaIngreso.dayOfMonth}/${fechaIngreso.monthValue < 10 ? '0' : ''}${fechaIngreso.monthValue}/${fechaIngreso.year}"/>
                    </strong>

                    <small class="resumen-horario">
                        Check-in: 12:00 pm
                    </small>

                </div>

                <div>

                    <span>
                        Salida
                    </span>

                    <strong>
                        <c:out value="${fechaSalida.dayOfMonth < 10 ? '0' : ''}${fechaSalida.dayOfMonth}/${fechaSalida.monthValue < 10 ? '0' : ''}${fechaSalida.monthValue}/${fechaSalida.year}"/>
                    </strong>

                    <small class="resumen-horario">
                        Check-out: 10:00 am
                    </small>

                </div>


                <div>

                    <span>
                        Huéspedes
                    </span>

                    <strong>
                        ${cantidadHuespedes}
                    </strong>

                </div>


                <div>

                    <span>
                        Noches
                    </span>

                    <strong>
                        ${cantidadNoches}
                    </strong>

                </div>


            </div>


            <!-- PRECIOS -->

            <div class="resumen-precios">


                <div class="resumen-precio">

                    <span>
                        Precio por noche
                    </span>

                    <strong>

                        $
                        <fmt:formatNumber value="${precioPorNoche}"
                                          type="number"
                                          maxFractionDigits="0"/>

                    </strong>

                </div>


                <div class="resumen-precio">

                    <span>

                        ${cantidadNoches}

                        ${cantidadNoches == 1 ? 'noche' : 'noches'}

                    </span>

                    <strong>

                        $
                        <fmt:formatNumber value="${precioTotal}"
                                          type="number"
                                          maxFractionDigits="0"/>

                    </strong>

                </div>


                <!-- SOLO APARECE SI MARCA MEDIO DÍA -->

                <div class="resumen-precio"
                     id="filaMedioDia"
                     hidden>

                    <span>
                        Medio día adicional
                    </span>

                    <strong id="precioMedioDia">
                        $ 0
                    </strong>

                </div>


                <div class="resumen-total">

                    <span>
                        Total estimado
                    </span>

                    <strong id="precioTotalFinal">

                        $
                        <fmt:formatNumber value="${precioTotal}"
                                          type="number"
                                          maxFractionDigits="0"/>

                    </strong>

                </div>


            </div>


            <!-- AVISO -->

            <div class="resumen-aviso">

                <span class="resumen-aviso-icono">!</span>

                <span>
                    El envío de la solicitud no confirma automáticamente
                    la reserva. La administración deberá revisarla.
                </span>

            </div>


            <!-- WHATSAPP -->

            <button type="submit"
                    form="formSolicitudReserva"
                    class="boton-whatsapp"
                    id="botonEnviarSolicitud">

                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <circle cx="12" cy="12" r="9"></circle>
                    <path d="M8.5 8.5c.5 3.3 2.7 5.5 6 6"></path>
                    <path d="M8.7 8.4l1.4 2-1 1"></path>
                    <path d="M14.2 12.8l1.9.9-.8 1.4"></path>
                </svg>

                Enviar solicitud por WhatsApp

            </button>


        </aside>


    </div>

</main>


<!--MEDIO DIA - ACTUALIZAR PRECIO-->

<script>

    const medioDia = document.getElementById("medioDia");

    const filaMedioDia = document.getElementById("filaMedioDia");

    const precioMedioDia = document.getElementById("precioMedioDia");

    const precioTotalFinal = document.getElementById("precioTotalFinal");


    const precioPorNoche = Number("${precioPorNoche}");

    const precioBase = Number("${precioTotal}");


    function formatearPrecio(valor) {

        return "$ " + valor.toLocaleString(
            "es-UY",
            {
                minimumFractionDigits: 0,
                maximumFractionDigits: 2
            }
        );
    }


    medioDia.addEventListener("change",function () {

            if (medioDia.checked) {

                const adicional = precioPorNoche / 2;

                const total = precioBase + adicional;


                filaMedioDia.hidden = false;


                precioMedioDia.textContent = formatearPrecio(adicional);


                precioTotalFinal.textContent = formatearPrecio(total);

            } else {

                filaMedioDia.hidden = true;


                precioTotalFinal.textContent = formatearPrecio(precioBase);
            }
        }
    );

</script>
<script>
    const formularioReserva =
        document.getElementById("formSolicitudReserva");

    formularioReserva.setAttribute("novalidate", "");

    formularioReserva.addEventListener("submit", function (evento) {

        let formularioValido = true;
        let primerCampoError = null;

        const camposEdad =
            formularioReserva.querySelectorAll('input[name="edad"]');

        camposEdad.forEach(function (campo) {

            const mensaje =
                document.getElementById(
                    "error" +
                    campo.id.charAt(0).toUpperCase() +
                    campo.id.slice(1)
                );

            campo.classList.remove("campo-error");

            if (mensaje) {
                mensaje.textContent = "";
                mensaje.classList.remove("visible");
            }

            if (campo.value.trim() === "") {

                formularioValido = false;

                campo.classList.add("campo-error");

                if (mensaje) {
                    mensaje.textContent =
                        "Ingresá la edad de este huésped.";
                    mensaje.classList.add("visible");
                }

                if (!primerCampoError) {
                    primerCampoError = campo;
                }

                return;
            }

            const edad = Number(campo.value);

            if (edad < 0 || edad > 120) {

                formularioValido = false;

                campo.classList.add("campo-error");

                if (mensaje) {
                    mensaje.textContent =
                        "Ingresá una edad válida entre 0 y 120 años.";
                    mensaje.classList.add("visible");
                }

                if (!primerCampoError) {
                    primerCampoError = campo;
                }
            }
        });

        if (!formularioValido) {

            evento.preventDefault();

            primerCampoError.focus();

            primerCampoError.scrollIntoView({
                behavior: "smooth",
                block: "center"
            });
        }
    });


    formularioReserva
        .querySelectorAll('input[name="edad"]')
        .forEach(function (campo) {

            campo.addEventListener("input", function () {

                const mensaje =
                    document.getElementById(
                        "error" +
                        campo.id.charAt(0).toUpperCase() +
                        campo.id.slice(1)
                    );

                campo.classList.remove("campo-error");

                if (mensaje) {
                    mensaje.textContent = "";
                    mensaje.classList.remove("visible");
                }
            });
        });
</script>
<%@ include file="/WEB-INF/views/components/loading.jsp" %>
<script src="${pageContext.request.contextPath}/js/loading.js"></script>
<%@ include file="components/footer.jsp" %>
</body>
</html>
