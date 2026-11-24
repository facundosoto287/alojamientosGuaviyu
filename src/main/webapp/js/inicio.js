/* VALIDACION DE FECHAS DEL BUSCADOR*/

/*CALENDARIOS CON LIBRERIA FLATPICKR*/

const calendarioSalida = flatpickr("#fechaSalida", {
    locale: "es",
    dateFormat: "Y-m-d",
    altInput: true,
    altFormat: "d/m/Y",
    minDate: "today",
    disableMobile: true
});


const calendarioIngreso = flatpickr("#fechaIngreso", {
    locale: "es",
    dateFormat: "Y-m-d",
    altInput: true,
    altFormat: "d/m/Y",
    minDate: "today",
    disableMobile: true,

    onChange: function (fechasSeleccionadas) {

        if (fechasSeleccionadas.length > 0) {

            const fechaSeleccionada = fechasSeleccionadas[0];

            const fechaMinimaSalida = new Date(fechaSeleccionada);

            fechaMinimaSalida.setDate(fechaMinimaSalida.getDate() + 2);

            calendarioSalida.set("minDate", fechaMinimaSalida); /* La salida no puede ser anterior al ingreso */


            /* Si ya había una salida menor al ingreso, se limpia */
            if (calendarioSalida.selectedDates.length > 0 && calendarioSalida.selectedDates[0] < fechaMinimaSalida) {
                calendarioSalida.clear();
            }
        }
    }
});

window.addEventListener("load", function () {

    const preloader = document.getElementById("preloader");

    if (!preloader) return;
    const yaMostrado = sessionStorage.getItem("preloaderMostrado");

    if (yaMostrado === "true") {
        preloader.style.display = "none";
        return;
    }

    sessionStorage.setItem("preloaderMostrado", "true");

    setTimeout(function () {
        preloader.classList.add("oculto");
    }, 1200);
});

const btnVolverArriba = document.getElementById("btnVolverArriba");
if (btnVolverArriba) {
    window.addEventListener("scroll", function () {
        if (window.scrollY > 350) {
            btnVolverArriba.classList.add("visible");
        } else {
            btnVolverArriba.classList.remove("visible");
        }

    });

    btnVolverArriba.addEventListener("click", function () {
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    });
}