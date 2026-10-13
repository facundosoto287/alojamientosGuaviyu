<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mapa | Alojamientos Guaviyú</title>

    <!-- Leaflet: librería gratuita de mapas (no necesita clave) -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mapa.css?v=4">
</head>
<body>
<%@ include file="components/header.jsp" %>

<main class="mapa-pagina">
    <section class="mapa-intro">
        <span class="etiqueta">Mapa</span>
        <h1>Termas de Guaviyú, Paysandú</h1>
        <p>Mové el mapa, acercate con la ruedita y tocá una casita para ver la información del alojamiento.</p>
    </section>

    <div class="mapa-caja">
        <div id="mapa" style="height: 72vh; min-height: 440px;" aria-label="Mapa de las Termas de Guaviyú"></div>

        <button type="button" id="btn-centrar" class="btn-centrar">Ver todo</button>
    </div>

    <!-- Leyenda: además de explicar los íconos, permite mostrar u ocultar cada tipo -->
    <fieldset class="leyenda">
        <legend>Mostrar en el mapa</legend>
        <label><input type="checkbox" data-capa="alojamientos" checked>
            <span class="icono-mini icono-mini--casa"><img src="${pageContext.request.contextPath}/img/logoAlojamientosGuaviyu.png" alt=""></span> Nuestros alojamientos</label>
        <label><input type="checkbox" data-capa="termas" checked>
            <span class="icono-mini icono-mini--termas">♨️</span> Termas y naturaleza</label>
        <label><input type="checkbox" data-capa="comida" checked>
            <span class="icono-mini icono-mini--comida">🍴</span> Dónde comer</label>
        <label><input type="checkbox" data-capa="compras" checked>
            <span class="icono-mini icono-mini--compras">🛒</span> Compras</label>
    </fieldset>
    <p id="sin-ubicacion" class="sin-ubicacion" hidden></p>
</main>

<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<script>
    // Datos que manda el servlet: centro del mapa + alojamientos de la base de datos
    const datos = ${datosMapa};
    const contexto = "${pageContext.request.contextPath}";

    // ⚠️ Dirección de la página de detalle de un alojamiento.
    // Tiene que ser igual al @WebServlet("...") del servlet que muestra alojamiento-detalle.jsp
    const RUTA_DETALLE = "/alojamiento-detalle";

    // ⚠️ Logo que va adentro del pin de los alojamientos (el mismo que usa el header)
    const RUTA_LOGO = contexto + "/img/logoAlojamientosGuaviyu.png";

    // ===== 1. Fondos del mapa =====
    // Satélite: foto aérea real, se ven las casas, las piscinas y las canchas
    const satelite = L.layerGroup([
        L.tileLayer("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}", {
            maxNativeZoom: 18, maxZoom: 20,
            attribution: "Imágenes &copy; Esri, Maxar, Earthstar Geographics"
        }),
        // Encima, los nombres de las calles y rutas
        L.tileLayer("https://server.arcgisonline.com/ArcGIS/rest/services/Reference/World_Transportation/MapServer/tile/{z}/{y}/{x}", {
            maxNativeZoom: 18, maxZoom: 20, opacity: 0.8
        })
    ]);

    const mapa = L.map("mapa", { layers: [satelite], zoomControl: false })
        .setView([datos.centro.lat, datos.centro.lng], 17);
    L.control.zoom({ position: "bottomright" }).addTo(mapa);
    L.control.scale({ imperial: false, position: "bottomleft" }).addTo(mapa);

    // ===== 2. Íconos =====
    function crearIcono(emoji, clase, tamanio) {
        return L.divIcon({
            className: "",
            html: '<div class="pin ' + clase + '"><span>' + emoji + '</span></div>',
            iconSize: [tamanio, tamanio],
            iconAnchor: [tamanio / 2, tamanio],
            popupAnchor: [0, -tamanio],
            tooltipAnchor: [0, -tamanio - 2]
        });
    }

    const iconos = {
        // Pin de los alojamientos: con el logo adentro (si el logo no carga, muestra 🏠)
        alojamientos: L.divIcon({
            className: "",
            html: '<div class="pin pin--casa pin--logo">' +
                  '<img src="' + RUTA_LOGO + '" alt="" onerror="this.parentNode.classList.add(\'sin-logo\')">' +
                  '<span>🏠</span>' +
                  '</div>',
            iconSize: [38, 38],          // tamaño del pin con logo (antes 54)
            iconAnchor: [19, 38],
            popupAnchor: [0, -38],
            tooltipAnchor: [0, -40]
        }),
        termas:       crearIcono("♨️", "pin--termas", 34),
        comida:       crearIcono("🍴", "pin--comida", 34),
        compras:      crearIcono("🛒", "pin--compras", 34)
    };

    // Un grupo por tipo, para poder mostrarlos u ocultarlos desde la leyenda
    const grupos = {
        alojamientos: L.layerGroup().addTo(mapa),
        termas: L.layerGroup().addTo(mapa),
        comida: L.layerGroup().addTo(mapa),
        compras: L.layerGroup().addTo(mapa)
    };

    // Agrega un texto a un elemento (con textContent, así no se mete HTML de la base de datos)
    function agregarTexto(padre, etiqueta, clase, texto) {
        const elemento = document.createElement(etiqueta);
        if (clase) {
            elemento.className = clase;
        }
        elemento.textContent = texto;
        padre.appendChild(elemento);
    }

    // ===== 3. Nuestros alojamientos (vienen de la base de datos) =====
    function crearCartel(a) {
        const cartel = document.createElement("div");
        cartel.className = "cartel";

        agregarTexto(cartel, "span", "cartel__tipo", "Alojamiento");
        agregarTexto(cartel, "h3", "", a.nombre);
        if (a.ubicacion) {
            agregarTexto(cartel, "p", "cartel__sub", "📍 " + a.ubicacion);
        }
        if (a.descripcion) {
            agregarTexto(cartel, "p", "cartel__desc", a.descripcion);
        }

        const datosCortos = document.createElement("div");
        datosCortos.className = "cartel__datos";
        agregarTexto(datosCortos, "span", "", "👥 " + a.capacidad + " personas");
        agregarTexto(datosCortos, "span", "", "💲 " + a.precio.toLocaleString("es-UY") + " por noche");
        cartel.appendChild(datosCortos);

        const enlace = document.createElement("a");
        enlace.href = contexto + RUTA_DETALLE + "?id=" + a.id; // página de ESTE alojamiento
        enlace.className = "cartel__boton";
        enlace.textContent = "Ver este alojamiento";
        cartel.appendChild(enlace);

        return cartel;
    }

    const puntosTodos = [];
    const sinUbicacion = [];
    const marcadoresPorId = {}; // para poder abrir una casita desde la dirección (?alojamiento=3)

    datos.alojamientos.forEach(function (a) {
        if (a.lat === null || a.lng === null) {
            sinUbicacion.push(a.nombre);
            return;
        }
        marcadoresPorId[a.id] = L.marker([a.lat, a.lng], { icon: iconos.alojamientos, zIndexOffset: 1000 })
            .bindPopup(crearCartel(a), { maxWidth: 290 })
            .addTo(grupos.alojamientos);
        marcadoresPorId[a.id].nombre = a.nombre; // para la etiqueta con el nombre (ver punto 6)
        puntosTodos.push([a.lat, a.lng]);
    });

    if (sinUbicacion.length > 0) {
        const aviso = document.getElementById("sin-ubicacion");
        aviso.textContent = "Todavía sin ubicación en el mapa: " + sinUbicacion.join(", ") + ".";
        aviso.hidden = false;
    }

    // ===== 4. Lugares de las termas (para que el turista se ubique) =====
    const lugares = [
        { tipo: "termas",  nombre: "Piscinas termales",    info: "Piscinas al aire libre del complejo termal", lat: -31.84163, lng: -57.88647 },
        { tipo: "termas",  nombre: "Piscinas cerradas",    info: "Piscinas termales techadas",                 lat: -31.83906, lng: -57.88844 },
        { tipo: "termas",  nombre: "Camping",              info: "Zona de camping con parrilleros",            lat: -31.84261, lng: -57.88586 },
        { tipo: "termas",  nombre: "Sendero Guaviyú",      info: "Sendero junto al arroyo",                    lat: -31.84408, lng: -57.88912 },
        { tipo: "comida",  nombre: "Parador UTU",          info: "Restaurante dentro del predio termal",       lat: -31.84142, lng: -57.88769 },
        { tipo: "comida",  nombre: "Parrilla Mi Familia",  info: "Restaurante y parrilla",                     lat: -31.84193, lng: -57.88628 },
        { tipo: "comida",  nombre: "Dalian Coffee & Market", info: "Cafetería, mercado y restaurante",         lat: -31.83612, lng: -57.89069 },
        { tipo: "compras", nombre: "Minimercado Dumbo",    info: "Almacén y carnicería",                       lat: -31.84175, lng: -57.88618 }
    ];

    lugares.forEach(function (lugar) {
        const cartel = document.createElement("div");
        cartel.className = "cartel";
        agregarTexto(cartel, "h3", "", lugar.nombre);
        agregarTexto(cartel, "p", "cartel__desc", lugar.info);

        L.marker([lugar.lat, lugar.lng], { icon: iconos[lugar.tipo] })
            .bindPopup(cartel)
            .bindTooltip(lugar.nombre, { direction: "top" }) // se ve al pasar el mouse
            .addTo(grupos[lugar.tipo]);
        puntosTodos.push([lugar.lat, lugar.lng]);
    });

    // ===== 5. Mostrar / ocultar tipos desde la leyenda =====
    document.querySelectorAll(".leyenda input").forEach(function (casilla) {
        casilla.addEventListener("change", function () {
            const grupo = grupos[casilla.dataset.capa];
            if (casilla.checked) {
                mapa.addLayer(grupo);
            } else {
                mapa.removeLayer(grupo);
            }
        });
    });

    // ===== 6. Etiquetas con el nombre de cada alojamiento =====
    // Con el mapa lejos se ven solo al pasar el mouse (si no, se pisan unas con otras);
    // al acercarse mucho quedan siempre visibles.
    function actualizarEtiquetas() {
        const fijas = mapa.getZoom() >= 18;
        Object.values(marcadoresPorId).forEach(function (marcador) {
            marcador.unbindTooltip();
            marcador.bindTooltip(marcador.nombre, {
                permanent: fijas, direction: "top", className: "etiqueta-casa"
            });
        });
    }
    mapa.on("zoomend", actualizarEtiquetas);
    actualizarEtiquetas();

    // ===== 7. Encuadrar todo =====
    function verTodo() {
        mapa.fitBounds(puntosTodos, { padding: [60, 60], maxZoom: 18 });
    }
    document.getElementById("btn-centrar").addEventListener("click", verTodo);

    // Si se llegó desde "Ver en el mapa" (mapa?alojamiento=3), ir directo a esa casita
    const idPedido = new URLSearchParams(window.location.search).get("alojamiento");
    const marcadorPedido = marcadoresPorId[idPedido];
    if (marcadorPedido) {
        mapa.setView(marcadorPedido.getLatLng(), 18);
        marcadorPedido.openPopup();
    } else {
        verTodo();
    }
</script>
</body>
</html>
