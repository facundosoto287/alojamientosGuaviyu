const formulario = document.querySelector(".buscador-inicio");

const campos = [
    {
        id: "fechaIngreso",
        nombre: "Check-in"
    },
    {
        id: "fechaSalida",
        nombre: "Check-out"
    },
    {
        id: "huespedes",
        nombre: "Huéspedes"
    },
    {
        id: "precioMax",
        nombre: "Precio máximo"
    }
];

formulario.addEventListener("submit", function (evento) {
    for (const campoInfo of campos) {
        const campo = document.getElementById(campoInfo.id);

        if (!campo.value) {
            evento.preventDefault();
            campo.setCustomValidity(
                "Para filtrar se debe completar " + campoInfo.nombre + "."
            );
            campo.reportValidity();
            return;
        }
    }
});

for (const campoInfo of campos) {
    const campo = document.getElementById(campoInfo.id);

    campo.addEventListener("input", function () {
        campo.setCustomValidity("");
    });
}