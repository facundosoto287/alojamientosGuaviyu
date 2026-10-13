const botonMenu = document.getElementById("menuHamburguesa");

const menuNavegacion = document.getElementById("menuNavegacion");

if (botonMenu && menuNavegacion) {
    botonMenu.addEventListener("click", () => {

        menuNavegacion.classList.toggle("activo");
        botonMenu.classList.toggle("activo");

        const abierto = menuNavegacion.classList.contains("activo");

        botonMenu.setAttribute("aria-expanded", abierto);
    });

    /* Cerrar el menú al tocar una opción */
    menuNavegacion
        .querySelectorAll("a")
        .forEach(enlace => {

            enlace.addEventListener("click", () => {

                menuNavegacion.classList.remove("activo");
                botonMenu.classList.remove("activo");

                botonMenu.setAttribute("aria-expanded", "false");
            });

        });
}

const headerUsuarioBoton = document.getElementById("headerUsuarioBoton");

const headerUsuarioDropdown = document.querySelector(".header-usuario-dropdown");


if (headerUsuarioBoton && headerUsuarioDropdown) {

    headerUsuarioBoton.addEventListener("click", (event) => {

        if (window.innerWidth <= 700) {
            return;
        }

        event.stopPropagation();

        headerUsuarioDropdown.classList.toggle("abierto");

    });

}