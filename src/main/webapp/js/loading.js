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

    formulario.addEventListener("submit", function () {

        setTimeout(() => {

            if (document.visibilityState === "visible") {
                ocultarLoading();
            }

        }, 1500);

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

document.querySelectorAll("select").forEach(select => {

    select.addEventListener("change", function () {

        const onchange = this.getAttribute("onchange");

        if (
            this.form &&
            onchange &&
            onchange.includes("submit")
        ) {
            mostrarLoading();
        }
    });
});

/*OCULTAR AL VOLVER CON ATRAS*/

window.addEventListener("pageshow", () => {
    ocultarLoading();
});