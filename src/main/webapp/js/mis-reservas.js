const filtros = document.querySelectorAll(".filtro-reserva");
const filas = document.querySelectorAll(".reserva-fila");

const detalleVacio = document.getElementById("detalleVacio");
const detalleContenido = document.getElementById("detalleContenido");

const detalleNombre = document.getElementById("detalleNombre");
const detalleUbicacion = document.getElementById("detalleUbicacion");

const detalleIngreso = document.getElementById("detalleIngreso");
const detalleSalida = document.getElementById("detalleSalida");

const detalleHuespedes = document.getElementById("detalleHuespedes");
const detalleNoches = document.getElementById("detalleNoches");

const detallePrecioNoche = document.getElementById("detallePrecioNoche");
const detalleCalculoNoches = document.getElementById("detalleCalculoNoches");
const detalleSubtotal = document.getElementById("detalleSubtotal");
const detalleTotal = document.getElementById("detalleTotal");

const detalleEstado = document.getElementById("detalleEstado");

const filtroVacio = document.getElementById("filtroVacio");
const filtroVacioTitulo = document.getElementById("filtroVacioTitulo");


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

        let cantidadVisibles = 0;


        filas.forEach(fila => {

            const estado = fila.dataset.estado;

            if (estadoSeleccionado === "TODAS" || estado === estadoSeleccionado) {

                fila.hidden = false;
                cantidadVisibles++;

            } else {
                fila.hidden = true;
            }

        });

        /* MENSAJE CUANDO NO HAY RESERVAS */

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

        const ubicacion = fila.dataset.ubicacion;

        const ingreso = fila.dataset.ingreso;

        const salida = fila.dataset.salida;

        const huespedes = fila.dataset.huespedes;

        const estado = fila.dataset.estadoReserva;

        const noches = fila.dataset.noches;

        const precio = fila.dataset.precio;

        const total = fila.dataset.total;

        /* DATOS */
        detalleNombre.textContent = nombre;

        detalleUbicacion.textContent = ubicacion;

        detalleIngreso.textContent = ingreso;

        detalleSalida.textContent = salida;

        detalleHuespedes.textContent = huespedes + " huéspedes";

        detalleNoches.textContent = noches;

        /* PRECIOS */
        detallePrecioNoche.textContent = "$ " + formatearPrecio(precio);

        detalleCalculoNoches.textContent = noches + " noches × $ " + formatearPrecio(precio);

        detalleSubtotal.textContent = "$ " + formatearPrecio(total);

        detalleTotal.textContent = "$ " + formatearPrecio(total);

        /* ESTADO */
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
    });

});