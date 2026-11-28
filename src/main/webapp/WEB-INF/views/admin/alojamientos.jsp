<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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
                        <th>Latitud</th><th>Longitud</th><th>Piscina</th><th class="acciones-col">Acciones</th>
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
                            <td>${empty a.latitud ? '—' : a.latitud}</td>
                            <td>${empty a.longitud ? '—' : a.longitud}</td>
                            <td>${a.distanciaApiscina} m</td>
                            <td class="acciones">
                                <button type="button" class="btn btn-secundario" data-modal-abrir="m-fotos-${a.id}">Fotos (${fn:length(a.listaImagenes)})</button>
                                <button type="button" class="btn btn-editar" data-modal-abrir="m-editar"
                                        data-f-id="${a.id}"
                                        data-f-nombre="<c:out value='${a.nombre}'/>"
                                        data-f-descripcion="<c:out value='${a.descripcion}'/>"
                                        data-f-capacidad="${a.capacidad}"
                                        data-f-precio="${a.precio}"
                                        data-f-preciohasta2="${a.precioHasta2}"
                                        data-f-preciohasta4="${a.precioHasta4}"
                                        data-f-preciohasta6="${a.precioHasta6}"
                                        data-f-latitud="${a.latitud}"
                                        data-f-longitud="${a.longitud}"
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
    <form action="${base}/alojamientos" method="post" enctype="multipart/form-data" class="modal-form">
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

            <label class="campo">Latitud
                <input type="number" name="latitud" min="-35" max="-30" step="any" placeholder="-31.83812"></label>
            <label class="campo">Longitud
                <input type="number" name="longitud" min="-59" max="-53" step="any" placeholder="-57.89034"></label>
            <div class="campo ancho">
                <small>Para que se vea en el mapa. En Google Maps, clic derecho sobre el lugar: el primer número es la latitud y el segundo la longitud.</small>
            </div>
            <label class="campo">Distancia a la piscina (m)
                <input type="number" name="distanciaApiscina" min="0" step="any" value="0"></label>
            <label class="campo">
                <input type="checkbox" name="permiteMascotas">
                Permite mascotas
            </label>
            <c:if test="${not empty imagenBase}">
                <label class="campo ancho">Fotos (opcional)
                    <input type="file" name="fotos" accept="image/jpeg,image/png,image/webp,image/heic,image/heif,.heic,.heif" multiple
                           data-max-mb="5" data-max-files="${maxFotos}">
                    <small>JPG, PNG, WebP o HEIC (fotos de iPhone), hasta 5 MB cada una y ${maxFotos} como máximo. La primera será la portada.</small>
                </label>
            </c:if>
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
            <div class="campo ancho precios-pampita" id="preciosPampita" hidden>

                <strong>Precios según cantidad de huéspedes</strong>
                <div class="campos">
                    <label class="campo">
                        Hasta 2 personas ($)
                        <input type="number"
                               name="precioHasta2"
                               min="0"
                               step="0.01">
                    </label>

                    <label class="campo">
                        Hasta 4 personas ($)
                        <input type="number"
                               name="precioHasta4"
                               min="0"
                               step="0.01">
                    </label>
                    <label class="campo">
                        Hasta 6 personas ($)
                        <input type="number"
                               name="precioHasta6"
                               min="0"
                               step="0.01">
                    </label>
                </div>
            </div>

            <label class="campo">Latitud
                <input type="number" name="latitud" min="-35" max="-30" step="any" placeholder="-31.83812"></label>
            <label class="campo">Longitud
                <input type="number" name="longitud" min="-59" max="-53" step="any" placeholder="-57.89034"></label>
            <div class="campo ancho">
                <small>Para que se vea en el mapa. En Google Maps, clic derecho sobre el lugar: el primer número es la latitud y el segundo la longitud.</small>
            </div>
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

<%-- ============ Fotos de cada alojamiento ============ --%>
<c:forEach var="a" items="${alojamientos}">
    <c:set var="fotos" value="${a.listaImagenes}"/>
    <dialog id="m-fotos-${a.id}" class="modal modal-fotos" ${param.fotos == a.id ? 'data-abrir-al-cargar' : ''}>
        <div class="modal-form">
            <h2>Fotos de <c:out value="${a.nombre}"/></h2>

            <c:choose>
                <c:when test="${empty imagenBase}">
                    <p class="aviso aviso-error" role="alert"><span>Las fotos no están configuradas: falta definir la variable CLOUDINARY_URL en el servidor.</span></p>
                </c:when>
                <c:otherwise>
                    <p class="muted">${fn:length(fotos)} de ${maxFotos} fotos. La primera es la portada y es la que se ve en el inicio.</p>

                    <c:if test="${empty fotos}"><p class="vacio">Este alojamiento todavía no tiene fotos.</p></c:if>

                    <ul class="fotos-grid">
                        <c:forEach var="foto" items="${fotos}" varStatus="posicion">
                            <li class="foto-item">
                                <img src="${imagenBase},w_320,h_220,c_fill/${foto}"
                                     alt="Foto ${posicion.index + 1} de <c:out value='${a.nombre}'/>" loading="lazy">
                                <c:if test="${posicion.first}"><span class="foto-etiqueta">Portada</span></c:if>
                                <div class="foto-acciones">
                                    <c:if test="${not posicion.first}">
                                        <form action="${base}/alojamientos" method="post">
                                            <input type="hidden" name="_csrf" value="${csrf}">
                                            <input type="hidden" name="accion" value="portada">
                                            <input type="hidden" name="id" value="${a.id}">
                                            <input type="hidden" name="foto" value="<c:out value='${foto}'/>">
                                            <button type="submit" class="btn btn-editar">Usar de portada</button>
                                        </form>
                                    </c:if>
                                    <form action="${base}/alojamientos" method="post" data-confirmar="¿Eliminar esta foto? No se puede deshacer.">
                                        <input type="hidden" name="_csrf" value="${csrf}">
                                        <input type="hidden" name="accion" value="quitarFoto">
                                        <input type="hidden" name="id" value="${a.id}">
                                        <input type="hidden" name="foto" value="<c:out value='${foto}'/>">
                                        <button type="submit" class="btn btn-eliminar">Eliminar</button>
                                    </form>
                                </div>
                            </li>
                        </c:forEach>
                    </ul>

                    <c:choose>
                        <c:when test="${fn:length(fotos) < maxFotos}">
                            <form action="${base}/alojamientos" method="post" enctype="multipart/form-data" class="subir-fotos">
                                <input type="hidden" name="_csrf" value="${csrf}">
                                <input type="hidden" name="accion" value="subirFotos">
                                <input type="hidden" name="id" value="${a.id}">
                                <label class="campo ancho">Agregar fotos
                                    <input type="file" name="fotos" accept="image/jpeg,image/png,image/webp,image/heic,image/heif,.heic,.heif" multiple required
                                           data-max-mb="5" data-max-files="${maxFotos - fn:length(fotos)}">
                                    <small>JPG, PNG, WebP o HEIC (fotos de iPhone), hasta 5 MB cada una. Podés agregar ${maxFotos - fn:length(fotos)} más.</small>
                                </label>
                                <button type="submit" class="btn btn-primario">Subir fotos</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <p class="muted">Llegaste al máximo de ${maxFotos} fotos. Eliminá una para poder agregar otra.</p>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>

            <div class="modal-pie">
                <button type="button" class="btn btn-secundario" data-modal-cerrar>Cerrar</button>
            </div>
        </div>
    </dialog>
</c:forEach>


<%@ include file="layout-bottom.jspf" %>
