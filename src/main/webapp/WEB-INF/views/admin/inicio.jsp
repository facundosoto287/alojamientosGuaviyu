<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="layout-top.jspf" %>

<div class="pagina-titulo">
    <div>
        <h1>Bienvenido al panel</h1>
        <p class="muted">Resumen del sistema y solicitudes que esperan una decisión.</p>
    </div>
</div>

<div class="tarjetas-resumen">
    <a class="resumen resumen-pendientes" href="${base}/reservas?estado=PENDIENTE">
        <span class="resumen-numero">${totalPendientes}</span>
        <span>Solicitudes pendientes</span>
    </a>
    <a class="resumen resumen-confirmadas" href="${base}/reservas?estado=CONFIRMADA">
        <span class="resumen-numero">${totalConfirmadas}</span>
        <span>Reservas confirmadas</span>
    </a>
    <a class="resumen resumen-alojamientos" href="${base}/alojamientos">
        <span class="resumen-numero">${totalAlojamientos}</span>
        <span>Alojamientos</span>
    </a>
    <a class="resumen resumen-servicios" href="${base}/servicios">
        <span class="resumen-numero">${totalServicios}</span>
        <span>Servicios</span>
    </a>
    <a class="resumen resumen-clientes" href="${base}/clientes">
        <span class="resumen-numero">${totalClientes}</span>
        <span>Clientes</span>
    </a>
</div>

<section class="tarjeta">
    <div class="tarjeta-cabecera">
        <h2>Últimas solicitudes pendientes</h2>
        <a href="${base}/reservas?estado=PENDIENTE" class="enlace">Ver todas</a>
    </div>

    <c:choose>
        <c:when test="${empty ultimasPendientes}">
            <p class="vacio">No hay solicitudes pendientes.</p>
        </c:when>
        <c:otherwise>
            <div class="tabla-scroll">
                <table class="tabla">
                    <thead>
                    <tr><th>#</th><th>Alojamiento</th><th>Cliente</th><th>Ingreso</th><th>Salida</th><th>Huéspedes</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="r" items="${ultimasPendientes}">
                        <tr>
                            <td><strong>#${r.id}</strong></td>
                            <td><c:out value="${r.alojamientoNombre}"/></td>
                            <td><c:out value="${empty r.clienteNombre ? '—' : r.clienteNombre}"/></td>
                            <td><fmt:formatDate value="${r.fechaIngreso}" pattern="dd/MM/yyyy"/></td>
                            <td><fmt:formatDate value="${r.fechaSalida}" pattern="dd/MM/yyyy"/></td>
                            <td>${r.cantidadHuespedes}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="layout-bottom.jspf" %>
