<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="layout-top.jspf" %>

<%-- "filtros" (lo arma el servlet) conserva la búsqueda y el alojamiento al cambiar de pestaña --%>

<nav class="migas" aria-label="Ruta"><a href="${base}/inicio">Inicio</a> <span>/</span> Reservas</nav>

<div class="pagina-titulo">
    <div>
        <h1>Reservas</h1>
        <p class="muted">Solicitudes de los clientes y reservas registradas. Confirmá, cancelá o creá una nueva.</p>
    </div>
    <button type="button" class="btn btn-primario" data-modal-abrir="m-crear">+ Nueva reserva</button>
</div>

<section class="tarjeta">
    <div class="pestanas" role="tablist">
        <a href="${base}/reservas?x=1${filtros}" class="${empty estadoSel ? 'activo' : ''}">Todas</a>
        <a href="${base}/reservas?estado=PENDIENTE${filtros}" class="${estadoSel == 'PENDIENTE' ? 'activo' : ''}">Pendientes</a>
        <a href="${base}/reservas?estado=CONFIRMADA${filtros}" class="${estadoSel == 'CONFIRMADA' ? 'activo' : ''}">Confirmadas</a>
        <a href="${base}/reservas?estado=CANCELADA${filtros}" class="${estadoSel == 'CANCELADA' ? 'activo' : ''}">Canceladas</a>
    </div>

    <form action="${base}/reservas" method="get" class="buscador" role="search">
        <c:if test="${not empty estadoSel}"><input type="hidden" name="estado" value="${estadoSel}"></c:if>
        <input type="search" name="q" value="<c:out value='${q}'/>" placeholder="Buscar por nombre o documento del cliente…" aria-label="Buscar reservas por cliente">
        <select name="alojamiento" aria-label="Filtrar por alojamiento">
            <option value="">Todos los alojamientos</option>
            <c:forEach var="a" items="${alojamientos}">
                <option value="${a.id}" ${a.id == alojamientoSel ? 'selected' : ''}><c:out value="${a.nombre}"/></option>
            </c:forEach>
        </select>
        <button type="submit" class="btn btn-secundario">Filtrar</button>
        <c:if test="${not empty q or not empty alojamientoSel}">
            <a href="${base}/reservas${empty estadoSel ? '' : '?estado='}${estadoSel}" class="enlace">Limpiar</a>
        </c:if>
    </form>

    <c:choose>
        <c:when test="${empty reservas}"><p class="vacio">No hay reservas para mostrar con estos filtros.</p></c:when>
        <c:otherwise>
            <div class="tabla-scroll">
                <table class="tabla">
                    <thead>
                    <tr>
                        <th>#</th><th>Alojamiento</th><th>Cliente</th><th>Ingreso</th><th>Salida</th>
                        <th>Huéspedes</th><th>Estado</th><th>Solicitada</th><th class="acciones-col">Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="r" items="${reservas}">
                        <tr>
                            <td><strong>#${r.id}</strong></td>
                            <td><c:out value="${r.alojamientoNombre}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty r.clienteNombre}">
                                        <a href="${base}/clientes?historial=${r.clienteId}" class="enlace"><c:out value="${r.clienteNombre}"/></a>
                                        <div class="celda-sub"><c:out value="${r.clienteDocumento}"/></div>
                                    </c:when>
                                    <c:otherwise>—</c:otherwise>
                                </c:choose>
                            </td>
                            <td><fmt:formatDate value="${r.fechaIngreso}" pattern="dd/MM/yyyy"/></td>
                            <td><fmt:formatDate value="${r.fechaSalida}" pattern="dd/MM/yyyy"/></td>
                            <td>${r.cantidadHuespedes}</td>
                            <td><span class="estado estado-${r.estado}">${r.estado == 'PENDIENTE' ? 'Pendiente' : (r.estado == 'CONFIRMADA' ? 'Confirmada' : 'Cancelada')}</span></td>
                            <td><fmt:formatDate value="${r.fechaSolicitud}" pattern="dd/MM/yyyy"/></td>
                            <td class="acciones">
                                <c:if test="${r.estado == 'PENDIENTE'}">
                                    <form action="${base}/reservas" method="post"
                                          data-confirmar="¿Confirmar la reserva #${r.id}? Las fechas quedarán ocupadas.">
                                        <input type="hidden" name="_csrf" value="${csrf}">
                                        <input type="hidden" name="accion" value="confirmar">
                                        <input type="hidden" name="id" value="${r.id}">
                                        <input type="hidden" name="fEstado" value="${estadoSel}">
                                        <input type="hidden" name="fQ" value="<c:out value='${q}'/>">
                                        <input type="hidden" name="fAlojamiento" value="${alojamientoSel}">
                                        <button type="submit" class="btn btn-ok">Confirmar</button>
                                    </form>
                                </c:if>
                                <c:if test="${r.estado != 'CANCELADA'}">
                                    <form action="${base}/reservas" method="post"
                                          data-confirmar="¿Cancelar la reserva #${r.id}?${r.estado == 'CONFIRMADA' ? ' Sus fechas volverán a quedar libres.' : ''}">
                                        <input type="hidden" name="_csrf" value="${csrf}">
                                        <input type="hidden" name="accion" value="cancelar">
                                        <input type="hidden" name="id" value="${r.id}">
                                        <input type="hidden" name="fEstado" value="${estadoSel}">
                                        <input type="hidden" name="fQ" value="<c:out value='${q}'/>">
                                        <input type="hidden" name="fAlojamiento" value="${alojamientoSel}">
                                        <button type="submit" class="btn btn-eliminar">Cancelar</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<%-- ============ Nueva reserva ============ --%>
<dialog id="m-crear" class="modal">
    <form action="${base}/reservas" method="post" class="modal-form">
        <h2>Nueva reserva</h2>
        <p class="muted">Usala también para reservas acordadas por teléfono, WhatsApp u otro medio: quedan confirmadas y bloquean las fechas.</p>
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="accion" value="crear">
        <input type="hidden" name="fEstado" value="${estadoSel}">
        <input type="hidden" name="fQ" value="<c:out value='${q}'/>">
        <input type="hidden" name="fAlojamiento" value="${alojamientoSel}">

        <div class="campos">
            <label class="campo ancho">Cliente
                <select name="clienteId" required>
                    <option value="">Elegí un cliente…</option>
                    <c:forEach var="c" items="${clientes}">
                        <option value="${c.id}"><c:out value="${c.nombre}"/> — <c:out value="${c.documento}"/></option>
                    </c:forEach>
                </select>
                <small>¿No está? Registralo primero en <a href="${base}/clientes">Clientes</a>.</small>
            </label>
            <label class="campo ancho">Alojamiento
                <select name="alojamientoId" required>
                    <option value="">Elegí un alojamiento…</option>
                    <c:forEach var="a" items="${alojamientos}">
                        <option value="${a.id}"><c:out value="${a.nombre}"/> (hasta ${a.capacidad} pers.)</option>
                    </c:forEach>
                </select>
            </label>
            <label class="campo">Ingreso
                <input type="date" name="fechaIngreso" min="${hoy}" required></label>
            <label class="campo">Salida
                <input type="date" name="fechaSalida" min="${hoy}" required></label>
            <label class="campo">Huéspedes
                <input type="number" name="huespedes" min="1" max="100" required></label>
            <fieldset class="campo opciones">
                <legend>Estado inicial</legend>
                <label><input type="radio" name="estadoInicial" value="CONFIRMADA" checked> Confirmada (bloquea las fechas)</label>
                <label><input type="radio" name="estadoInicial" value="PENDIENTE"> Pendiente (queda como solicitud)</label>
            </fieldset>
        </div>

        <div class="modal-pie">
            <button type="button" class="btn btn-secundario" data-modal-cerrar>Cancelar</button>
            <button type="submit" class="btn btn-primario">Crear reserva</button>
        </div>
    </form>
</dialog>

<%@ include file="layout-bottom.jspf" %>
