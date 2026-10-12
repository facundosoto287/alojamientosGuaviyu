<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="layout-top.jspf" %>

<nav class="migas" aria-label="Ruta"><a href="${base}/inicio">Inicio</a> <span>/</span> Disponibilidad</nav>

<div class="pagina-titulo">
    <div>
        <h1>Disponibilidad</h1>
        <p class="muted">Consultá qué fechas están libres y cuáles ocupadas en cada alojamiento.</p>
    </div>
</div>

<section class="tarjeta">
    <form action="${base}/disponibilidad" method="get" class="campo-simple">
        <label for="sel-alojamiento">Alojamiento</label>
        <select id="sel-alojamiento" name="alojamiento" data-autoenviar>
            <option value="">Elegí un alojamiento…</option>
            <c:forEach var="a" items="${alojamientos}">
                <option value="${a.id}" ${a.id == alojamientoSel ? 'selected' : ''}><c:out value="${a.nombre}"/></option>
            </c:forEach>
        </select>
        <noscript><button type="submit" class="btn btn-secundario">Ver</button></noscript>
    </form>
</section>

<c:if test="${not empty alojamiento}">
    <section class="tarjeta">
        <div class="calendario-cabecera">
            <a class="btn btn-secundario" href="${base}/disponibilidad?alojamiento=${alojamientoSel}&mes=${mesAnterior}">&larr; Anterior</a>
            <h2 class="mes-titulo"><c:out value="${mesTexto}"/></h2>
            <a class="btn btn-secundario" href="${base}/disponibilidad?alojamiento=${alojamientoSel}&mes=${mesSiguiente}">Siguiente &rarr;</a>
        </div>

        <ul class="leyenda">
            <li><span class="muestra libre"></span> Libre</li>
            <li><span class="muestra ocupado"></span> Ocupado (reserva confirmada)</li>
            <li><span class="muestra pendiente"></span> Solicitud pendiente</li>
        </ul>

        <table class="calendario" aria-label="Calendario de <c:out value='${alojamiento.nombre}'/>">
            <thead>
            <tr><th>Lun</th><th>Mar</th><th>Mié</th><th>Jue</th><th>Vie</th><th>Sáb</th><th>Dom</th></tr>
            </thead>
            <tbody>
            <c:forEach var="semana" items="${semanas}">
                <tr>
                    <c:forEach var="dia" items="${semana}">
                        <td class="dia ${dia.delMes ? dia.estado : 'fuera'} ${dia.hoy ? 'hoy' : ''}">${dia.numero}</td>
                    </c:forEach>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <p class="muted nota">El día de salida queda libre para un nuevo ingreso.</p>
    </section>

    <section class="tarjeta">
        <div class="tarjeta-cabecera"><h2>Próximas reservas de <c:out value="${alojamiento.nombre}"/></h2></div>
        <c:choose>
            <c:when test="${empty proximas}"><p class="vacio">No hay reservas vigentes ni próximas para este alojamiento.</p></c:when>
            <c:otherwise>
                <div class="tabla-scroll">
                    <table class="tabla">
                        <thead>
                        <tr><th>#</th><th>Cliente</th><th>Ingreso</th><th>Salida</th><th>Huéspedes</th><th>Estado</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="r" items="${proximas}">
                            <tr>
                                <td><strong>#${r.id}</strong></td>
                                <td><c:out value="${empty r.clienteNombre ? '—' : r.clienteNombre}"/></td>
                                <td><fmt:formatDate value="${r.fechaIngreso}" pattern="dd/MM/yyyy"/></td>
                                <td><fmt:formatDate value="${r.fechaSalida}" pattern="dd/MM/yyyy"/></td>
                                <td>${r.cantidadHuespedes}</td>
                                <td><span class="estado estado-${r.estado}">${r.estado == 'PENDIENTE' ? 'Pendiente' : 'Confirmada'}</span></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</c:if>

<%@ include file="layout-bottom.jspf" %>
