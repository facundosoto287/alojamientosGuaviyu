<%--
  Created by IntelliJ IDEA.
  User: facuw
  Date: 03/11/2026
  Time: 10:37 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%@ include file="layout-top.jspf" %>

<section class="adm-panel observaciones-panel">

    <div class="observaciones-cabecera">
        <div>
            <h1>Observaciones de clientes</h1>
            <p>
                Consultá los datos de cada cliente y registrá observaciones internas.
            </p>
        </div>
    </div>

    <!-- BUSCADOR -->
    <form method="get"
          action="${base}/clientes/observaciones"
          class="observaciones-buscador">

        <input type="text"
               name="q"
               value="<c:out value='${q}'/>"
               placeholder="Buscar por nombre o documento">

        <button type="submit" class="btn btn-primario">
            Buscar
        </button>

        <c:if test="${not empty q}">
            <a href="${base}/clientes/observaciones"
               class="btn btn-secundario">
                Limpiar
            </a>
        </c:if>

    </form>


    <div class="observaciones-contenido">

        <!-- LISTA DE CLIENTES -->
        <aside class="observaciones-clientes">

            <h2>Clientes</h2>

            <c:choose>

                <c:when test="${empty clientes}">
                    <p class="observaciones-vacio">
                        No se encontraron clientes.
                    </p>
                </c:when>

                <c:otherwise>

                    <div class="observaciones-clientes-lista">

                        <c:forEach var="cliente" items="${clientes}">

                            <a href="${base}/clientes/observaciones?cliente=${cliente.id}"
                               class="observaciones-cliente-item
                               ${not empty clienteSeleccionado && clienteSeleccionado.id == cliente.id ? 'activo' : ''}">

                                <strong>
                                    <c:out value="${cliente.nombre}"/>
                                </strong>

                                <span>
                                    <c:out value="${cliente.documento}"/>
                                </span>

                            </a>

                        </c:forEach>

                    </div>

                </c:otherwise>

            </c:choose>

        </aside>


        <!-- DETALLE DEL CLIENTE -->
        <section class="observaciones-detalle">

            <c:choose>

                <c:when test="${empty clienteSeleccionado}">

                    <div class="observaciones-sin-seleccion">
                        <h2>Seleccioná un cliente</h2>
                        <p>
                            Elegí un cliente de la lista para ver sus datos y observaciones.
                        </p>
                    </div>

                </c:when>

                <c:otherwise>

                    <!-- DATOS -->
                    <div class="observaciones-datos-cliente">

                        <div class="observaciones-datos-cabecera">
                            <h2>
                                <c:out value="${clienteSeleccionado.nombre}"/>
                            </h2>
                        </div>

                        <div class="observaciones-datos-grid">

                            <div>
                                <span>Documento</span>
                                <strong>
                                    <c:out value="${clienteSeleccionado.documento}"/>
                                </strong>
                            </div>

                            <div>
                                <span>Teléfono</span>
                                <strong>
                                    <c:out value="${clienteSeleccionado.telefono}"/>
                                </strong>
                            </div>

                            <div>
                                <span>Correo</span>
                                <strong>
                                    <c:out value="${clienteSeleccionado.email}"/>
                                </strong>
                            </div>

                            <div>
                                <span>Procedencia</span>
                                <strong>
                                    <c:out value="${clienteSeleccionado.procedencia}"/>
                                </strong>
                            </div>

                            <div>
                                <span>Localidad</span>
                                <strong>
                                    <c:out value="${clienteSeleccionado.localidad}"/>
                                </strong>
                            </div>

                        </div>

                    </div>


                    <!-- NUEVA OBSERVACIÓN -->
                    <div class="observacion-formulario-contenedor">

                        <h3>Nueva observación</h3>

                        <form method="post"
                              action="${base}/clientes/observaciones"
                              class="observacion-formulario">
                              <input type="hidden" name="_csrf" value="${csrf}">

                            <input type="hidden"
                                   name="accion"
                                   value="agregar">

                            <input type="hidden"
                                   name="clienteId"
                                   value="${clienteSeleccionado.id}">

                            <textarea name="descripcion"
                                      rows="4"
                                      maxlength="1000"
                                      required
                                      placeholder="Escribí una observación sobre este cliente..."></textarea>

                            <div class="observacion-formulario-acciones">

                                <button type="submit"
                                        class="btn btn-primario">
                                    + Agregar observación
                                </button>

                            </div>

                        </form>

                    </div>


                    <!-- HISTORIAL -->
                    <div class="observaciones-historial">

                        <h3>Historial de observaciones</h3>

                        <c:choose>

                            <c:when test="${empty observaciones}">

                                <p class="observaciones-vacio">
                                    Este cliente todavía no tiene observaciones.
                                </p>

                            </c:when>

                            <c:otherwise>

                                <div class="observaciones-lista">

                                    <c:forEach var="observacion"
                                               items="${observaciones}">

                                        <article class="observacion-item">

                                            <div class="observacion-item-cabecera">

                                                <span class="observacion-fecha">
                                                    ${observacion.fechaFormateada}
                                                </span>

                                                <form method="post"
                                                      action="${base}/clientes/observaciones"
                                                      onsubmit="return confirm('¿Eliminar esta observación?');">
                                                      <input type="hidden" name="_csrf" value="${csrf}">

                                                    <input type="hidden"
                                                           name="accion"
                                                           value="eliminar">

                                                    <input type="hidden"
                                                           name="clienteId"
                                                           value="${clienteSeleccionado.id}">

                                                    <input type="hidden"
                                                           name="id"
                                                           value="${observacion.id}">

                                                    <button type="submit"
                                                            class="observacion-eliminar">
                                                        Eliminar
                                                    </button>

                                                </form>

                                            </div>

                                            <p>
                                                <c:out value="${observacion.descripcion}"/>
                                            </p>

                                        </article>

                                    </c:forEach>

                                </div>

                            </c:otherwise>

                        </c:choose>

                    </div>

                </c:otherwise>

            </c:choose>

        </section>

    </div>

</section>

<%@ include file="layout-bottom.jspf" %>
