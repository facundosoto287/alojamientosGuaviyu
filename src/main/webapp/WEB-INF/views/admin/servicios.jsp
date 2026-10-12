<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="layout-top.jspf" %>

<nav class="migas" aria-label="Ruta"><a href="${base}/inicio">Inicio</a> <span>/</span> Servicios</nav>

<div class="pagina-titulo">
    <div>
        <h1>Servicios y asociaciones</h1>
        <p class="muted">Gestioná los servicios disponibles del sistema y asignalos a los alojamientos.</p>
    </div>
    <button type="button" class="btn btn-primario" data-modal-abrir="m-agregar">+ Agregar servicio</button>
</div>

<div class="dos-columnas">

    <%-- ============ Servicios disponibles ============ --%>
    <section class="tarjeta">
        <div class="tarjeta-cabecera"><h2>Servicios disponibles</h2></div>

        <c:choose>
            <c:when test="${empty servicios}"><p class="vacio">Todavía no hay servicios registrados.</p></c:when>
            <c:otherwise>
                <div class="tabla-scroll">
                    <table class="tabla">
                        <thead>
                        <tr><th>ID</th><th>Servicio</th><th>En alojamientos</th><th class="acciones-col">Acciones</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="s" items="${servicios}">
                            <c:set var="cantidad" value="${usos[s.id]}"/>
                            <tr>
                                <td><strong>#${s.id}</strong></td>
                                <td><c:out value="${s.nombre}"/></td>
                                <td>${cantidad}</td>
                                <td class="acciones">
                                    <button type="button" class="btn btn-editar" data-modal-abrir="m-editar"
                                            data-f-id="${s.id}" data-f-nombre="<c:out value='${s.nombre}'/>">Editar</button>
                                    <form action="${base}/servicios" method="post"
                                          data-confirmar="¿Eliminar el servicio &quot;<c:out value='${s.nombre}'/>&quot;?<c:if test='${cantidad > 0}'> Está asociado a ${cantidad} alojamiento(s) y también se quitará de ellos.</c:if>">
                                        <input type="hidden" name="_csrf" value="${csrf}">
                                        <input type="hidden" name="accion" value="eliminar">
                                        <input type="hidden" name="id" value="${s.id}">
                                        <c:if test="${not empty alojamientoSel}"><input type="hidden" name="alojamiento" value="${alojamientoSel}"></c:if>
                                        <button type="submit" class="btn btn-eliminar">Eliminar</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <%-- ============ Asociar a un alojamiento ============ --%>
    <section class="tarjeta">
        <div class="tarjeta-cabecera"><h2>Asociar servicios a un alojamiento</h2></div>

        <form action="${base}/servicios" method="get" class="campo-simple">
            <label for="sel-alojamiento">Alojamiento</label>
            <select id="sel-alojamiento" name="alojamiento" data-autoenviar>
                <option value="">Elegí un alojamiento…</option>
                <c:forEach var="a" items="${alojamientos}">
                    <option value="${a.id}" ${a.id == alojamientoSel ? 'selected' : ''}><c:out value="${a.nombre}"/></option>
                </c:forEach>
            </select>
            <noscript><button type="submit" class="btn btn-secundario">Ver</button></noscript>
        </form>

        <c:if test="${not empty alojamientoSel}">
            <form action="${base}/servicios" method="post" class="bloque">
                <input type="hidden" name="_csrf" value="${csrf}">
                <input type="hidden" name="alojamiento" value="${alojamientoSel}">

                <p class="etiqueta">Marcá los servicios y elegí qué hacer:</p>
                <c:choose>
                    <c:when test="${empty servicios}"><p class="vacio">Primero agregá algún servicio.</p></c:when>
                    <c:otherwise>
                        <ul class="lista-checks">
                            <c:forEach var="s" items="${servicios}">
                                <li>
                                    <label>
                                        <input type="checkbox" name="servicioIds" value="${s.id}">
                                        <span><c:out value="${s.nombre}"/></span>
                                        <c:if test="${asociados.contains(s.id)}"><em class="etiqueta-chica">asociado</em></c:if>
                                    </label>
                                </li>
                            </c:forEach>
                        </ul>
                        <div class="botonera">
                            <button type="submit" name="accion" value="asociar" class="btn btn-primario">Asociar</button>
                            <button type="submit" name="accion" value="desasociar" class="btn btn-peligro-borde">Desasociar</button>
                        </div>
                    </c:otherwise>
                </c:choose>
            </form>

            <div class="bloque caja">
                <h3>Servicios del alojamiento seleccionado</h3>
                <c:choose>
                    <c:when test="${empty serviciosDelAlojamiento}"><p class="muted">Este alojamiento todavía no tiene servicios asociados.</p></c:when>
                    <c:otherwise>
                        <ul class="chips">
                            <c:forEach var="s" items="${serviciosDelAlojamiento}">
                                <li class="chip">
                                    <span><c:out value="${s.nombre}"/></span>
                                    <form action="${base}/servicios" method="post">
                                        <input type="hidden" name="_csrf" value="${csrf}">
                                        <input type="hidden" name="accion" value="desasociar">
                                        <input type="hidden" name="alojamiento" value="${alojamientoSel}">
                                        <input type="hidden" name="servicioIds" value="${s.id}">
                                        <button type="submit" class="chip-quitar" aria-label="Quitar ${fn:escapeXml(s.nombre)}">&times;</button>
                                    </form>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </section>
</div>

<%-- ============ Agregar ============ --%>
<dialog id="m-agregar" class="modal modal-chico">
    <form action="${base}/servicios" method="post" class="modal-form">
        <h2>Agregar servicio</h2>
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="accion" value="agregar">
        <c:if test="${not empty alojamientoSel}"><input type="hidden" name="alojamiento" value="${alojamientoSel}"></c:if>
        <label class="campo ancho">Nombre del servicio
            <input type="text" name="nombre" maxlength="60" placeholder="Ej. Wi-Fi" required></label>
        <div class="modal-pie">
            <button type="button" class="btn btn-secundario" data-modal-cerrar>Cancelar</button>
            <button type="submit" class="btn btn-primario">Guardar</button>
        </div>
    </form>
</dialog>

<%-- ============ Editar ============ --%>
<dialog id="m-editar" class="modal modal-chico">
    <form action="${base}/servicios" method="post" class="modal-form">
        <h2>Editar servicio</h2>
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="accion" value="editar">
        <input type="hidden" name="id">
        <c:if test="${not empty alojamientoSel}"><input type="hidden" name="alojamiento" value="${alojamientoSel}"></c:if>
        <label class="campo ancho">Nombre del servicio
            <input type="text" name="nombre" maxlength="60" required></label>
        <div class="modal-pie">
            <button type="button" class="btn btn-secundario" data-modal-cerrar>Cancelar</button>
            <button type="submit" class="btn btn-primario">Guardar cambios</button>
        </div>
    </form>
</dialog>

<%@ include file="layout-bottom.jspf" %>
