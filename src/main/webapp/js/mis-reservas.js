const filtros = document.querySelectorAll(".filtro-reserva");
const filas = document.querySelectorAll(".reserva-fila");

const detalleVacio = document.getElementById("detalleVacio");
const detalleContenido = document.getElementById("detalleContenido");

const detalleNombre = document.getElementById("detalleNombre");

const detalleIngreso = document.getElementById("detalleIngreso");
const detalleSalida = document.getElementById("detalleSalida");

const detalleHuespedes = document.getElementById("detalleHuespedes");
const detalleNoches = document.getElementById("detalleNoches");

const detalleHorarioSalida = document.getElementById("detalleHorarioSalida");
const detalleMedioDia = document.getElementById("detalleMedioDia");

const detallePrecioNoche = document.getElementById("detallePrecioNoche");
const detalleCalculoNoches = document.getElementById("detalleCalculoNoches");
const detalleSubtotal = document.getElementById("detalleSubtotal");
const detalleTotal = document.getElementById("detalleTotal");

const detalleFilaMedioDia = document.getElementById("detalleFilaMedioDia");
const detallePrecioMedioDia = document.getElementById("detallePrecioMedioDia");

const detalleEstado = document.getElementById("detalleEstado");

const filtroVacio = document.getElementById("filtroVacio");
const filtroVacioTitulo = document.getElementById("filtroVacioTitulo");

const detalleColchonExtra = document.getElementById("detalleColchonExtra");

const detalleFilaColchonExtra = document.getElementById("detalleFilaColchonExtra");

const detallePrecioColchonExtra = document.getElementById("detallePrecioColchonExtra");

const detalleFilaAjuste = document.getElementById("detalleFilaAjuste");
const detalleEtiquetaAjuste = document.getElementById("detalleEtiquetaAjuste");
const detallePrecioAjuste = document.getElementById("detallePrecioAjuste");


/*FORMATEAR PRECIO*/
function formatearPrecio(valor) {

    return Number(valor).toLocaleString("es-UY", {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2
    });
}


/*FILTROS*/
filtros.forEach(filtro => {

    filtro.addEventListener("click", () => {

        filtros.forEach(boton => {
            boton.classList.remove("activo");
        });

        filtro.classList.add("activo");

        const estadoSeleccionado = filtro.dataset.filtro;

        filas.forEach(fila => {
            fila.classList.remove("seleccionada");
        });

        detalleVacio.hidden = false;
        detalleContenido.hidden = true;

        let cantidadVisibles = 0;

        filas.forEach(fila => {

            const estado = fila.dataset.estado;

            if (estadoSeleccionado === "TODAS" || estado === estadoSeleccionado) {
                fila.classList.remove("oculta");
                cantidadVisibles++;

            } else {
                fila.classList.add("oculta");
            }

        });


        /*MENSAJE CUANDO NO HAY RESERVAS*/
        if (filtroVacio) {
            if (cantidadVisibles === 0) {
                filtroVacio.hidden = false;

                if (estadoSeleccionado === "PENDIENTE") {
                    filtroVacioTitulo.textContent = "No tenés reservas pendientes";

                } else if (estadoSeleccionado === "CONFIRMADA") {
                    filtroVacioTitulo.textContent = "No tenés reservas confirmadas";

                } else if (estadoSeleccionado === "CANCELADA") {
                    filtroVacioTitulo.textContent = "No tenés reservas canceladas";

                } else {
                    filtroVacioTitulo.textContent = "No tenés reservas";
                }

            } else {
                filtroVacio.hidden = true;
            }
        }

    });

});


/*VER DETALLE*/
filas.forEach(fila => {

    const boton = fila.querySelector(".btn-ver-detalle");

    if (!boton) {
        return;
    }


    boton.addEventListener("click", () => {

        filas.forEach(item => {
            item.classList.remove("seleccionada");
        });

        fila.classList.add("seleccionada");

        const nombre = fila.dataset.nombre;

        const ingreso = fila.dataset.ingreso;

        const salida = fila.dataset.salida;

        const huespedes = fila.dataset.huespedes;

        const estado = fila.dataset.estadoReserva;

        const noches = Number(fila.dataset.noches);

        const precio = Number(fila.dataset.precio);

        const total = Number(fila.dataset.total);

        const medioDiaExtra = fila.dataset.medioDia === "true";

        const colchonExtra = fila.dataset.colchonExtra === "true";


        /*DATOS*/
        detalleNombre.textContent = nombre;

        detalleIngreso.textContent = formatearFecha(ingreso);
        detalleSalida.textContent = formatearFecha(salida);

        detalleHuespedes.textContent = huespedes + " huéspedes";

        detalleNoches.textContent = noches;


        /*MEDIO DIA*/
        if (medioDiaExtra) {
            detalleHorarioSalida.textContent = "21:00 hs";
            detalleMedioDia.textContent = "Sí";

        } else {
            detalleHorarioSalida.textContent = "10:00 hs";
            detalleMedioDia.textContent = "No";
        }

        if (colchonExtra) {
            detalleColchonExtra.textContent = "Sí";
        } else {
            detalleColchonExtra.textContent = "No";
        }


        /*PRECIOS*/
        const subtotalNoches = noches * precio;

        detallePrecioNoche.textContent = "$ " + formatearPrecio(precio);

        detalleCalculoNoches.textContent = noches + (noches === 1 ? " noche" : " noches") + " × $ " + formatearPrecio(precio);

        detalleSubtotal.textContent = "$ " + formatearPrecio(subtotalNoches);


        /*PRECIO MEDIO DIA*/
        if (medioDiaExtra) {
            const precioMedioDia = precio / 2;

            detalleFilaMedioDia.hidden = false;

            detallePrecioMedioDia.textContent = "$ " + formatearPrecio(precioMedioDia);

        } else {
            detalleFilaMedioDia.hidden = true;

            detallePrecioMedioDia.textContent = "";
        }

        /*PRECIO COLCHON EXTRA*/
        if (colchonExtra) {

            const precioColchonExtra = noches * 200;

            detalleFilaColchonExtra.hidden = false;

            detallePrecioColchonExtra.textContent =
                "$ " + formatearPrecio(precioColchonExtra);

        } else {

            detalleFilaColchonExtra.hidden = true;

            detallePrecioColchonExtra.textContent = "";
        }

        /*PRECIO ACORDADO: la diferencia con la cuenta normal se muestra como descuento o ajuste*/
        const calculado = subtotalNoches
            + (medioDiaExtra ? precio / 2 : 0)
            + (colchonExtra ? noches * 200 : 0);

        const ajuste = total - calculado;

        if (fila.dataset.precioAcordado === "true" && ajuste !== 0) {

            detalleFilaAjuste.hidden = false;

            detalleEtiquetaAjuste.textContent = ajuste < 0 ? "Descuento" : "Ajuste de precio";

            detallePrecioAjuste.textContent =
                (ajuste < 0 ? "- $ " : "+ $ ") + formatearPrecio(Math.abs(ajuste));

        } else {

            detalleFilaAjuste.hidden = true;

            detallePrecioAjuste.textContent = "";
        }

        detalleTotal.textContent = "$ " + formatearPrecio(total);


        /*ESTADO*/

        detalleEstado.className = "estado";


        if (estado === "PENDIENTE") {
            detalleEstado.textContent = "Pendiente";

            detalleEstado.classList.add("estado-pendiente");

        } else if (estado === "CONFIRMADA") {
            detalleEstado.textContent = "Confirmada";

            detalleEstado.classList.add("estado-confirmada");
        } else {
            detalleEstado.textContent = "Cancelada";

            detalleEstado.classList.add("estado-cancelada");
        }
        detalleVacio.hidden = true;
        detalleContenido.hidden = false;
        if (window.innerWidth <= 1050) {

            document
                .getElementById("detalleReserva")
                .scrollIntoView({
                    behavior: "smooth",
                    block: "start"
                });
        }
    });

});

function formatearFecha(fecha) {

    if (!fecha) {
        return "-";
    }

    const partes = fecha.split("-");

    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}