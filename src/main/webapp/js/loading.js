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

document.addEventListener("submit", function (evento) {

    const formulario = evento.target;

    if (!(formulario instanceof HTMLFormElement)) {
        return;
    }

    mostrarLoading();

    setTimeout(() => {
        if (document.visibilityState === "visible") {
            ocultarLoading();
        }
    }, 1500);
});


/*NAVEGACION ENTRE PAGINAS*/

document.addEventListener("click", function (evento) {

    const enlace = evento.target.closest("a[href]");

    if (!enlace) {
        return;
    }

    const href = enlace.getAttribute("href");

    if (
        !href ||
        href.startsWith("#") ||
        href.startsWith("javascript:") ||
        href.startsWith("mailto:") ||
        href.startsWith("tel:") ||
        enlace.hasAttribute("download") ||
        enlace.target === "_blank"
    ) {
        return;
    }

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
        window.location.href = enlace.href;
    }, 100);
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