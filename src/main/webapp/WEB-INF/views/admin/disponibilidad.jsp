<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="layout-top.jspf" %>
<%-- Estilos de la vista "Todas las casas". Van acá adentro para que siempre se carguen junto con la página. --%>
<style>
/* Disponibilidad · vista Todas las casas (planilla de ocupación). Se carga solo desde disponibilidad.jsp */

/* ===================== PLANILLA DE OCUPACIÓN ===================== */
/* Todas las casas (columnas) y los días del mes (filas), como la planilla de Excel */

.solo-lector {
    position: absolute; width: 1px; height: 1px; overflow: hidden;
    clip: rect(0 0 0 0); white-space: nowrap;
}

.planilla-acciones { display: flex; flex-wrap: wrap; gap: .5rem; }

/* Año y pestañas de meses (como las hojas del Excel) */
.planilla-barra { display: flex; flex-wrap: wrap; align-items: center; gap: .8rem 1.2rem; margin-bottom: .8rem; }
.planilla-anio { display: flex; align-items: center; gap: .4rem; }
.planilla-anio strong { font: 800 1.15rem var(--fuente-titulos); color: var(--azul-900); }
.planilla-anio a {
    width: 34px; height: 34px; display: grid; place-items: center;
    border-radius: 8px; background: #fff; border: 1px solid var(--linea);
    font-size: 1.3rem; font-weight: 800; text-decoration: none;
}
.meses { display: flex; flex-wrap: wrap; gap: .3rem; }
.meses a {
    padding: .45rem .8rem; border-radius: 8px; background: #fff; border: 1px solid var(--linea);
    color: var(--azul-900); font: 700 .85rem var(--fuente-texto); text-decoration: none;
    text-transform: uppercase; letter-spacing: .03em;
}
.meses a:hover { background: var(--azul-50); }
.meses a.activo { background: var(--azul-700); border-color: var(--azul-700); color: #fff; }
.planilla-mes { font-size: 1.3rem; margin: .2rem 0 .8rem; text-transform: capitalize; }

/* Resumen de hoy */
.resumen-hoy { display: flex; flex-wrap: wrap; gap: .8rem; margin-bottom: 1rem; }
.resumen-hoy div {
    display: flex; align-items: center; gap: .6rem; padding: .7rem 1rem;
    background: #fff; border: 1px solid var(--linea); border-radius: 12px; font-size: 1rem;
}
.resumen-hoy strong { font: 800 1.6rem var(--fuente-titulos); }
.resumen-hoy .libres strong { color: var(--verde); }
.resumen-hoy .ocupadas strong { color: var(--azul-700); }
.resumen-hoy .pendientes strong { color: var(--amarillo); }

/* Leyenda */
.leyenda-planilla { display: flex; flex-wrap: wrap; gap: .6rem 1.4rem; margin-bottom: .8rem; font-size: .95rem; }
.leyenda-planilla span { display: inline-flex; align-items: center; gap: .45rem; }
.planilla-muestra { width: 28px; height: 20px; border-radius: 5px; display: inline-grid; place-items: center; font-size: .75rem; font-style: normal; }
.planilla-muestra.libre { background: #fff; border: 1px solid var(--linea); }
.planilla-muestra.confirmada { background: var(--azul-700); color: #fff; }
.planilla-muestra.pendiente { background: repeating-linear-gradient(45deg, #fff3d6, #fff3d6 4px, #ffe4a3 4px, #ffe4a3 8px); border: 2px dashed #c98a00; }
.planilla-muestra.feriado { background: #ffcc99; }

/* La grilla */
.planilla-caja { overflow: auto; max-height: 72vh; border: 1px solid var(--linea); border-radius: 14px; background: #fff; }
.planilla { border-collapse: separate; border-spacing: 0; width: 100%; min-width: 1400px; table-layout: fixed; }
.planilla th, .planilla td { border-right: 1px solid #e3e8f0; border-bottom: 1px solid #e3e8f0; padding: 0; vertical-align: top; }

/* Encabezado fijo con el nombre de cada casa */
.planilla thead th {
    position: sticky; top: 0; z-index: 3; padding: .7rem .3rem;
    font: 800 .72rem var(--fuente-titulos); color: var(--azul-950);
    text-align: center; text-transform: uppercase; border-bottom: 2px solid #b9c4d6;
}

/* Columna de días fija a la izquierda */
.planilla .dia {
    position: sticky; left: 0; z-index: 2; width: 74px; padding: .45rem .5rem;
    background: #fff; font: 800 1rem var(--fuente-titulos); color: var(--azul-900);
    white-space: nowrap; text-align: left;
}
.planilla .dia small { display: block; font: 700 .7rem var(--fuente-texto); color: var(--texto-suave); white-space: normal; line-height: 1.1; }
.planilla thead .dia { z-index: 4; }
.planilla tr.finde .dia { background: #f3f6fb; }
.planilla tr.feriado .dia, .planilla tr.feriado td.libre { background: #ffcc99 !important; }
.planilla tr.feriado .dia small { color: #8a4b00; }
.planilla tr.hoy .dia { background: var(--azul-700); color: #fff; }
.planilla tr.hoy .dia small { color: #dbe6ff; }
.planilla tr.hoy td { border-bottom: 2px solid var(--azul-700); }

/* Lugar libre: botón que ocupa toda la celda */
.planilla td.libre { height: 46px; }
.celda-libre {
    display: grid; width: 100%; height: 100%; min-height: 46px; padding: 4px;
    border: 0; background: transparent; cursor: pointer;
}
.celda-libre > span { grid-area: 1 / 1; }
.celda-libre .celda-mas {
    display: grid; place-items: center; height: 100%; border-radius: 8px;
    color: var(--azul-700); font: 700 .8rem var(--fuente-texto); opacity: 0;
}
.celda-libre:hover .celda-mas, .celda-libre:focus-visible .celda-mas {
    opacity: 1; border: 2px dashed var(--azul-600); background: rgba(255, 255, 255, .85);
}
.celda-libre:focus-visible { outline: 3px solid var(--naranja); outline-offset: -3px; }

/* Bloque de reserva: ocupa todas las noches (nombres propios para no chocar con .bloque y .estado del panel) */
.planilla td.reserva { position: relative; height: 46px; }
.reserva-bloque {
    position: absolute; top: 4px; left: 4px;
    width: calc(100% - 8px); height: calc(100% - 8px);      /* ocupa todas las noches de la reserva */
    display: flex; flex-direction: column; justify-content: flex-start; align-items: stretch;
    border: 0; border-radius: 9px; padding: .45rem .55rem;
    text-align: left; font: 400 .85rem/1.25 var(--fuente-texto); overflow: hidden; cursor: pointer;
}
.reserva-bloque:focus-visible { outline: 3px solid var(--naranja); outline-offset: 1px; }
.reserva-bloque strong {
    display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
    padding-right: 1rem; font: 700 .88rem var(--fuente-texto);
}
.reserva-bloque .info { display: block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-size: .76rem; }
.reserva-bloque .bloque-estado { position: absolute; top: .3rem; right: .4rem; font-weight: 800; font-size: .85rem; }
.reserva-bloque.confirmada { background: var(--azul-700); color: #fff; box-shadow: 0 3px 8px rgba(11, 71, 184, .25); }
.reserva-bloque.confirmada:hover { background: var(--azul-600); }
.reserva-bloque.pendiente {
    background: repeating-linear-gradient(45deg, #fff3d6, #fff3d6 6px, #ffe9b8 6px, #ffe9b8 12px);
    border: 2px dashed #c98a00; color: #6b4400;
}

/* Ventana de detalle */
.etiqueta-estado { display: inline-block; padding: .2rem .75rem; border-radius: 99px; font-weight: 800; font-size: .85rem; }
.etiqueta-estado.confirmada { background: var(--azul-100); color: var(--azul-700); }
.etiqueta-estado.pendiente { background: var(--amarillo-fondo); color: var(--amarillo); }
.detalle-datos { display: grid; grid-template-columns: 1fr 1.4fr; gap: .5rem .8rem; margin: 1rem 0 1.2rem; }
.detalle-datos dt { color: var(--texto-suave); }
.detalle-datos dd { margin: 0; font-weight: 700; }
.detalle-botones { display: grid; gap: .55rem; }
.detalle-botones form { margin: 0; }
.detalle-botones form .btn { width: 100%; }
.aviso-superpuesta { margin: .8rem 0 0; padding: .6rem .8rem; border-radius: 10px; background: var(--amarillo-fondo); color: var(--amarillo); font-weight: 600; }

/* Imprimir: solo la planilla, sin menú ni botones */
@media print {
    .adm-header, .adm-lateral, .planilla-acciones, .planilla-barra, .resumen-hoy, .migas, .aviso { display: none !important; }
    body.admin, .adm-panel { background: #fff !important; padding: 0 !important; }
    .adm-contenido { padding: 0 !important; }
    .planilla-caja { max-height: none; overflow: visible; border: 0; }
    .planilla { min-width: 0; font-size: .7rem; }
    .planilla th, .planilla td, .reserva-bloque, .planilla-muestra { -webkit-print-color-adjust: exact; print-color-adjust: exact; }
    .celda-libre .celda-mas { display: none; }
}

/* Disponibilidad · Todas las casas: el nombre de cada casa lleva a su calendario */
.planilla thead th a { color: inherit; text-decoration: none; }
.planilla thead th a:hover, .planilla thead th a:focus-visible { text-decoration: underline; }
/* ===== Precio y color de cada casillero ===== */
.celda-precio {
    align-self: center; justify-self: center;
    font: 700 .78rem var(--fuente-texto); color: var(--azul-950); white-space: nowrap;
}
.celda-precio.pasado { display: block; padding: .9rem .3rem; text-align: center; opacity: .7; }
.planilla-muestra.precio { width: auto; padding: 0 .35rem; background: #fff; border: 1px solid var(--linea); font-weight: 800; color: var(--azul-950); }
.dc-precio { font: 800 1.5rem var(--fuente-titulos); color: var(--azul-950); margin: .6rem 0 1rem; }
.dc-precio:empty { display: none; }
.lista-casas { display: grid; grid-template-columns: repeat(auto-fill, minmax(170px, 1fr)); gap: .1rem 1rem; }
.lista-casas label:first-child { grid-column: 1 / -1; border-bottom: 1px solid var(--linea); margin-bottom: .2rem; }
.opcion-precio input[type=number] { width: 9rem; margin: 0 0 0 .3rem; }
.colores { display: grid; grid-template-columns: repeat(auto-fill, minmax(190px, 1fr)); gap: .1rem 1rem; }
.colores label { cursor: pointer; }
.muestra-color { width: 26px; height: 18px; border-radius: 5px; border: 1px solid rgba(7, 29, 74, .25); flex: 0 0 auto; }
.colores input[type=color] { width: 42px; height: 26px; padding: 0; border: 1px solid var(--linea); border-radius: 6px; background: #fff; }
</style>

<%--
    Disponibilidad tiene dos vistas:
    - "Todas las casas" (sin alojamiento elegido): la planilla del mes, como el Excel.
    - Una casa: el calendario del mes de ese alojamiento y sus próximas reservas.
--%>

<nav class="migas" aria-label="Ruta"><a href="${base}/inicio">Inicio</a> <span>/</span> Disponibilidad</nav>

<div class="pagina-titulo">
    <div>
        <h1>Disponibilidad</h1>
        <p class="muted">
            <c:choose>
                <c:when test="${empty alojamiento}">Todas las casas, día por día. Tocá un lugar libre para reservar o anotarle precio y color, o una reserva para ver sus datos.</c:when>
                <c:otherwise>Consultá qué fechas están libres y cuáles ocupadas en cada alojamiento.</c:otherwise>
            </c:choose>
        </p>
    </div>
    <c:if test="${empty alojamiento}">
        <div class="planilla-acciones">
            <a class="btn btn-secundario" href="${base}/disponibilidad?mes=${mesActual}">Ir a hoy</a>
            <button type="button" class="btn btn-secundario" data-imprimir>🖨 Imprimir mes</button>
            <button type="button" class="btn btn-secundario" data-abrir-dias>🎨 Precios y colores</button>
            <button type="button" class="btn btn-primario" data-modal-abrir="m-crear"
                    data-f-alojamientoid="" data-f-fechaingreso="" data-f-fechasalida="">+ Nueva reserva</button>
        </div>
    </c:if>
</div>

<section class="tarjeta">
    <form action="${base}/disponibilidad" method="get" class="campo-simple">
        <label for="sel-alojamiento">Ver</label>
        <select id="sel-alojamiento" name="alojamiento" data-autoenviar>
            <option value="">Todas las casas</option>
            <c:forEach var="a" items="${alojamientos}">
                <option value="${a.id}" ${a.id == alojamientoSel ? 'selected' : ''}><c:out value="${a.nombre}"/></option>
            </c:forEach>
        </select>
        <input type="hidden" name="mes" value="${mesValor}">
        <noscript><button type="submit" class="btn btn-secundario">Ver</button></noscript>
    </form>
</section>

<c:choose>
<%-- ===================== UNA CASA: calendario ===================== --%>
<c:when test="${not empty alojamiento}">
    <p><a href="${base}/disponibilidad?mes=${mesValor}">&larr; Ver todas las casas</a></p>
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
        <p class="muted nota">El día de salida queda libre para un nuevo ingreso (salvo que la reserva tenga medio día extra).</p>
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
</c:when>

<%-- ===================== TODAS LAS CASAS: planilla ===================== --%>
<c:otherwise>
<%-- Meses del año, como las hojas del Excel --%>
<div class="planilla-barra">
    <div class="planilla-anio">
        <a href="${base}/disponibilidad?mes=${anioAnterior}" aria-label="Año anterior">‹</a>
        <strong>${anio}</strong>
        <a href="${base}/disponibilidad?mes=${anioSiguiente}" aria-label="Año siguiente">›</a>
    </div>
    <nav class="meses" aria-label="Meses">
        <c:forEach var="m" items="${meses}">
            <a href="${base}/disponibilidad?mes=${m.valor}" class="${m.activo ? 'activo' : ''}"
               ${m.activo ? 'aria-current="page"' : ''}>${m.nombre}</a>
        </c:forEach>
    </nav>
</div>

<h2 class="planilla-mes">Todas las casas · <c:out value="${mesTexto}"/></h2>

<%-- Resumen de hoy (solo en el mes actual) --%>
<c:if test="${not empty hoyLibres}">
    <div class="resumen-hoy" aria-label="Resumen de hoy">
        <div class="libres"><strong>${hoyLibres}</strong> ${hoyLibres == 1 ? 'casa libre' : 'casas libres'} hoy</div>
        <div class="ocupadas"><strong>${hoyOcupadas}</strong> ${hoyOcupadas == 1 ? 'ocupada' : 'ocupadas'}</div>
        <div class="pendientes"><strong>${hoyPorConfirmar}</strong> por confirmar</div>
    </div>
</c:if>

<%-- Leyenda: cada estado se distingue por color Y por texto o ícono --%>
<div class="leyenda-planilla">
    <span><i class="planilla-muestra libre"></i> Libre (tocá para reservar)</span>
    <span><i class="planilla-muestra confirmada">✓</i> Confirmada</span>
    <span><i class="planilla-muestra pendiente">⏳</i> Pendiente de confirmar</span>
    <span><i class="planilla-muestra feriado"></i> Feriado</span>
    <span><i class="planilla-muestra precio">$</i> Precio por noche anotado</span>
</div>

<c:choose>
    <c:when test="${empty casas}">
        <section class="tarjeta"><p class="muted">Todavía no hay alojamientos cargados.</p></section>
    </c:when>
    <c:otherwise>
        <div class="planilla-caja">
            <table class="planilla">
                <caption class="solo-lector">Ocupación de <c:out value="${mesTexto}"/></caption>
                <thead>
                <tr>
                    <th class="dia" scope="col">Día</th>
                    <c:forEach var="casa" items="${casas}">
                        <th scope="col" style="background:${casa.color}">
                            <a href="${base}/disponibilidad?alojamiento=${casa.id}&mes=${mesValor}"
                               title="Ver el calendario de esta casa"><c:out value="${casa.nombre}"/></a>
                        </th>
                    </c:forEach>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="fila" items="${filas}">
                    <tr class="${fila.finDeSemana ? 'finde' : ''} ${not empty fila.feriado ? 'feriado' : ''} ${fila.hoy ? 'hoy' : ''}">
                        <th class="dia" scope="row">
                            ${fila.numero} ${fila.letra}
                            <small>
                                <c:choose>
                                    <c:when test="${fila.hoy}">HOY</c:when>
                                    <c:otherwise><c:out value="${fila.feriado}"/></c:otherwise>
                                </c:choose>
                            </small>
                        </th>

                        <c:forEach var="celda" items="${fila.celdas}">
                            <c:choose>

                                <%-- Libre --%>
                                <c:when test="${celda.tipo == 'libre'}">
                                    <td class="libre" style="background:${celda.fondo}${empty celda.colorPropio ? '' : ' !important'}">
                                        <c:choose>
                                            <c:when test="${celda.pasado}">
                                                <c:if test="${not empty celda.precio}">
                                                    <span class="celda-precio pasado" aria-hidden="true">${celda.precio}</span>
                                                </c:if>
                                                <span class="solo-lector"><c:out value="${celda.descripcion}"/></span>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="button" class="celda-libre"
                                                        data-celda
                                                        data-casa-id="${celda.casa.id}"
                                                        data-casa="<c:out value='${celda.casa.nombre}'/>"
                                                        data-fecha="${celda.fecha}"
                                                        data-fecha-siguiente="${celda.fechaSiguiente}"
                                                        data-fecha-texto="${celda.fechaTexto}"
                                                        data-precio="${celda.precio}"
                                                        data-precio-valor="${celda.precioValor}"
                                                        data-color="${celda.colorPropio}"
                                                        aria-label="<c:out value='${celda.descripcion}'/>${empty celda.precio ? '' : ', '}${celda.precio}">
                                                    <c:if test="${not empty celda.precio}">
                                                        <span class="celda-precio" aria-hidden="true">${celda.precio}</span>
                                                    </c:if>
                                                    <span class="celda-mas" aria-hidden="true">+</span>
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </c:when>

                                <%-- Reserva: un bloque que ocupa todas sus noches --%>
                                <c:otherwise>
                                    <c:set var="r" value="${celda.reserva}"/>
                                    <td class="reserva" rowspan="${celda.filas}" style="background:${celda.fondo}${empty celda.colorPropio ? '' : ' !important'}">
                                        <button type="button"
                                                class="reserva-bloque ${r.confirmada ? 'confirmada' : 'pendiente'}"
                                                aria-label="<c:out value='${celda.descripcion}'/>"
                                                data-ver-reserva
                                                data-id="${r.id}"
                                                data-estado="${r.estado}"
                                                data-casa="<c:out value='${celda.casa.nombre}'/>"
                                                data-casa-id="${celda.casa.id}"
                                                data-cliente="<c:out value='${r.nombreMostrar}'/>"
                                                data-documento="<c:out value='${r.clienteDocumento}'/>"
                                                data-telefono="<c:out value='${r.clienteTelefono}'/>"
                                                data-whatsapp="${r.telefonoWhatsapp}"
                                                data-ingreso="${r.ingresoTexto}"
                                                data-salida="${r.salidaTexto}"
                                                data-medio-dia="${r.medioDiaExtra}"
                                                data-noches="${r.noches}"
                                                data-huespedes="${r.cantidadHuespedes}"
                                                data-superpuesta="${celda.superpuesta}">
                                            <span class="bloque-estado" aria-hidden="true">${r.confirmada ? '✓' : '⏳'}${celda.superpuesta ? ' ⚠' : ''}</span>
                                            <strong><c:out value="${r.nombreMostrar}"/></strong>
                                            <span class="info">${r.cantidadHuespedes} pers.</span>
                                            <span class="info">Sale ${r.salidaCorta}${r.medioDiaExtra ? ' +½ día' : ''}</span>
                                            <c:if test="${not r.confirmada}"><span class="info"><b>Por confirmar</b></span></c:if>
                                        </button>
                                    </td>
                                </c:otherwise>

                            </c:choose>
                        </c:forEach>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>



<%-- ============ Detalle de una reserva (se llena con los datos del bloque tocado) ============ --%>
<dialog id="m-reserva" class="modal modal-chico">
    <div class="modal-form">
        <span id="dr-estado" class="etiqueta-estado"></span>
        <h2 id="dr-cliente" style="margin-top:.6rem"></h2>
        <p class="muted" id="dr-subtitulo"></p>

        <p class="aviso-superpuesta" id="dr-superpuesta" hidden>
            ⚠ Hay otra solicitud pendiente para alguna de estas noches. Revisala en Reservas.
        </p>

        <dl class="detalle-datos">
            <dt>Ingreso</dt><dd id="dr-ingreso"></dd>
            <dt>Salida</dt><dd id="dr-salida"></dd>
            <dt>Noches</dt><dd id="dr-noches"></dd>
            <dt>Huéspedes</dt><dd id="dr-huespedes"></dd>
            <dt>Teléfono</dt><dd id="dr-telefono"></dd>
            <dt>Cédula</dt><dd id="dr-documento"></dd>
        </dl>

        <div class="detalle-botones">
            <a id="dr-whatsapp" class="btn btn-primario" href="#" target="_blank" rel="noopener">💬 Escribir por WhatsApp</a>

            <form id="dr-confirmar" action="${base}/reservas" method="post">
                <input type="hidden" name="_csrf" value="${csrf}">
                <input type="hidden" name="accion" value="confirmar">
                <input type="hidden" name="id" value="">
                <input type="hidden" name="volver" value="/disponibilidad?mes=${mesValor}">
                <button type="submit" class="btn btn-primario">✓ Confirmar reserva</button>
            </form>

            <a id="dr-ver" class="btn btn-secundario" href="#">Ver en Reservas</a>

            <form id="dr-cancelar" action="${base}/reservas" method="post"
                  data-confirmar="¿Seguro que querés cancelar esta reserva? Las fechas quedan libres.">
                <input type="hidden" name="_csrf" value="${csrf}">
                <input type="hidden" name="accion" value="cancelar">
                <input type="hidden" name="id" value="">
                <input type="hidden" name="volver" value="/disponibilidad?mes=${mesValor}">
                <button type="submit" class="btn btn-peligro-borde">Cancelar reserva</button>
            </form>

            <button type="button" class="btn btn-secundario" data-modal-cerrar>Cerrar</button>
        </div>
    </div>
</dialog>


<%-- ============ Casillero libre: reservar o anotar precio y color ============ --%>
<dialog id="m-celda" class="modal modal-chico">
    <div class="modal-form">
        <h2 id="dc-casa"></h2>
        <p class="muted" id="dc-fecha"></p>
        <p class="dc-precio" id="dc-precio"></p>
        <div class="detalle-botones">
            <button type="button" class="btn btn-primario" id="dc-reservar">+ Reservar desde este día</button>
            <button type="button" class="btn btn-secundario" id="dc-editar">🎨 Precio y color de este día</button>
            <button type="button" class="btn btn-secundario" data-modal-cerrar>Cerrar</button>
        </div>
    </div>
</dialog>


<%-- ============ Precios y colores (uno o varios días, una o varias casas) ============ --%>
<dialog id="m-dias" class="modal">
    <form action="${base}/disponibilidad" method="post" class="modal-form">
        <h2>Precios y colores</h2>
        <p class="muted">Anotá el precio por noche y marcá con un color los feriados o las temporadas.
            Podés hacerlo para varios días y varias casas de una vez.</p>
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="accion" value="dias">
        <input type="hidden" name="mes" value="${mesValor}">

        <div class="campos">
            <label class="campo">Desde
                <input type="date" name="desde" required></label>
            <label class="campo">Hasta (incluido)
                <input type="date" name="hasta" required></label>

            <fieldset class="campo opciones">
                <legend>Casas</legend>
                <div class="lista-casas">
                    <label><input type="checkbox" id="dd-todas"> <strong>Todas las casas</strong></label>
                    <c:forEach var="casa" items="${casas}">
                        <label><input type="checkbox" name="casa" value="${casa.id}"> <c:out value="${casa.nombre}"/></label>
                    </c:forEach>
                </div>
            </fieldset>

            <fieldset class="campo opciones">
                <legend>Precio por noche</legend>
                <label><input type="radio" name="precioOpcion" value="igual" checked> No cambiar el precio</label>
                <label class="opcion-precio"><input type="radio" name="precioOpcion" value="nuevo"> Poner este precio: $
                    <input type="number" name="precio" min="0" step="any" inputmode="decimal" aria-label="Precio por noche en pesos"></label>
                <label><input type="radio" name="precioOpcion" value="quitar"> Quitar el precio</label>
            </fieldset>

            <fieldset class="campo opciones">
                <legend>Color</legend>
                <label><input type="radio" name="color" value="igual" checked> No cambiar el color</label>
                <label><input type="radio" name="color" value="ninguno"> Sin color (vuelve al color de la casa)</label>
                <div class="colores">
                    <label><input type="radio" name="color" value="#FFCC99"><span class="muestra-color" style="background:#FFCC99"></span> Feriado</label>
                    <label><input type="radio" name="color" value="#F4B6B6"><span class="muestra-color" style="background:#F4B6B6"></span> Temporada alta</label>
                    <label><input type="radio" name="color" value="#FFE699"><span class="muestra-color" style="background:#FFE699"></span> Temporada media</label>
                    <label><input type="radio" name="color" value="#C6EFCE"><span class="muestra-color" style="background:#C6EFCE"></span> Temporada baja</label>
                    <label><input type="radio" name="color" value="#BDD7EE"><span class="muestra-color" style="background:#BDD7EE"></span> Celeste</label>
                    <label><input type="radio" name="color" value="#E4D1F5"><span class="muestra-color" style="background:#E4D1F5"></span> Lila</label>
                    <label><input type="radio" name="color" value="otro"> Otro:
                        <input type="color" name="colorOtro" value="#FFCC99" aria-label="Elegir otro color"></label>
                </div>
            </fieldset>
        </div>

        <div class="modal-pie">
            <button type="button" class="btn btn-secundario" data-modal-cerrar>Cancelar</button>
            <button type="submit" class="btn btn-primario">Guardar</button>
        </div>
    </form>
</dialog>


<%-- ============ Nueva reserva (el mismo formulario de la sección Reservas) ============ --%>
<dialog id="m-crear" class="modal">
    <form action="${base}/reservas" method="post" class="modal-form">
        <h2>Nueva reserva</h2>
        <p class="muted">Para reservas acordadas por teléfono, WhatsApp u otro medio. Si queda confirmada, bloquea las fechas.</p>
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="accion" value="crear">
        <input type="hidden" name="volver" value="/disponibilidad?mes=${mesValor}">

        <div class="campos">
            <label class="campo ancho">Cliente
                <select name="clienteId" required>
                    <option value="">Elegí un cliente…</option>
                    <c:forEach var="cli" items="${clientes}">
                        <option value="${cli.id}"><c:out value="${cli.nombre}"/> — <c:out value="${cli.documento}"/></option>
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


<script>
    (function () {
        'use strict';
        var base = '${base}';
        var ventana = document.getElementById('m-reserva');

        function poner(id, texto) {
            document.getElementById(id).textContent = texto; // textContent: nunca interpreta HTML
        }

        // Al tocar una reserva: llenar la ventana de detalle con sus datos y abrirla
        document.querySelectorAll('[data-ver-reserva]').forEach(function (bloque) {
            bloque.addEventListener('click', function () {
                var d = bloque.dataset;
                var confirmada = d.estado === 'CONFIRMADA';

                var etiqueta = document.getElementById('dr-estado');
                etiqueta.textContent = confirmada ? '✓ Confirmada' : '⏳ Pendiente de confirmar';
                etiqueta.className = 'etiqueta-estado ' + (confirmada ? 'confirmada' : 'pendiente');

                poner('dr-cliente', d.cliente);
                poner('dr-subtitulo', 'Reserva N.º ' + d.id + ' · ' + d.casa);
                poner('dr-ingreso', d.ingreso + ' · 12:00');
                poner('dr-salida', d.salida + (d.medioDia === 'true' ? ' · medio día extra' : ' · 10:00'));
                poner('dr-noches', d.noches);
                poner('dr-huespedes', d.huespedes + (d.huespedes === '1' ? ' persona' : ' personas'));
                poner('dr-telefono', d.telefono || '—');
                poner('dr-documento', d.documento || '—');
                document.getElementById('dr-superpuesta').hidden = d.superpuesta !== 'true';

                var whatsapp = document.getElementById('dr-whatsapp');
                whatsapp.hidden = !d.whatsapp;
                whatsapp.href = 'https://wa.me/' + d.whatsapp;

                document.getElementById('dr-ver').href = base + '/reservas?alojamiento=' + d.casaId;

                var confirmar = document.getElementById('dr-confirmar');
                confirmar.hidden = confirmada;
                confirmar.querySelector('[name=id]').value = d.id;
                document.getElementById('dr-cancelar').querySelector('[name=id]').value = d.id;

                ventana.showModal();
            });
        });

        // ---- Casillero libre: elegir entre reservar o anotar precio y color ----
        var celdaVentana = document.getElementById('m-celda');
        var diasVentana = document.getElementById('m-dias');
        var crearVentana = document.getElementById('m-crear');
        var celdaActual = null;

        document.querySelectorAll('[data-celda]').forEach(function (celda) {
            celda.addEventListener('click', function () {
                celdaActual = celda.dataset;
                poner('dc-casa', celdaActual.casa);
                poner('dc-fecha', 'Libre · ' + celdaActual.fechaTexto);
                poner('dc-precio', celdaActual.precio ? celdaActual.precio + ' por noche' : '');
                celdaVentana.showModal();
            });
        });

        document.getElementById('dc-reservar').addEventListener('click', function () {
            celdaVentana.close();
            crearVentana.querySelector('[name=alojamientoId]').value = celdaActual.casaId;
            crearVentana.querySelector('[name=fechaIngreso]').value = celdaActual.fecha;
            crearVentana.querySelector('[name=fechaSalida]').value = celdaActual.fechaSiguiente;
            crearVentana.showModal();
            crearVentana.querySelector('[name=clienteId]').focus();
        });

        document.getElementById('dc-editar').addEventListener('click', function () {
            celdaVentana.close();
            abrirDias(celdaActual.fecha, celdaActual.fecha, [celdaActual.casaId], celdaActual.precioValor);
        });

        // ---- Ventana "Precios y colores" ----
        var casillas = diasVentana.querySelectorAll('[name=casa]');
        var todas = document.getElementById('dd-todas');

        function abrirDias(desde, hasta, casas, precio) {
            var formulario = diasVentana.querySelector('form');
            formulario.reset();
            formulario.desde.value = desde || '';
            formulario.hasta.value = hasta || '';
            casillas.forEach(function (c) { c.checked = casas.indexOf(c.value) !== -1; });
            todas.checked = casas.length > 0 && casas.length === casillas.length;
            formulario.precio.value = precio || '';
            diasVentana.showModal();
            formulario.desde.focus();
        }

        document.querySelectorAll('[data-abrir-dias]').forEach(function (boton) {
            boton.addEventListener('click', function () { abrirDias('', '', [], ''); });
        });

        todas.addEventListener('change', function () {
            casillas.forEach(function (c) { c.checked = todas.checked; });
        });
        casillas.forEach(function (c) {
            c.addEventListener('change', function () {
                todas.checked = Array.prototype.every.call(casillas, function (x) { return x.checked; });
            });
        });

        // Escribir un precio o tocar "Otro color" ya marca esa opción
        diasVentana.querySelector('[name=precio]').addEventListener('input', function () {
            diasVentana.querySelector('[name=precioOpcion][value=nuevo]').checked = true;
        });
        diasVentana.querySelector('[name=colorOtro]').addEventListener('input', function () {
            diasVentana.querySelector('[name=color][value=otro]').checked = true;
        });

        // Antes de guardar: al menos una casa
        diasVentana.querySelector('form').addEventListener('submit', function (evento) {
            var alguna = Array.prototype.some.call(casillas, function (c) { return c.checked; });
            if (!alguna) {
                evento.preventDefault();
                window.alert('Elegí al menos una casa.');
            }
        });

        // Imprimir el mes
        document.querySelectorAll('[data-imprimir]').forEach(function (boton) {
            boton.addEventListener('click', function () { window.print(); });
        });

        // Al abrir la página del mes actual, bajar hasta "hoy"
        var hoy = document.querySelector('.planilla tr.hoy');
        if (hoy && document.querySelector('.planilla-caja')) {
            var caja = document.querySelector('.planilla-caja');
            caja.scrollTop = Math.max(0, hoy.offsetTop - 140);
        }
    })();
</script>

</c:otherwise>
</c:choose>

<%@ include file="layout-bottom.jspf" %>
