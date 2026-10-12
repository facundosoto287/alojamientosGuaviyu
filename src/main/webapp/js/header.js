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