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
.resumen-hoy { display: flex; flex-wrap: wrap; gap: .5rem; margin-bottom: .8rem; }
.resumen-hoy div {
    display: flex; align-items: center; gap: .4rem; padding: .3rem .65rem;
    background: #fff; border: 1px solid var(--linea); border-radius: 9px; font-size: .8rem;
}
.resumen-hoy strong { font: 800 1.1rem var(--fuente-titulos); }
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
.planilla-caja { overflow-x: auto; border: 1px solid var(--linea); border-radius: 14px; background: #fff; }
.planilla-controles { display: flex; justify-content: flex-end; align-items: center; gap: .6rem; margin-bottom: .55rem; }
.planilla-zoom { display: inline-flex; border: 1px solid var(--linea); border-radius: 9px; overflow: hidden; background: #fff; }
.planilla-zoom button { height: 38px; min-width: 38px; padding: 0 .6rem; border: 0; background: #fff; color: var(--azul-700); font: 700 1rem var(--fuente-texto); cursor: pointer; }
.planilla-zoom button + button { border-left: 1px solid var(--linea); }
.planilla-zoom button:hover { background: var(--azul-50); }

.boton-ampliar-planilla {
    width: 38px;
    height: 38px;

    display: inline-flex;
    align-items: center;
    justify-content: center;

    padding: 0;

    border: 1px solid var(--linea);
    border-radius: 9px;

    background: #fff;
    color: var(--azul-700);

    cursor: pointer;

    transition:
            background .15s ease,
            border-color .15s ease,
            transform .15s ease;
}

.boton-ampliar-planilla:hover {
    background: var(--azul-50);
    border-color: var(--azul-600);
}

.boton-ampliar-planilla:active {
    transform: scale(.96);
}

.boton-ampliar-planilla svg {
    width: 20px;
    height: 20px;

    fill: none;
    stroke: currentColor;
    stroke-width: 2;

    stroke-linecap: round;
    stroke-linejoin: round;
}
.planilla-ampliada {
    position: fixed;

    inset: 0;

    z-index: 9999;

    padding: 20px;

    background: #f4f7fb;

    display: flex;
    flex-direction: column;
}

.planilla-ampliada .planilla-controles {
    margin-bottom: .7rem;
}

.planilla-ampliada .planilla-caja {
    flex: 1;

    width: 100%;
    max-height: none;

    overflow: auto;

    border-radius: 14px;
}

body.planilla-abierta {
    overflow: hidden;
}
.planilla { border-collapse: separate; border-spacing: 0; width: 100%; min-width: calc(100% * var(--zoom-planilla, 1)); table-layout: fixed; }
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
    border: 0; border-radius: 9px; padding: .4rem .45rem;
    text-align: left; font: 400 .85rem/1.25 var(--fuente-texto); overflow: hidden; cursor: pointer;
}
.reserva-bloque:focus-visible { outline: 3px solid var(--naranja); outline-offset: 1px; }
.reserva-bloque strong { display: block; padding-right: 1.1rem; font: 700 .86rem/1.2 var(--fuente-texto); }
.reserva-bloque .info { display: block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-size: .72rem; line-height: 1.3; }
.reserva-bloque .info.precio { font-weight: 800; }
.reserva-bloque > * { flex-shrink: 0; }   /* los renglones no se aprietan: si no entran, se ven al pasar el mouse */
/* En reservas cortas no entra todo: al pasar el mouse o con el teclado, el cartel se agranda y muestra todos los datos */
.reserva-bloque:hover, .reserva-bloque:focus-visible { height: auto; min-height: calc(100% - 8px); z-index: 6; overflow: visible; }
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

    </div>
    <c:if test="${empty alojamiento}">
        <div class="planilla-acciones">
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
              <div class="planilla-controles">
                  <button type="button" class="btn btn-secundario" id="botonIrHoy"
                          data-mes-actual="${base}/disponibilidad?mes=${mesActual}">Ir a hoy</button>
                  <button type="button" class="btn btn-secundario" data-abrir-dias>🎨 Precios y colores</button>
                  <div class="planilla-zoom" role="group" aria-label="Zoom de la planilla">
                      <button type="button" id="zoomMenos" aria-label="Alejar" title="Alejar">−</button>
                      <button type="button" id="zoomRestablecer" title="Tamaño normal">100%</button>
                      <button type="button" id="zoomMas" aria-label="Acercar" title="Acercar">+</button>
                  </div>
            <button type="button"
                    class="boton-ampliar-planilla"
                    id="botonAmpliarPlanilla"
                    aria-label="Ampliar calendario"
                    title="Ampliar calendario">

                <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M8 3H3v5"/>
                    <path d="M3 3l6 6"/>
                    <path d="M16 3h5v5"/>
                    <path d="M21 3l-6 6"/>
                    <path d="M8 21H3v-5"/>
                    <path d="M3 21l6-6"/>
                    <path d="M16 21h5v-5"/>
                    <path d="M21 21l-6-6"/>
                </svg>

            </button>
        </div>

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
                                                data-cliente-id="${r.clienteId}"
                                                data-ingreso-iso="${r.ingreso}"
                                                data-salida-iso="${r.salida}"
                                                data-cliente="<c:out value='${r.nombreMostrar}'/>"
                                                data-documento="<c:out value='${r.clienteDocumento}'/>"
                                                data-telefono="<c:out value='${r.clienteTelefono}'/>"
                                                data-whatsapp="${r.telefonoWhatsapp}"
                                                data-ingreso="${r.ingresoTexto}"
                                                data-salida="${r.salidaTexto}"
                                                data-medio-dia="${r.medioDiaExtra}"
                                                data-noches="${r.noches}"
                                                data-huespedes="${r.cantidadHuespedes}"
                                                data-colchon-extra="${r.colchonExtra}"
                                                data-precio="${r.cuenta} = ${r.precioTexto}"
                                                data-procedencia="<c:out value='${r.clienteProcedencia}'/>"
                                                data-nota="<c:out value='${celda.nota}'/>"
                                                data-superpuesta="${celda.superpuesta}">
                                            <span class="bloque-estado" aria-hidden="true">${r.confirmada ? '✓' : '⏳'}${celda.superpuesta ? ' ⚠' : ''}</span>
                                            <strong><c:out value="${r.nombreMostrar}"/></strong>
                                            <span class="info">Tel. <c:out value="${empty r.clienteTelefono ? '—' : r.clienteTelefono}"/></span>
                                            <span class="info precio" title="${r.nochesTexto} × precio por noche">${r.cuenta}</span>
                                            <span class="info">${r.cantidadHuespedes} ${r.cantidadHuespedes == 1 ? 'persona' : 'personas'}</span>
                                            <c:if test="${not empty celda.nota}">
                                                <span class="info obs">📝 <c:out value="${celda.nota}"/></span>
                                            </c:if>
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
            <dt>Colchón extra</dt><dd id="dr-colchon-extra"></dd>
            <dt>Cédula</dt><dd id="dr-documento"></dd>
            <dt>Procedencia</dt><dd id="dr-procedencia"></dd>
            <dt>Precio</dt><dd id="dr-precio"></dd>
        </dl>

        <%-- Observación de la reserva (ej. "llega 22 hs", "trae mascota"): se guarda en la planilla, en su casa y día de ingreso --%>
        <form id="dr-nota" action="${base}/disponibilidad" method="post" class="observacion-form">
            <input type="hidden" name="_csrf" value="${csrf}">
            <input type="hidden" name="accion" value="nota">
            <input type="hidden" name="mes" value="${mesValor}">
            <input type="hidden" name="alojamientoId" value="">
            <input type="hidden" name="fecha" value="">
            <label for="dr-nota-texto">📝 Observación</label>
            <textarea id="dr-nota-texto" name="nota" rows="2" maxlength="500"
                      placeholder="Ej. llega a las 22 hs, trae mascota…"></textarea>
            <button type="submit" class="btn btn-editar">Guardar observación</button>
        </form>

        <div class="detalle-botones">
            <a id="dr-whatsapp" class="btn btn-primario" href="#" target="_blank" rel="noopener">💬 Escribir por WhatsApp</a>

            <form id="dr-confirmar" action="${base}/reservas" method="post">
                <input type="hidden" name="_csrf" value="${csrf}">
                <input type="hidden" name="accion" value="confirmar">
                <input type="hidden" name="id" value="">
                <input type="hidden" name="volver" value="/disponibilidad?mes=${mesValor}">
                <button type="submit" class="btn btn-primario">✓ Confirmar reserva</button>
            </form>

            <button type="button" id="dr-editar" class="btn btn-secundario">✏ Editar reserva</button>

            <a id="dr-ver" class="btn btn-secundario" href="#">Ver en Reservas</a>

            <button type="button"
                    id="dr-mostrar-cancelar"
                    class="btn btn-peligro-borde">
                Cancelar reserva
            </button>

            <form id="dr-cancelar"
                  action="${base}/reservas"
                  method="post"
                  hidden>

                <input type="hidden" name="_csrf" value="${csrf}">
                <input type="hidden" name="accion" value="cancelar">
                <input type="hidden" name="id" value="">
                <input type="hidden" name="volver" value="/disponibilidad?mes=${mesValor}">

                <fieldset class="opciones sena-cancelar" id="dr-sena" hidden>
                    <legend>
                        La seña queda a favor del cliente por 6 meses
                    </legend>
                    <input type="hidden" name="guardarSena" value="1">
                    <label class="importe-sena">
                        Importe de la seña: $
                        <input type="number"
                               name="importeSena"
                               min="1"
                               step="any"
                               inputmode="decimal">
                    </label>
                </fieldset>

                <div class="detalle-cancelacion-acciones">
                    <button type="button"
                            class="btn btn-secundario"
                            id="dr-volver-cancelar">
                        Volver
                    </button>
                    <button type="submit"
                            class="btn btn-peligro-borde">
                        Confirmar cancelación
                    </button>
                </div>
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


<%-- ============ Editar reserva (el estado no cambia: para eso están Confirmar y Cancelar) ============ --%>
<dialog id="m-editar" class="modal">
    <form action="${base}/reservas" method="post" class="modal-form">
        <h2>Editar reserva</h2>
        <p class="muted">Cambiá el cliente, el alojamiento, las fechas o los huéspedes. Se controla que las fechas no choquen con otra reserva.</p>
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="accion" value="editar">
        <input type="hidden" name="id" value="">
        <input type="hidden" name="volver" value="/disponibilidad?mes=${mesValor}">

        <div class="campos">
            <label class="campo ancho">Cliente
                <select name="clienteId" required>
                    <option value="">Elegí un cliente…</option>
                    <c:forEach var="cli" items="${clientes}">
                        <option value="${cli.id}"><c:out value="${cli.nombre}"/><c:if test="${not empty cli.documento}"> — <c:out value="${cli.documento}"/></c:if></option>
                    </c:forEach>
                </select>
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
                <input type="date" name="fechaIngreso" required></label>
            <label class="campo">Salida
                <input type="date" name="fechaSalida" required></label>
            <label class="campo">Huéspedes
                <input type="number" name="huespedes" min="1" max="100" required></label>
            <label class="campo">Medio día extra
                <select name="medioDiaExtra">
                    <option value="0">No</option>
                    <option value="1">Sí (el día de salida queda ocupado)</option>
                </select>
            </label>
        </div>

        <div class="modal-pie">
            <button type="button" class="btn btn-secundario" data-modal-cerrar>Cancelar</button>
            <button type="submit" class="btn btn-primario">Guardar cambios</button>
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
                    <option value="nuevo">+ Agregar cliente nuevo…</option>
                    <c:forEach var="cli" items="${clientes}">
                        <c:set var="senaCli" value="${senasPorCliente[cli.id]}"/>
                        <option value="${cli.id}" data-sena-id="${senaCli.id}" data-sena="${senaCli.resumen}"><c:out value="${cli.nombre}"/><c:if test="${not empty cli.documento}"> — <c:out value="${cli.documento}"/></c:if></option>
                    </c:forEach>
                </select>
                <button type="button" class="btn btn-editar boton-cliente-nuevo" data-elegir-cliente-nuevo>+ Agregar cliente nuevo</button>
            </label>
            <%-- Aviso cuando el cliente elegido tiene una seña a favor (de una reserva confirmada que canceló) --%>
            <div class="campo ancho aviso-sena" data-aviso-sena hidden>
                <span>💰 Este cliente tiene <strong data-texto-sena></strong>.</span>
                <label><input type="checkbox" name="usarSena" value="" data-usar-sena> Usar esta seña en la nueva reserva (queda marcada como usada)</label>
            </div>
            <%-- Datos del cliente nuevo: aparecen al elegir "+ Agregar cliente nuevo…" --%>
            <fieldset class="campo cliente-nuevo" data-cliente-nuevo hidden>
                <legend>Cliente nuevo</legend>
                <div class="campos">
                    <label class="campo ancho">Nombre completo
                        <input type="text" name="nuevoNombre" maxlength="100" data-requerido></label>
                    <label class="campo">Documento (cédula)
                        <input type="text" name="nuevoDocumento" maxlength="20" placeholder="1.234.567-2"></label>
                    <label class="campo">Teléfono (celular)
                        <input type="tel" name="nuevoTelefono" maxlength="30" placeholder="099 123 456"></label>
                    <label class="campo">Correo electrónico
                        <input type="email" name="nuevoEmail" maxlength="100"></label>
                    <label class="campo">Procedencia
                        <small>Departamento, provincia o país</small>
                        <select name="nuevoProcedencia">
                            <option value="">Elegí una opción…</option>
                            <c:forEach var="p" items="${procedencias}">
                                <option value="<c:out value='${p}'/>"><c:out value="${p}"/></option>
                            </c:forEach>
                        </select></label>
                    <label class="campo">Localidad
                        <small>Ciudad o pueblo donde vive</small>
                        <input type="text" name="nuevoLocalidad" list="lista-localidades" maxlength="100" placeholder="Ciudad o pueblo"></label>
                    <datalist id="lista-localidades">
                        <c:forEach var="sugerida" items="${localidades}"><option value="<c:out value='${sugerida}'/>"></option></c:forEach>
                    </datalist>
                </div>
                <small>Solo el nombre es obligatorio. Se registra junto con la reserva, con la contraseña por defecto (la cambia en su primer ingreso).</small>
            </fieldset>
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

        var botonMostrarCancelar = document.getElementById('dr-mostrar-cancelar');

        var formularioCancelar = document.getElementById('dr-cancelar');

        var botonVolverCancelar = document.getElementById('dr-volver-cancelar');

        botonMostrarCancelar.addEventListener('click', function () {
            botonMostrarCancelar.hidden = true;
            formularioCancelar.hidden = false;
        });

        botonVolverCancelar.addEventListener('click', function () {
            formularioCancelar.hidden = true;
            botonMostrarCancelar.hidden = false;
        });

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
                poner('dr-colchon-extra',d.colchonExtra === 'true' ? 'Sí' : 'No');
                poner('dr-documento', d.documento || '—');
                poner('dr-procedencia', d.procedencia || '—');
                var formNota = document.getElementById('dr-nota');
                formNota.querySelector('[name=alojamientoId]').value = d.casaId;
                formNota.querySelector('[name=fecha]').value = d.ingresoIso;
                formNota.querySelector('[name=nota]').value = d.nota || '';
                poner('dr-precio', d.precio);
                document.getElementById('dr-superpuesta').hidden = d.superpuesta !== 'true';

                var whatsapp = document.getElementById('dr-whatsapp');
                whatsapp.hidden = !d.whatsapp;
                whatsapp.href = 'https://wa.me/' + d.whatsapp;

                document.getElementById('dr-ver').href = base + '/reservas?alojamiento=' + d.casaId;

                var confirmar = document.getElementById('dr-confirmar');
                confirmar.hidden = confirmada;
                confirmar.querySelector('[name=id]').value = d.id;
                var cancelar = document.getElementById('dr-cancelar');
                cancelar.querySelector('[name=id]').value = d.id;
                // Si está confirmada, preguntar por la seña; si está pendiente, se cancela directo
                var sena = document.getElementById('dr-sena');
                sena.hidden = !confirmada;
                sena.disabled = !confirmada;   // un fieldset deshabilitado no envía sus campos
                var importeSena = cancelar.querySelector('[name=importeSena]');
                importeSena.value = '';
                importeSena.required = confirmada;

                formularioCancelar.hidden = true;
                botonMostrarCancelar.hidden = false;
                reservaActual = d;
                ventana.showModal();
            });
        });

        // Editar la reserva abierta: llenar el formulario con sus datos
        var reservaActual = null;
        document.getElementById('dr-editar').addEventListener('click', function () {
            var d = reservaActual;
            var editar = document.getElementById('m-editar');
            var f = editar.querySelector('form');
            ventana.close();
            f.querySelector('[name=id]').value = d.id; // (f.id sería el id del formulario)
            f.clienteId.value = d.clienteId || '';
            f.alojamientoId.value = d.casaId;
            f.fechaIngreso.value = d.ingresoIso;
            f.fechaSalida.value = d.salidaIso;
            f.huespedes.value = d.huespedes;
            f.medioDiaExtra.value = d.medioDia === 'true' ? '1' : '0';
            editar.showModal();
            f.clienteId.focus();
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

        // Zoom de la planilla
        var tablaZoom = document.querySelector('.planilla');
        var textoZoom = document.getElementById('zoomRestablecer');
        if (tablaZoom && textoZoom) {
            var aplicarZoom = function (valor) {
                valor = Math.min(2, Math.max(1, Math.round(valor * 10) / 10));   // de 100% a 200%
                tablaZoom.style.width = (valor * 100) + '%';
                textoZoom.textContent = Math.round(valor * 100) + '%';
                try { localStorage.setItem('planillaZoom', valor); } catch (e) {}
                return valor;
            };
            var zoomActual = 1;
            try { zoomActual = parseFloat(localStorage.getItem('planillaZoom')) || 1; } catch (e) {}
            zoomActual = aplicarZoom(zoomActual);

            document.getElementById('zoomMenos').addEventListener('click', function () { zoomActual = aplicarZoom(zoomActual - 0.1); });
            document.getElementById('zoomMas').addEventListener('click', function () { zoomActual = aplicarZoom(zoomActual + 0.1); });
            textoZoom.addEventListener('click', function () { zoomActual = aplicarZoom(1); });
        }
        // "Ir a hoy": baja hasta la fila de hoy; si el mes mostrado no es el actual, abre el mes actual
        var botonIrHoy = document.getElementById('botonIrHoy');
        if (botonIrHoy) {
            botonIrHoy.addEventListener('click', function () {
                var filaHoy = document.querySelector('.planilla tr.hoy');
                if (filaHoy) {
                    filaHoy.scrollIntoView({ behavior: 'smooth', block: 'center' });
                } else {
                    window.location.href = botonIrHoy.getAttribute('data-mes-actual');
                }
            });
        }
        // Ampliar / restaurar la planilla de disponibilidad
        var botonAmpliar = document.getElementById('botonAmpliarPlanilla');

        var cajaPlanilla = document.querySelector('.planilla-caja');

        var controlesPlanilla = document.querySelector('.planilla-controles');

        if (botonAmpliar && cajaPlanilla && controlesPlanilla) {

            var contenedorNormal = cajaPlanilla.parentElement;

            var siguienteControles = controlesPlanilla.nextSibling;

            var siguienteCaja = cajaPlanilla.nextSibling;

            var vistaAmpliada = false;

            botonAmpliar.addEventListener('click', function () {

                if (!vistaAmpliada) {
                    var capa = document.createElement('div');

                    capa.className = 'planilla-ampliada';

                    capa.id = 'planillaAmpliada';

                    document.body.appendChild(capa);

                    capa.appendChild(controlesPlanilla);
                    capa.appendChild(cajaPlanilla);

                    document.body.classList.add('planilla-abierta');

                    var tablaPlanilla = cajaPlanilla.querySelector('.planilla');

                    cajaPlanilla.style.overflowX = 'hidden';
                    cajaPlanilla.style.width = '100%';
                    cajaPlanilla.style.maxWidth = '100%';
                    cajaPlanilla.style.minWidth = '0';

                    if (tablaPlanilla) {
                        tablaPlanilla.style.setProperty('width', '100%', 'important');
                        tablaPlanilla.style.setProperty('min-width', '0', 'important');
                        tablaPlanilla.style.setProperty('max-width', '100%', 'important');
                        tablaPlanilla.style.setProperty('table-layout', 'fixed', 'important');
                    }

                    botonAmpliar.setAttribute('title','Restaurar calendario');

                    botonAmpliar.setAttribute('aria-label','Restaurar calendario');

                    botonAmpliar.innerHTML = `
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M9 9H4V4"/>
                            <path d="M4 9l5-5"/>
                            <path d="M15 9h5V4"/>
                            <path d="M20 9l-5-5"/>
                            <path d="M9 15H4v5"/>
                            <path d="M4 15l5 5"/>
                            <path d="M15 15h5v5"/>
                            <path d="M20 15l-5 5"/>
                        </svg>
                    `;

                    vistaAmpliada = true;

                } else {
                    var capa = document.getElementById('planillaAmpliada');

                    contenedorNormal.insertBefore(controlesPlanilla, siguienteControles);

                    contenedorNormal.insertBefore(cajaPlanilla, siguienteCaja);

                    if (capa) {
                        capa.remove();
                    }

                    document.body.classList.remove('planilla-abierta');

                    cajaPlanilla.style.overflowX = '';
                    cajaPlanilla.style.width = '';
                    cajaPlanilla.style.maxWidth = '';
                    cajaPlanilla.style.minWidth = '';

                    if (tablaPlanilla) {

                        tablaPlanilla.style.removeProperty('min-width');
                        tablaPlanilla.style.removeProperty('max-width');
                        tablaPlanilla.style.removeProperty('table-layout');
                    }

                    botonAmpliar.setAttribute('title', 'Ampliar calendario');

                    botonAmpliar.setAttribute( 'aria-label', 'Ampliar calendario');

                    botonAmpliar.innerHTML = `
                        <svg viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M8 3H3v5"/>
                            <path d="M3 3l6 6"/>
                            <path d="M16 3h5v5"/>
                            <path d="M21 3l-6 6"/>
                            <path d="M8 21H3v-5"/>
                            <path d="M3 21l6-6"/>
                            <path d="M16 21h5v-5"/>
                            <path d="M21 21l-6-6"/>
                        </svg>
                    `;
                    vistaAmpliada = false;
                }
            });
        }

    })();
</script>

</c:otherwise>
</c:choose>

<style>
    /* Campos del cliente nuevo dentro de "Nueva reserva" */
    .cliente-nuevo { grid-column: 1 / -1; border: 1px solid var(--linea); border-radius: 10px; padding: .6rem .9rem .8rem; margin: 0; }
    .cliente-nuevo legend { padding: 0 .4rem; }
    .cliente-nuevo .campos { margin-top: .4rem; }
    .cliente-nuevo > small { display: block; margin-top: .6rem; color: var(--texto-suave); font-weight: 400; }
    .cliente-nuevo[hidden] { display: none; }
    .aviso-sena { display: grid; gap: .4rem; padding: .7rem .9rem; border-radius: 10px;
                  background: var(--verde-fondo); color: var(--verde); font-weight: 600; }
    .aviso-sena[hidden] { display: none; }
    .aviso-sena label { display: flex; align-items: center; gap: .5rem; font-weight: 700; color: var(--texto); }
    .aviso-sena input { width: auto; margin: 0; }
    .sena-cancelar { margin: 0 0 .6rem; text-align: left; }
    .sena-cancelar[hidden] { display: none; }
    .importe-sena input { width: 9rem; margin: 0 0 0 .3rem; }
    /* Observación de la reserva */
    .reserva-bloque .info.obs { font-style: italic; white-space: normal; }   /* puede ocupar varios renglones */
    .observacion-form { display: grid; gap: .4rem; margin: 0 0 1.1rem; }
    .observacion-form label { font-weight: 700; }
    .observacion-form textarea { width: 100%; font: inherit; resize: vertical; }
    .observacion-form .btn { justify-self: start; padding: .45rem .9rem; }
    .boton-cliente-nuevo { margin-top: .5rem; padding: .4rem .8rem; font-size: .88rem; }
</style>

<script>
    // "Nueva reserva": al elegir "+ Agregar cliente nuevo…" se muestran los campos para registrarlo
    (function () {
        'use strict';
        var ventana = document.getElementById('m-crear');
        if (!ventana) return;
        var selector = ventana.querySelector('select[name=clienteId]');
        var bloque = ventana.querySelector('[data-cliente-nuevo]');

        function actualizar() {
            var nuevo = selector.value === 'nuevo';
            bloque.hidden = !nuevo;
            bloque.querySelectorAll('[data-requerido]').forEach(function (campo) {
                campo.required = nuevo;
            });
        }

        // Seña a favor del cliente elegido
        var avisoSena = ventana.querySelector('[data-aviso-sena]');
        var usarSena = ventana.querySelector('[data-usar-sena]');
        function actualizarSena() {
            var opcion = selector.options[selector.selectedIndex];
            var texto = opcion && opcion.dataset.sena;
            avisoSena.hidden = !texto;
            usarSena.checked = false;
            usarSena.value = texto ? opcion.dataset.senaId : '';
            avisoSena.querySelector('[data-texto-sena]').textContent = texto || '';
        }
        selector.addEventListener('change', actualizarSena);
        ventana.addEventListener('close', function () { usarSena.checked = false; });
        actualizarSena();

        // Botón "+ Agregar cliente nuevo": elige esa opción del desplegable y muestra los campos
        ventana.querySelector('[data-elegir-cliente-nuevo]').addEventListener('click', function () {
            selector.value = 'nuevo';
            actualizar();
            actualizarSena();
            bloque.querySelector('[name=nuevoNombre]').focus();
        });

        selector.addEventListener('change', function () {
            actualizar();
            if (selector.value === 'nuevo') {
                bloque.querySelector('[name=nuevoNombre]').focus();
            }
        });
        actualizar();
    })();
</script>

<%@ include file="layout-bottom.jspf" %>
