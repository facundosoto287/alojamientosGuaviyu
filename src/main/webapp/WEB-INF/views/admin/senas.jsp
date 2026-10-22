<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="layout-top.jspf" %>

<%-- Señas a favor: reservas confirmadas que el cliente tuvo que cancelar. Valen 6 meses desde la fecha de ingreso, para cualquier casa. --%>

<nav class="migas" aria-label="Ruta"><a href="${base}/inicio">Inicio</a> <span>/</span> Señas a favor</nav>

<div class="pagina-titulo">
    <div>
        <h1>Señas a favor</h1>
        <p class="muted">Cuando un cliente cancela una reserva confirmada, su seña se guarda 6 meses desde la fecha de ingreso
            y la puede usar en cualquier casa. Se agregan solas al cancelar una reserva confirmada.</p>
    </div>
</div>

<c:if test="${totalPorVencer > 0}">
    <div class="aviso aviso-error" role="status">
        <span>⚠ ${totalPorVencer} ${totalPorVencer == 1 ? 'seña vence' : 'señas vencen'} en los próximos 30 días.</span>
    </div>
</c:if>

<section class="tarjeta">
    <div class="pestanas" role="tablist">
        <a href="${base}/senas?estado=VIGENTE${filtroQ}" class="${estadoSel == 'VIGENTE' ? 'activo' : ''}">Vigentes (${totalVigentes})</a>
        <a href="${base}/senas?estado=USADA${filtroQ}" class="${estadoSel == 'USADA' ? 'activo' : ''}">Usadas</a>
        <a href="${base}/senas?estado=VENCIDA${filtroQ}" class="${estadoSel == 'VENCIDA' ? 'activo' : ''}">Vencidas</a>
        <a href="${base}/senas?estado=TODAS${filtroQ}" class="${estadoSel == 'TODAS' ? 'activo' : ''}">Todas</a>
    </div>

    <form action="${base}/senas" method="get" class="buscador" role="search">
        <input type="hidden" name="estado" value="${estadoSel}">
        <input type="search" name="q" value="<c:out value='${q}'/>" placeholder="Buscar por nombre o documento del cliente…" aria-label="Buscar señas por cliente">
        <button type="submit" class="btn btn-secundario">Buscar</button>
        <c:if test="${not empty q}"><a href="${base}/senas?estado=${estadoSel}" class="enlace">Limpiar</a></c:if>
    </form>

    <c:choose>
        <c:when test="${empty senas}"><p class="vacio">No hay señas para mostrar.</p></c:when>
        <c:otherwise>
            <div class="tabla-scroll">
                <table class="tabla tabla-senas">
                    <thead>
                    <tr>
                        <th>Fecha de ingreso</th><th>Nombre y apellido</th><th>Celular</th><th>Importe</th>
                        <th>Válida hasta</th><th>Casa</th><th>Estado</th><th class="acciones-col">Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="s" items="${senas}">
                        <tr class="${s.porVencer ? 'por-vencer' : ''}">
                            <td>${s.fechaIngresoTexto}<div class="celda-sub">Reserva #${s.reservaId}</div></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty s.clienteId}">
                                        <a href="${base}/clientes?historial=${s.clienteId}" class="enlace"><c:out value="${s.nombreMostrar}"/></a>
                                        <div class="celda-sub"><c:out value="${s.clienteDocumento}"/></div>
                                    </c:when>
                                    <c:otherwise>Sin cliente</c:otherwise>
                                </c:choose>
                            </td>
                            <td><c:out value="${empty s.clienteTelefono ? '—' : s.clienteTelefono}"/></td>
                            <td><strong>${s.importeTexto}</strong></td>
                            <td>
                                ${s.validaHastaTexto}
                                <c:if test="${s.vigente}">
                                    <div class="celda-sub ${s.porVencer ? 'texto-alerta' : ''}">
                                        <c:choose>
                                            <c:when test="${s.diasRestantes == 0}">Vence hoy</c:when>
                                            <c:when test="${s.diasRestantes == 1}">Queda 1 día</c:when>
                                            <c:otherwise>Quedan ${s.diasRestantes} días</c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>
                            </td>
                            <td><c:out value="${empty s.alojamientoNombre ? '—' : s.alojamientoNombre}"/></td>
                            <td>
                                <span class="estado estado-sena-${s.estado}">
                                    ${s.estado == 'VIGENTE' ? 'Vigente' : (s.estado == 'USADA' ? 'Usada' : 'Vencida')}
                                </span>
                            </td>
                            <td class="acciones">
                                <c:if test="${s.vigente and not empty s.telefonoWhatsapp}">
                                    <a class="btn btn-ok" target="_blank" rel="noopener"
                                       href="https://wa.me/${s.telefonoWhatsapp}">💬 WhatsApp</a>
                                </c:if>
                                <c:if test="${not s.usada}">
                                    <form action="${base}/senas" method="post"
                                          data-confirmar="¿Marcar como usada la seña de ${s.importeTexto}?">
                                        <input type="hidden" name="_csrf" value="${csrf}">
                                        <input type="hidden" name="accion" value="usada">
                                        <input type="hidden" name="id" value="${s.id}">
                                        <input type="hidden" name="fEstado" value="${estadoSel}">
                                        <input type="hidden" name="fQ" value="<c:out value='${q}'/>">
                                        <button type="submit" class="btn btn-editar">Marcar usada</button>
                                    </form>
                                </c:if>
                                <c:if test="${s.usada}">
                                    <form action="${base}/senas" method="post">
                                        <input type="hidden" name="_csrf" value="${csrf}">
                                        <input type="hidden" name="accion" value="disponible">
                                        <input type="hidden" name="id" value="${s.id}">
                                        <input type="hidden" name="fEstado" value="${estadoSel}">
                                        <input type="hidden" name="fQ" value="<c:out value='${q}'/>">
                                        <button type="submit" class="btn btn-secundario">Deshacer</button>
                                    </form>
                                </c:if>
                                <form action="${base}/senas" method="post"
                                      data-confirmar="¿Eliminar esta seña? Usalo solo si se cargó por error.">
                                    <input type="hidden" name="_csrf" value="${csrf}">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="id" value="${s.id}">
                                    <input type="hidden" name="fEstado" value="${estadoSel}">
                                    <input type="hidden" name="fQ" value="<c:out value='${q}'/>">
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

<style>
    .tabla-senas tr.por-vencer { background: #fff8e6; }
    .texto-alerta { color: #a15c00 !important; font-weight: 700; }
    .estado-sena-VIGENTE { background: var(--verde-fondo); color: var(--verde); }
    .estado-sena-USADA { background: var(--azul-100); color: var(--azul-700); }
    .estado-sena-VENCIDA { background: #eceff3; color: #5b6475; }
    /* Acciones una debajo de la otra, para que siempre se vean todas */
    .tabla-senas td.acciones { white-space: normal; width: 9rem; }
    .tabla-senas td.acciones > * { display: block; margin: .25rem 0; }
    .tabla-senas td.acciones .btn { width: 100%; padding: .4rem .6rem; font-size: .88rem; text-align: center; }
</style>

<%@ include file="layout-bottom.jspf" %>
