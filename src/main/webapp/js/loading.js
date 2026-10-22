const loadingOverlay = document.getElementById("loadingOverlay");

function mostrarLoading() {
    if (loadingOverlay) {
        loadingOverlay.hidden = false;
    }
}


function ocultarLoading() {
    if (loadingOverlay) {
        loadingOverlay.hidden = true;
    }
}


/*FORMULARIOS*/

document.querySelectorAll("form").forEach(formulario => {

    formulario.addEventListener("submit", () => {

        mostrarLoading();

    });

});


/*NAVEGACION ENTRE PAGINAS*/

document.querySelectorAll("a[href]").forEach(enlace => {

    enlace.addEventListener("click", function (evento) {

        const href = this.getAttribute("href");

        /* No mostrar loading en enlaces especiales */
        if (
            !href ||
            href.startsWith("#") ||
            href.startsWith("javascript:") ||
            href.startsWith("mailto:") ||
            href.startsWith("tel:") ||
            this.hasAttribute("download") ||
            this.target === "_blank"
        ) {
            return;
        }

        /* Mantener Ctrl + click / abrir en nueva pestaña */
        if (
            evento.ctrlKey ||
            evento.metaKey ||
            evento.shiftKey ||
            evento.altKey
        ) {
            return;
        }

        evento.preventDefault();

        mostrarLoading();

        setTimeout(() => {

            window.location.href = this.href;

        }, 100);

    });

});


/*OCULTAR AL VOLVER CON ATRAS*/

window.addEventListener("pageshow", () => {
    ocultarLoading();
});

document.querySelectorAll("select").forEach(select => {

    select.addEventListener("change", function () {

        const formulario = this.form;
        if (!formulario) {
            return;
        }
        mostrarLoading();
    });

});