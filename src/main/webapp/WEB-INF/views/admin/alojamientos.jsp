<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="layout-top.jspf" %>

<nav class="migas" aria-label="Ruta"><a href="${base}/inicio">Inicio</a> <span>/</span> Alojamientos</nav>

<div class="pagina-titulo">
    <div>
        <h1>Alojamientos</h1>
        <p class="muted">Gestioná el catálogo: agregá, editá o retirá alojamientos.</p>
    </div>
    <button type="button" class="btn btn-primario" data-modal-abrir="m-agregar">+ Agregar alojamiento</button>
</div>

<section class="tarjeta">
    <form action="${base}/alojamientos" method="get" class="buscador" role="search">
        <input type="search" name="q" value="<c:out value='${q}'/>" placeholder="Buscar por nombre…" aria-label="Buscar alojamiento por nombre">
        <button type="submit" class="btn btn-secundario">Buscar</button>
        <c:if test="${not empty q}"><a href="${base}/alojamientos" class="enlace">Limpiar</a></c:if>
    </form>

    <c:choose>
        <c:when test="${empty alojamientos}">
            <p class="vacio">
                <c:choose>
                    <c:when test="${not empty q}">No hay alojamientos que coincidan con "<c:out value="${q}"/>".</c:when>
                    <c:otherwise>Todavía no hay alojamientos en el catálogo.</c:otherwise>
                </c:choose>
            </p>
        </c:when>
        <c:otherwise>
            <div class="tabla-scroll">
                <table class="tabla">
                    <thead>
                    <tr>
                        <th>ID</th><th>Alojamiento</th><th>Capacidad</th><th>Precio</th>
                        <th>Ubicación</th><th>Piscina</th><th class="acciones-col">Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="a" items="${alojamientos}">
                        <tr>
                            <td><strong>#${a.id}</strong></td>
                            <td>
                                <strong><c:out value="${a.nombre}"/></strong>
                                <c:if test="${not empty a.descripcion}"><div class="celda-sub"><c:out value="${a.descripcion}"/></div></c:if>
                            </td>
                            <td>${a.capacidad} pers.</td>
                            <td>$ <fmt:formatNumber value="${a.precio}" pattern="#,##0.##"/></td>
                            <td><c:out value="${a.ubicacion}"/></td>
                            <td>${a.distanciaApiscina} m</td>
                            <td class="acciones">
                                <button type="button" class="btn btn-editar" data-modal-abrir="m-editar"
                                        data-f-id="${a.id}"
                                        data-f-nombre="<c:out value='${a.nombre}'/>"
                                        data-f-descripcion="<c:out value='${a.descripcion}'/>"
                                        data-f-capacidad="${a.capacidad}"
                                        data-f-precio="${a.precio}"
                                        data-f-ubicacion="<c:out value='${a.ubicacion}'/>"
                                        data-f-distanciaApiscina="${a.distanciaApiscina}"
                                        data-f-permiteMascotas="${a.permiteMascotas}">Editar</button>
                                <form action="${base}/alojamientos" method="post"
                                      data-confirmar="¿Eliminar el alojamiento &quot;<c:out value='${a.nombre}'/>&quot;? Esta acción no se puede deshacer.">
                                    <input type="hidden" name="_csrf" value="${csrf}">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="id" value="${a.id}">
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

<%-- ============ Agregar ============ --%>
<dialog id="m-agregar" class="modal">
    <form action="${base}/alojamientos" method="post" class="modal-form">
        <h2>Agregar alojamiento</h2>
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="accion" value="agregar">
        <div class="campos">
            <label class="campo ancho">Nombre
                <input type="text" name="nombre" maxlength="100" required></label>
            <label class="campo ancho">Descripción
                <textarea name="descripcion" rows="3" maxlength="1000"></textarea></label>
            <label class="campo">Capacidad (personas)
                <input type="number" name="capacidad" min="1" max="100" required></label>
            <label class="campo">Precio por noche ($)
                <input type="number" name="precio" min="0" step="0.01" required></label>
            <label class="campo">Ubicación
                <input type="text" name="ubicacion" maxlength="150" required></label>
            <label class="campo">Distancia a la piscina (m)
                <input type="number" name="distanciaApiscina" min="0" step="any" value="0"></label>
            <label class="campo">
                <input type="checkbox" name="permiteMascotas">
                Permite mascotas
            </label>
        </div>
        <div class="modal-pie">
            <button type="button" class="btn btn-secundario" data-modal-cerrar>Cancelar</button>
            <button type="submit" class="btn btn-primario">Guardar</button>
        </div>
    </form>
</dialog>

<%-- ============ Editar ============ --%>
<dialog id="m-editar" class="modal">
    <form action="${base}/alojamientos" method="post" class="modal-form">
        <h2>Editar alojamiento</h2>
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="accion" value="editar">
        <input type="hidden" name="id">
        <div class="campos">
            <label class="campo ancho">Nombre
                <input type="text" name="nombre" maxlength="100" required></label>
            <label class="campo ancho">Descripción
                <textarea name="descripcion" rows="3" maxlength="1000"></textarea></label>
            <label class="campo">Capacidad (personas)
                <input type="number" name="capacidad" min="1" max="100" required></label>
            <label class="campo">Precio por noche ($)
                <input type="number" name="precio" min="0" step="0.01" required></label>
            <label class="campo">Ubicación
                <input type="text" name="ubicacion" maxlength="150" required></label>
            <label class="campo">Distancia a la piscina (m)
                <input type="number" name="distanciaApiscina" min="0" step="any"></label>
            <label class="campo">
                <input type="checkbox" name="permiteMascotas">
                Permite mascotas
            </label>
        </div>
        <div class="modal-pie">
            <button type="button" class="btn btn-secundario" data-modal-cerrar>Cancelar</button>
            <button type="submit" class="btn btn-primario">Guardar cambios</button>
        </div>
    </form>
</dialog>

<%@ include file="layout-bottom.jspf" %>
