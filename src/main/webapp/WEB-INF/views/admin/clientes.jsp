<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="layout-top.jspf" %>

<nav class="migas" aria-label="Ruta"><a href="${base}/inicio">Inicio</a> <span>/</span> Clientes</nav>

<div class="pagina-titulo">
    <div>
        <h1>Clientes</h1>
        <p class="muted">Registrá clientes, consultá su historial de reservas y mantené el listado al día.</p>
    </div>
    <button type="button" class="btn btn-primario" data-modal-abrir="m-registrar">+ Registrar cliente</button>
</div>

<%-- ============ Historial de un cliente ============ --%>
<c:if test="${not empty clienteHistorial}">
    <section class="tarjeta destacada">
        <div class="tarjeta-cabecera">
            <h2>Historial de <c:out value="${clienteHistorial.nombre}"/></h2>
            <a href="${base}/clientes" class="enlace">Cerrar historial</a>
        </div>
        <p class="muted">Documento <c:out value="${clienteHistorial.documento}"/> · <c:out value="${clienteHistorial.email}"/></p>

        <c:choose>
            <c:when test="${empty historial}"><p class="vacio">Este cliente todavía no tiene reservas.</p></c:when>
            <c:otherwise>
                <div class="tabla-scroll">
                    <table class="tabla">
                        <thead>
                        <tr><th>#</th><th>Alojamiento</th><th>Ingreso</th><th>Salida</th><th>Huéspedes</th><th>Estado</th><th>Solicitada</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="r" items="${historial}">
                            <tr>
                                <td><strong>#${r.id}</strong></td>
                                <td><c:out value="${r.alojamientoNombre}"/></td>
                                <td><fmt:formatDate value="${r.fechaIngreso}" pattern="dd/MM/yyyy"/></td>
                                <td><fmt:formatDate value="${r.fechaSalida}" pattern="dd/MM/yyyy"/></td>
                                <td>${r.cantidadHuespedes}</td>
                                <td><span class="estado estado-${r.estado}">${r.estado == 'PENDIENTE' ? 'Pendiente' : (r.estado == 'CONFIRMADA' ? 'Confirmada' : 'Cancelada')}</span></td>
                                <td><fmt:formatDate value="${r.fechaSolicitud}" pattern="dd/MM/yyyy"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</c:if>

<section class="tarjeta">
    <form action="${base}/clientes" method="get" class="buscador" role="search">
        <input type="search" name="q" value="<c:out value='${q}'/>" placeholder="Buscar por nombre o documento…" aria-label="Buscar cliente por nombre o documento">
        <button type="submit" class="btn btn-secundario">Buscar</button>
        <c:if test="${not empty q}"><a href="${base}/clientes" class="enlace">Limpiar</a></c:if>
    </form>

    <c:choose>
        <c:when test="${empty clientes}">
            <p class="vacio">
                <c:choose>
                    <c:when test="${not empty q}">No hay clientes que coincidan con "<c:out value="${q}"/>".</c:when>
                    <c:otherwise>Todavía no hay clientes registrados.</c:otherwise>
                </c:choose>
            </p>
        </c:when>
        <c:otherwise>
            <div class="tabla-scroll">
                <table class="tabla">
                    <thead>
                    <tr>
                        <th>ID</th><th>Nombre</th><th>Documento</th><th>Teléfono</th>
                        <th>Correo</th><th>Procedencia</th><th>Localidad</th><th class="acciones-col">Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="cl" items="${clientes}">
                        <tr>
                            <td><strong>#${cl.id}</strong></td>
                            <td>
                                <c:out value="${cl.nombre}"/>
                                <c:if test="${cl.debeCambiarContrasena}"><div class="celda-sub">Aún no cambió la contraseña inicial</div></c:if>
                            </td>
                            <td><c:out value="${cl.documento}"/></td>
                            <td><c:out value="${empty cl.telefono ? '—' : cl.telefono}"/></td>
                            <td><c:out value="${cl.email}"/></td>
                            <td><c:out value="${empty cl.procedencia ? '—' : cl.procedencia}"/></td>
                            <td><c:out value="${empty cl.localidad ? '—' : cl.localidad}"/></td>
                            <td class="acciones">
                                <a href="${base}/clientes?historial=${cl.id}" class="btn btn-editar">Historial</a>
                                <form action="${base}/clientes" method="post"
                                      data-confirmar="¿Eliminar al cliente &quot;<c:out value='${cl.nombre}'/>&quot;? Solo se puede si no tiene reservas.">
                                    <input type="hidden" name="_csrf" value="${csrf}">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="id" value="${cl.id}">
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

<%-- ============ Registrar ============ --%>
<dialog id="m-registrar" class="modal">
    <form action="${base}/clientes" method="post" class="modal-form">
        <h2>Registrar cliente</h2>
        <p class="muted">La cuenta se crea con la contraseña por defecto del sistema. El cliente tendrá que cambiarla la primera vez que ingrese.</p>
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="accion" value="registrar">
        <div class="campos">
            <label class="campo ancho">Nombre completo
                <input type="text" name="nombre" maxlength="100" required></label>
            <label class="campo">Documento (cédula)
                <input type="text" name="documento" maxlength="20" placeholder="1.234.567-2" required></label>
            <label class="campo">Teléfono (celular)
                <input type="tel" name="telefono" maxlength="30" placeholder="099 123 456"></label>
            <label class="campo ancho">Correo electrónico
                <input type="email" name="email" maxlength="100" required></label>
            <label class="campo ancho">Procedencia
                <small>Departamento, provincia o país donde vive.</small>
                <select name="procedencia" required>
                    <option value="">Elegí una opción…</option>
                    <c:forEach var="p" items="${procedencias}">
                        <option value="${fn:escapeXml(p)}"><c:out value="${p}"/></option>
                    </c:forEach>
                </select></label>
            <label class="campo ancho">Localidad
                <small>Ciudad o pueblo donde vive (por ejemplo: Paysandú, Salto o Guichón).</small>
                <input type="text" name="localidad" list="lista-localidades" maxlength="100" placeholder="Ciudad o pueblo" required></label>
            <datalist id="lista-localidades">
                <c:forEach var="sugerida" items="${localidades}"><option value="${fn:escapeXml(sugerida)}"></option></c:forEach>
            </datalist>
        </div>
        <div class="modal-pie">
            <button type="button" class="btn btn-secundario" data-modal-cerrar>Cancelar</button>
            <button type="submit" class="btn btn-primario">Registrar</button>
        </div>
    </form>
</dialog>

<script>
    window.addEventListener('DOMContentLoaded', function () {
        const parametros = new URLSearchParams(window.location.search);

        if (parametros.get('abrir') === 'nuevo') {
            const modalRegistrarCliente =
                document.getElementById('m-registrar');

            if (modalRegistrarCliente && !modalRegistrarCliente.open) {
                modalRegistrarCliente.showModal();
            }
        }
    });
</script>

<%@ include file="layout-bottom.jspf" %>
