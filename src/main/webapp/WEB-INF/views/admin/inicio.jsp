<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="layout-top.jspf" %>

<div class="pagina-titulo">
    <div>
        <h1>Bienvenido al panel</h1>
        <p class="muted"><strong><c:out value="${fechaTexto}"/></strong> · ${totalAlojamientos} alojamientos · ${totalServicios} servicios · ${totalClientes} clientes</p>
    </div>
</div>

<%-- ============ Hoy ============ --%>
<section class="inicio-hoy" aria-label="Resumen de hoy">
    <div class="anillo" style="--pct:${hoyPorcentaje}" role="img" aria-label="${hoyOcupadas} de ${totalAlojamientos} casas ocupadas hoy">
        <div class="anillo-centro"><strong>${hoyOcupadas}/${totalAlojamientos}</strong><span>ocupadas</span></div>
    </div>
    <div class="inicio-hoy-texto">
        <span class="inicio-hoy-etiqueta">Hoy</span>
        <h2>
            <c:choose>
                <c:when test="${hoyLibres == 0}">No quedan casas libres</c:when>
                <c:when test="${hoyLibres == 1}">Queda 1 casa libre</c:when>
                <c:otherwise>Quedan ${hoyLibres} casas libres</c:otherwise>
            </c:choose>
        </h2>
        <ul class="inicio-hoy-datos">
            <li><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M10 7l5 5-5 5M3 12h12M15 4h4a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-4"/></svg><strong>${hoyLlegadas}</strong> ${hoyLlegadas == 1 ? 'llegada' : 'llegadas'}</li>
            <li><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M14 7l-5 5 5 5M21 12H9M9 4H5a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h4"/></svg><strong>${hoySalidas}</strong> ${hoySalidas == 1 ? 'salida' : 'salidas'}</li>
            <li><svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg><strong>${totalPendientes}</strong> ${totalPendientes == 1 ? 'solicitud pendiente' : 'solicitudes pendientes'}</li>
        </ul>
    </div>
    <a class="btn inicio-hoy-boton" href="${base}/disponibilidad">Ver disponibilidad</a>
</section>

<%-- ============ Indicadores ============ --%>
<div class="inicio-kpis">
    <a class="kpi kpi-azul" href="${base}/disponibilidad">
        <span class="kpi-numero">${kpiOcupacion}%</span>
        <span class="kpi-texto">Ocupación de <c:out value="${kpiMesNombre}"/></span>
        <span class="kpi-barra" aria-hidden="true"><span style="width:${kpiOcupacion}%"></span></span>
    </a>
    <a class="kpi kpi-verde" href="${base}/reservas?estado=CONFIRMADA">
        <span class="kpi-numero"><c:out value="${kpiIngresos}"/></span>
        <span class="kpi-texto">Ingresos estimados de <c:out value="${kpiMesNombre}"/></span>
        <span class="kpi-pie">${kpiNochesVendidas} ${kpiNochesVendidas == 1 ? 'noche vendida' : 'noches vendidas'} · reservas confirmadas</span>
    </a>
    <a class="kpi kpi-naranja" href="${base}/reservas?estado=PENDIENTE">
        <span class="kpi-numero">${totalPendientes}</span>
        <span class="kpi-texto">Solicitudes pendientes</span>
        <span class="kpi-pie">Esperan una decisión</span>
    </a>
    <a class="kpi kpi-violeta" href="${base}/senas">
        <span class="kpi-numero">${kpiSenas}</span>
        <span class="kpi-texto">Señas a favor vigentes</span>
        <span class="kpi-pie">Total <c:out value="${kpiSenasTotal}"/></span>
    </a>
</div>

<div class="inicio-grilla">

    <%-- ============ Noches ocupadas por mes ============ --%>
    <section class="tarjeta inicio-doble">
        <h2>Noches ocupadas por mes</h2>
        <p class="muted inicio-sub">Reservas confirmadas en ${anioResumen} · ${nochesAnio} noches en total</p>
        <ol class="ini-meses">
            <c:forEach var="mes" items="${mesesBarras}">
                <li class="ini-mes ini-mes-${mes.tiempo}" title="${mes.nombre}: ${mes.noches} noches ocupadas (${mes.porcentaje}% de ocupación)">
                    <span class="ini-mes-valor">${mes.noches}</span>
                    <span class="ini-mes-caja"><span class="ini-mes-barra" style="height:${mes.alto}%"></span></span>
                    <span class="ini-mes-nombre">${mes.nombre}</span>
                    <span class="ini-mes-pct">${mes.porcentaje}%</span>
                </li>
            </c:forEach>
        </ol>
        <p class="muted inicio-nota">Debajo de cada mes: porcentaje de ocupación. Los meses que vienen muestran lo ya reservado.</p>
    </section>

    <%-- ============ Estado de las reservas ============ --%>
    <section class="tarjeta">
        <h2>Estado de las reservas</h2>
        <div class="dona" style="background:${estadoDona}" role="img"
             aria-label="${estadoConfirmadas} confirmadas, ${estadoPendientes} pendientes, ${estadoCanceladas} canceladas">
            <div class="dona-centro"><strong>${estadoTotal}</strong><span>reservas</span></div>
        </div>
        <ul class="dona-leyenda">
            <li><span class="punto punto-verde"></span><span class="dona-nombre">Confirmadas</span><strong>${estadoConfirmadas}</strong><span class="dona-pct">${estadoPConfirmadas}%</span></li>
            <li><span class="punto punto-naranja"></span><span class="dona-nombre">Pendientes</span><strong>${estadoPendientes}</strong><span class="dona-pct">${estadoPPendientes}%</span></li>
            <li><span class="punto punto-rojo"></span><span class="dona-nombre">Canceladas</span><strong>${estadoCanceladas}</strong><span class="dona-pct">${estadoPCanceladas}%</span></li>
        </ul>
    </section>

    <%-- ============ Ocupación de los próximos 14 días ============ --%>
    <section class="tarjeta inicio-doble">
        <div class="tarjeta-cabecera">
            <div>
                <h2>Ocupación de los próximos 14 días</h2>
                <p class="muted inicio-sub">Porcentaje de casas ocupadas cada noche</p>
            </div>
            <a href="${base}/disponibilidad" class="enlace">Ver planilla</a>
        </div>
        <ol class="ini-dias">
            <c:forEach var="dia" items="${proximosDias}">
                <li class="ini-dia ${dia.finDeSemana ? 'ini-dia-fin' : ''} ${dia.hoy ? 'ini-dia-hoy' : ''}" title="<c:out value='${dia.titulo}'/>">
                    <span class="ini-dia-pila" aria-hidden="true">
                        <span class="ini-dia-pendientes" style="height:${dia.altoPendientes}%"></span>
                        <span class="ini-dia-confirmadas" style="height:${dia.altoConfirmadas}%"></span>
                    </span>
                    <span class="ini-dia-letra">${dia.letra}</span>
                    <span class="ini-dia-numero">${dia.numero}</span>
                    <span class="solo-lectores"><c:out value="${dia.titulo}"/></span>
                </li>
            </c:forEach>
        </ol>
        <ul class="leyenda-inicio">
            <li><span class="punto punto-azul"></span>Confirmadas</li>
            <li><span class="punto punto-naranja"></span>Solicitudes pendientes</li>
            <li><span class="punto punto-libre"></span>Libres</li>
        </ul>
    </section>

    <%-- ============ Casas más reservadas ============ --%>
    <section class="tarjeta">
        <h2>Casas más reservadas</h2>
        <p class="muted inicio-sub">Noches confirmadas en ${anioResumen}</p>
        <c:choose>
            <c:when test="${empty casasMasReservadas}"><p class="vacio">Todavía no hay reservas confirmadas este año.</p></c:when>
            <c:otherwise>
                <ol class="barras-inicio">
                    <c:forEach var="fila" items="${casasMasReservadas}">
                        <li>
                            <span class="barras-inicio-fila"><span class="barras-inicio-nombre"><c:out value="${fila.nombre}"/></span><strong>${fila.cantidad}</strong></span>
                            <span class="barras-inicio-pista" aria-hidden="true"><span class="barras-inicio-barra" style="width:${fila.ancho}%"></span></span>
                        </li>
                    </c:forEach>
                </ol>
            </c:otherwise>
        </c:choose>
    </section>

    <%-- ============ Próximas llegadas ============ --%>
    <section class="tarjeta">
        <div class="tarjeta-cabecera">
            <h2>Próximas llegadas</h2>
            <a href="${base}/reservas?estado=CONFIRMADA" class="enlace">Ver reservas</a>
        </div>
        <c:choose>
            <c:when test="${empty proximasLlegadas}"><p class="vacio">No hay llegadas confirmadas próximamente.</p></c:when>
            <c:otherwise>
                <ul class="lista-inicio">
                    <c:forEach var="llegada" items="${proximasLlegadas}">
                        <li>
                            <span class="fecha-chip"><strong>${llegada.dia}</strong><small>${llegada.mes}</small></span>
                            <span class="lista-inicio-texto">
                                <strong><c:out value="${llegada.cliente}"/></strong>
                                <small><c:out value="${llegada.detalle}"/></small>
                            </span>
                            <span class="ini-etiqueta ${llegada.pronto ? 'ini-etiqueta-ya' : ''}"><c:out value="${llegada.cuando}"/></span>
                        </li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
    </section>

    <%-- ============ ¿De dónde vienen? (procedencia) ============ --%>
    <section class="tarjeta">
        <h2>¿De dónde vienen?</h2>
        <p class="muted inicio-sub">Procedencia en reservas confirmadas de ${anioResumen}</p>
        <c:choose>
            <c:when test="${empty procedenciaBarras}"><p class="vacio">Todavía no hay reservas confirmadas este año.</p></c:when>
            <c:otherwise>
                <ol class="barras-inicio barras-inicio-naranja">
                    <c:forEach var="fila" items="${procedenciaBarras}">
                        <li>
                            <span class="barras-inicio-fila"><span class="barras-inicio-nombre"><c:out value="${fila.nombre}"/></span><strong>${fila.cantidad}</strong></span>
                            <span class="barras-inicio-pista" aria-hidden="true"><span class="barras-inicio-barra ${fila.sinDato ? 'sin-dato' : ''}" style="width:${fila.ancho}%"></span></span>
                        </li>
                    </c:forEach>
                </ol>
            </c:otherwise>
        </c:choose>
    </section>

    <%-- ============ Últimas solicitudes ============ --%>
    <section class="tarjeta inicio-ultimas">
        <div class="tarjeta-cabecera">
            <h2>Últimas solicitudes</h2>
            <a href="${base}/reservas?estado=PENDIENTE" class="enlace">Ver todas</a>
        </div>
        <c:choose>
            <c:when test="${empty ultimasPendientes}"><p class="vacio">No hay solicitudes pendientes.</p></c:when>
            <c:otherwise>
                <ul class="lista-inicio">
                    <c:forEach var="r" items="${ultimasPendientes}">
                        <li>
                            <span class="numero-chip">#${r.id}</span>
                            <span class="lista-inicio-texto">
                                <strong><c:out value="${empty r.clienteNombre ? 'Sin cliente' : r.clienteNombre}"/></strong>
                                <small><c:out value="${r.alojamientoNombre}"/> · <fmt:formatDate value="${r.fechaIngreso}" pattern="dd/MM"/> al <fmt:formatDate value="${r.fechaSalida}" pattern="dd/MM"/> · ${r.cantidadHuespedes} ${r.cantidadHuespedes == 1 ? 'persona' : 'personas'}</small>
                            </span>
                        </li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
    </section>
</div>

<%@ include file="grafica-localidades.jspf" %>

<%@ include file="layout-bottom.jspf" %>
