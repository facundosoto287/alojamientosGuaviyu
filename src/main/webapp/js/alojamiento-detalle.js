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

/* ==================================================
   CALENDARIO DE DISPONIBILIDAD
   ================================================== */

const diasCalendario = document.getElementById("diasCalendario");
const tituloCalendario = document.getElementById("tituloCalendario");

const mesAnterior = document.getElementById("mesAnterior");
const mesSiguiente = document.getElementById("mesSiguiente");


/* Leer las reservas confirmadas enviadas desde el servlet */
const elementosReservas =
    document.querySelectorAll(".reserva-confirmada");


const reservasConfirmadas = [];

elementosReservas.forEach(elemento => {

    reservasConfirmadas.push({

        ingreso: elemento.dataset.ingreso,
        salida: elemento.dataset.salida

    });

});


/* Comenzamos mostrando el mes actual */
const hoy = new Date();

let anioActual = hoy.getFullYear();
let mesActual = hoy.getMonth();


const nombresMeses = [
    "Enero",
    "Febrero",
    "Marzo",
    "Abril",
    "Mayo",
    "Junio",
    "Julio",
    "Agosto",
    "Septiembre",
    "Octubre",
    "Noviembre",
    "Diciembre"
];


/* Comprueba si una fecha está ocupada */
function fechaOcupada(fecha) {

    for (const reserva of reservasConfirmadas) {

        const ingreso =
            new Date(reserva.ingreso + "T00:00:00");

        const salida =
            new Date(reserva.salida + "T00:00:00");


        /*
         * La fecha de ingreso está ocupada.
         * La fecha de salida queda disponible porque
         * el huésped abandona el alojamiento ese día.
         */
        if (fecha >= ingreso && fecha < salida) {
            return true;
        }
    }

    return false;
}

let fechaInicioSeleccionada = null;
let fechaFinSeleccionada = null;

function mismaFecha(fecha1, fecha2) {

    return fecha1.getFullYear() === fecha2.getFullYear()
        && fecha1.getMonth() === fecha2.getMonth()
        && fecha1.getDate() === fecha2.getDate();
}

function fechaDentroDelRango(fecha) {

    if (fechaInicioSeleccionada === null) {
        return false;
    }

    if (fechaFinSeleccionada === null) {
        return mismaFecha(fecha, fechaInicioSeleccionada);
    }

    return fecha >= fechaInicioSeleccionada
        && fecha <= fechaFinSeleccionada;
}

function rangoTieneOcupados(inicio, fin) {

    const fecha = new Date(inicio);

    while (fecha <= fin) {

        if (fechaOcupada(fecha)) {
            return true;
        }

        fecha.setDate(fecha.getDate() + 1);
    }

    return false;
}

/* Dibuja el mes */
function mostrarCalendario() {

    diasCalendario.innerHTML = "";

    tituloCalendario.textContent =
        nombresMeses[mesActual] + " " + anioActual;


    const primerDia =
        new Date(anioActual, mesActual, 1);


    const ultimoDia =
        new Date(anioActual, mesActual + 1, 0);


    /*
     * JavaScript:
     * domingo = 0
     * lunes = 1
     *
     * Nuestro calendario comienza en lunes.
     */
    let espaciosIniciales = primerDia.getDay() - 1;

    if (espaciosIniciales < 0) {
        espaciosIniciales = 6;
    }


    /* Espacios antes del día 1 */
    for (let i = 0; i < espaciosIniciales; i++) {

        const espacio = document.createElement("span");

        diasCalendario.appendChild(espacio);
    }


    /* Crear los días */
    for (let dia = 1; dia <= ultimoDia.getDate(); dia++) {

        const boton = document.createElement("button");

        boton.type = "button";
        boton.textContent = dia;


        const fecha = new Date(anioActual, mesActual, dia);

        const hoySinHora = new Date();
        hoySinHora.setHours(0, 0, 0, 0);

        fecha.setHours(0, 0, 0, 0);


        if (fecha < hoySinHora) {

            boton.classList.add("fecha-pasada");
            boton.disabled = true;
            boton.title = "No se pueden seleccionar fechas anteriores a hoy";

        } else if (fechaOcupada(fecha)) {

            boton.classList.add("ocupado");
            boton.disabled = true;
            boton.title = "Alojamiento ocupado";

        } else {

            if (fechaDentroDelRango(fecha)) {
                boton.classList.add("seleccionado");
            }

            boton.addEventListener("click", () => {
                seleccionarFecha(fecha);
            });

        }
        diasCalendario.appendChild(boton);
    }
}

function seleccionarFecha(fecha) {                  /*Primer clic:seleccionamos fecha de ingreso*/
    if (fechaInicioSeleccionada === null || fechaFinSeleccionada !== null) {
        fechaInicioSeleccionada = fecha;
        fechaFinSeleccionada = null;

    } else {                                /*Segundo clic:debe ser posterior al check-in*/
        if (fecha <= fechaInicioSeleccionada) {

            fechaInicioSeleccionada = fecha;
            fechaFinSeleccionada = null;

        } else {             /* Verifica que entre las dos fechas no exista una reserva confirmada*/
            if (rangoTieneOcupados(fechaInicioSeleccionada,fecha)) {
                alert("El rango seleccionado contiene fechas ocupadas.");
                return;
            }
            fechaFinSeleccionada = fecha;
        }
    }
    actualizarInputs();
    mostrarCalendario();
}

function actualizarInputs() {

    const inputIngreso =
        document.getElementById("fechaIngreso");

    const inputSalida =
        document.getElementById("fechaSalida");

    if (fechaInicioSeleccionada !== null) {
        inputIngreso.value =convertirFechaInput(fechaInicioSeleccionada);
    } else {
        inputIngreso.value = "";
    }
    if (fechaFinSeleccionada !== null) {
        inputSalida.value = convertirFechaInput(fechaFinSeleccionada);
    } else {
        inputSalida.value = "";
    }
}

function convertirFechaInput(fecha) {

    const anio = fecha.getFullYear();

    const mes = String(fecha.getMonth() + 1).padStart(2, "0");

    const dia = String(fecha.getDate()).padStart(2, "0");

    return `${anio}-${mes}-${dia}`;
}

/* Mes anterior */
mesAnterior.addEventListener("click", () => {

    mesActual--;

    if (mesActual < 0) {

        mesActual = 11;
        anioActual--;
    }

    mostrarCalendario();
});


/* Mes siguiente */
mesSiguiente.addEventListener("click", () => {

    mesActual++;

    if (mesActual > 11) {

        mesActual = 0;
        anioActual++;
    }

    mostrarCalendario();
});


/* Mostrar calendario al cargar la pantalla */
mostrarCalendario();