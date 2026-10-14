const fotos = document.querySelectorAll(".foto-galeria");

const visor = document.getElementById("visorImagen");
const imagenAmpliada = document.getElementById("imagenAmpliada");

const cerrarVisor = document.getElementById("cerrarVisor");
const imagenAnterior = document.getElementById("imagenAnterior");
const imagenSiguiente = document.getElementById("imagenSiguiente");

let indiceActual = 0;


/* Abrir una imagen */
fotos.forEach((foto, indice) => {

    foto.addEventListener("click", () => {

        indiceActual = indice;

        mostrarImagen();

        visor.classList.add("activo");
    });

});


/* Mostrar imagen actual */
function mostrarImagen() {

    imagenAmpliada.src = fotos[indiceActual].src;
    imagenAmpliada.alt = fotos[indiceActual].alt;
}


/* Imagen siguiente */
imagenSiguiente.addEventListener("click", () => {

    indiceActual++;

    if (indiceActual >= fotos.length) {
        indiceActual = 0;
    }

    mostrarImagen();
});


/* Imagen anterior */
imagenAnterior.addEventListener("click", () => {

    indiceActual--;

    if (indiceActual < 0) {
        indiceActual = fotos.length - 1;
    }

    mostrarImagen();
});


/* Cerrar */
cerrarVisor.addEventListener("click", () => {

    visor.classList.remove("activo");
});


/* Cerrar tocando el fondo */
visor.addEventListener("click", (evento) => {

    if (evento.target === visor) {
        visor.classList.remove("activo");
    }

});

const fechasOcupadas = new Set();

document
    .querySelectorAll(".reserva-confirmada")
    .forEach(reserva => {

        const ingreso = new Date(reserva.dataset.ingreso + "T00:00:00");

        const salida = new Date(reserva.dataset.salida + "T00:00:00");

        const medioDiaExtra = reserva.dataset.medioDia === "true";

        const fechaActual = new Date(ingreso);


        /*Sin medio dia: la fecha de salida queda libre. Con medio día:la fecha de salida también queda ocupada*/

        while (medioDiaExtra ? fechaActual <= salida : fechaActual < salida) {

            fechasOcupadas.add(convertirFecha(fechaActual));

            fechaActual.setDate(fechaActual.getDate() + 1);
        }
    });

/*CALENDARIO DE RESERVA CON LIBRERIA FLATPICKR*/

const inputFechaIngreso = document.getElementById("fechaIngreso");
const inputFechaSalida = document.getElementById("fechaSalida");
const mensajeReserva = document.getElementById("mensajeReserva");


/* Leer las reservas confirmadas enviadas desde el servlet */
const reservasConfirmadas = [];

document
    .querySelectorAll(".reserva-confirmada")
    .forEach(reserva => {

        const ingreso = reserva.dataset.ingreso;

        const salida = reserva.dataset.salida;

        const medioDiaExtra = reserva.dataset.medioDia === "true";


        const ultimoDiaOcupado = new Date(salida + "T00:00:00");


        /*Si NO tiene medio día: salida 22 → ocupamos hasta el 21. Si tiene medio día:salida 22 → ocupamos también el 22.*/
        if (!medioDiaExtra) {
            ultimoDiaOcupado.setDate(ultimoDiaOcupado.getDate() - 1);
        }


        reservasConfirmadas.push({

            from: ingreso,

            to: convertirFecha(ultimoDiaOcupado)
        });
    });


/* Crear calendario */
const calendarioReserva = flatpickr("#calendarioReserva", {

    locale: "es",

    mode: "range",

    dateFormat: "Y-m-d",

    minDate: "today",

    inline: true,

    disableMobile: true,

    disable: reservasConfirmadas,

    onDayCreate: function (dObj, dStr, fp, dayElem) {

        const fecha = convertirFecha(dayElem.dateObj);

        if (fechasOcupadas.has(fecha)) {
            dayElem.classList.add("fecha-ocupada");
        }
    },

    onChange: function (fechasSeleccionadas) {

        if (fechasSeleccionadas.length === 1) { /* Primer click = ingreso */

            inputFechaIngreso.value = convertirFecha(fechasSeleccionadas[0]);

            inputFechaSalida.value = "";

            mensajeReserva.hidden = true;
        }

        if (fechasSeleccionadas.length === 2) { /* Segundo click = salida */

            const ingreso = fechasSeleccionadas[0];
            const salida = fechasSeleccionadas[1];

            const fechaMinimaSalida = new Date(ingreso);

            fechaMinimaSalida.setDate(fechaMinimaSalida.getDate() + 1);

            if (salida < fechaMinimaSalida) {

                mensajeReserva.textContent = "La estadía debe ser de al menos una noche";

                mensajeReserva.hidden = false;

                calendarioReserva.clear();

                inputFechaIngreso.value = "";
                inputFechaSalida.value = "";

                return;
            }

            mensajeReserva.hidden = true;

            inputFechaIngreso.value = convertirFecha(ingreso);

            inputFechaSalida.value = convertirFecha(salida);
        }
    }
});

/* Convertir Date a YYYY-MM-DD */
function convertirFecha(fecha) {

    const anio = fecha.getFullYear();

    const mes = String(
        fecha.getMonth() + 1
    ).padStart(2, "0");

    const dia = String(
        fecha.getDate()
    ).padStart(2, "0");

    return `${anio}-${mes}-${dia}`;
}