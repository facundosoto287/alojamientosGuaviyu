// Panel de administración: ventanas modales, confirmaciones y pequeños atajos.
(function () {
    'use strict';

    // Abrir una ventana. Si el botón trae data-f-<campo>, rellena el formulario de la ventana (para "Editar").
    document.querySelectorAll('[data-modal-abrir]').forEach(function (boton) {
        boton.addEventListener('click', function () {
            var ventana = document.getElementById(boton.dataset.modalAbrir);
            if (!ventana) return;

            Array.prototype.forEach.call(boton.attributes, function (atributo) {
                if (atributo.name.indexOf('data-f-') !== 0) return;
                var campo = atributo.name.slice(7); // el navegador pasa los nombres de atributos a minúsculas
                Array.prototype.forEach.call(ventana.querySelectorAll('input, textarea, select'), function (elemento) {
                    if (elemento.name && elemento.name.toLowerCase() === campo) {
                        elemento.value = atributo.value;
                    }
                });
            });
            // En la edición de Pampita muestra los precios según cantidad de huéspedes.
            if (boton.dataset.modalAbrir === 'm-editar') {
                var preciosPampita = ventana.querySelector('#preciosPampita');
                var nombre = boton.getAttribute('data-f-nombre') || '';

                if (preciosPampita) {
                    var esPampita = nombre.trim().toLowerCase() === 'pampita';

                    preciosPampita.hidden = !esPampita;

                    Array.prototype.forEach.call(
                        preciosPampita.querySelectorAll('input'),
                        function (input) {
                            input.required = esPampita;
                        }
                    );
                }
            }
            ventana.showModal();
            var primero = ventana.querySelector('input:not([type=hidden]), textarea, select');
            if (primero) primero.focus();
        });
    });

    // Cerrar con el botón "Cancelar" o haciendo clic fuera de la ventana.
    document.querySelectorAll('dialog.modal').forEach(function (ventana) {
        ventana.addEventListener('click', function (evento) {
            if (evento.target === ventana) ventana.close();
        });
    });
    document.querySelectorAll('[data-modal-cerrar]').forEach(function (boton) {
        boton.addEventListener('click', function () {
            var ventana = boton.closest('dialog');
            if (ventana) ventana.close();
        });
    });

    // Confirmaciones: los formularios marcados con data-confirmar piden confirmar en una ventana propia
    // (con el estilo del panel) antes de enviarse, en lugar del aviso gris del navegador.
    var ventanaConfirmar = null;
    var formularioPendiente = null;

    function crearVentanaConfirmar() {
        var ventana = document.createElement('dialog');
        ventana.className = 'modal modal-chico modal-confirmar';
        ventana.setAttribute('aria-labelledby', 'confirmar-titulo');
        ventana.innerHTML =
            '<div class="modal-form">' +
            '<span class="confirmar-icono" aria-hidden="true">!</span>' +
            '<h2 id="confirmar-titulo"></h2>' +
            '<p class="confirmar-texto"></p>' +
            '<div class="modal-pie">' +
            '<button type="button" class="btn btn-secundario" data-confirmar-no>Volver</button>' +
            '<button type="button" class="btn" data-confirmar-si>Aceptar</button>' +
            '</div></div>';
        document.body.appendChild(ventana);

        ventana.addEventListener('click', function (evento) {
            if (evento.target === ventana) ventana.close();
        });
        ventana.querySelector('[data-confirmar-no]').addEventListener('click', function () {
            ventana.close();
        });
        ventana.querySelector('[data-confirmar-si]').addEventListener('click', function () {
            var formulario = formularioPendiente;
            formularioPendiente = null;
            ventana.close();
            if (formulario) formulario.submit(); // submit() no vuelve a disparar el evento "submit"
        });
        ventana.addEventListener('close', function () {
            formularioPendiente = null;
        });
        return ventana;
    }

    // Título, botón y color según de qué se trate (eliminar y cancelar en rojo, confirmar en verde).
    function describirConfirmacion(mensaje) {
        var texto = mensaje.replace(/^\u00bf/, '');
        if (/^eliminar/i.test(texto)) return { titulo: 'Eliminar', boton: 'Eliminar', tono: 'peligro' };
        if (/^(seguro que quer[e\u00e9]s )?cancelar/i.test(texto)) return { titulo: 'Cancelar reserva', boton: 'S\u00ed, cancelar', tono: 'peligro' };
        if (/^confirmar/i.test(texto)) return { titulo: 'Confirmar reserva', boton: 'Confirmar', tono: 'exito' };
        return { titulo: '\u00bfEst\u00e1s seguro?', boton: 'Aceptar', tono: 'normal' };
    }

    function pedirConfirmacion(formulario) {
        if (!ventanaConfirmar) ventanaConfirmar = crearVentanaConfirmar();

        var mensaje = formulario.dataset.confirmar;
        var datos = describirConfirmacion(mensaje);
        var boton = ventanaConfirmar.querySelector('[data-confirmar-si]');

        ventanaConfirmar.dataset.tono = datos.tono;
        ventanaConfirmar.querySelector('h2').textContent = datos.titulo;
        ventanaConfirmar.querySelector('.confirmar-texto').textContent = mensaje; // textContent: nunca interpreta HTML
        boton.textContent = datos.boton;
        boton.className = 'btn ' + (datos.tono === 'peligro' ? 'btn-peligro' : datos.tono === 'exito' ? 'btn-exito' : 'btn-primario');

        formularioPendiente = formulario;
        ventanaConfirmar.showModal();
        ventanaConfirmar.querySelector('[data-confirmar-no]').focus(); // por defecto queda marcado "Volver"
    }

    document.querySelectorAll('form[data-confirmar]').forEach(function (formulario) {
        formulario.addEventListener('submit', function (evento) {
            evento.preventDefault();
            pedirConfirmacion(formulario);
        });
    });

    // Los selectores marcados con data-autoenviar envían su formulario al cambiar.
    document.querySelectorAll('select[data-autoenviar]').forEach(function (selector) {
        selector.addEventListener('change', function () {
            selector.form.submit();
        });
    });

    // Ventanas que deben abrirse solas al cargar la página (por ejemplo, la de fotos después de subir una).
    document.querySelectorAll('dialog[data-abrir-al-cargar]').forEach(function (ventana) {
        ventana.showModal();
    });

    // Selectores de fotos: avisan antes de enviar si hay demasiadas fotos o alguna pesa de más.
    document.querySelectorAll('input[type=file][data-max-mb]').forEach(function (selector) {
        selector.addEventListener('change', function () {
            var maxBytes = Number(selector.dataset.maxMb) * 1024 * 1024;
            var maxArchivos = Number(selector.dataset.maxFiles) || Infinity;
            var archivos = Array.prototype.slice.call(selector.files);
            var problema = '';

            if (archivos.length > maxArchivos) {
                problema = 'Elegiste ' + archivos.length + ' fotos y solo se pueden agregar ' + maxArchivos + '.';
            } else {
                var grande = archivos.filter(function (archivo) { return archivo.size > maxBytes; })[0];
                if (grande) {
                    problema = '"' + grande.name + '" pesa más de ' + selector.dataset.maxMb + ' MB.';
                }
            }

            if (problema) {
                window.alert(problema);
                selector.value = '';
            }
        });
    });

    // Cerrar los avisos.
    document.querySelectorAll('[data-cerrar-aviso]').forEach(function (boton) {
        boton.addEventListener('click', function () {
            var aviso = boton.closest('.aviso');
            if (aviso) aviso.remove();
        });
    });


    const admMenuToggle = document.getElementById("admMenuToggle");
    const admLateral = document.querySelector(".adm-lateral");
    const admMenuFondo = document.getElementById("admMenuFondo");

    function abrirCerrarMenuAdmin() {

        if (!admLateral || !admMenuToggle) {
            return;
        }

        admLateral.classList.toggle("abierto");

        const abierto = admLateral.classList.contains("abierto");

        admMenuToggle.setAttribute("aria-expanded", abierto);

        admMenuToggle.classList.toggle("abierto", abierto);

        if (admMenuFondo) {
            admMenuFondo.classList.toggle("activo", abierto);
        }
    }

    function cerrarMenuAdmin() {

        if (!admLateral || !admMenuToggle) {
            return;
        }

        admLateral.classList.remove("abierto");

        admMenuToggle.classList.remove("abierto");

        admMenuToggle.setAttribute("aria-expanded", "false");

        if (admMenuFondo) {
            admMenuFondo.classList.remove("activo");
        }
    }


    if (admMenuToggle) {
        admMenuToggle.addEventListener("click", abrirCerrarMenuAdmin);
    }


    if (admMenuFondo) {
        admMenuFondo.addEventListener("click", cerrarMenuAdmin);
    }


    document.querySelectorAll(".adm-lateral a").forEach(enlace => {

        enlace.addEventListener("click", () => {

            if (window.innerWidth <= 820) {
                cerrarMenuAdmin();
            }

        });
    });

    const clientesSubmenuBoton =
        document.getElementById("clientesSubmenuBoton");

    if (clientesSubmenuBoton) {

        clientesSubmenuBoton.addEventListener("click", function () {

            const submenu =
                clientesSubmenuBoton.closest(".adm-submenu");

            if (!submenu) {
                return;
            }

            submenu.classList.toggle("abierto");

            const abierto =
                submenu.classList.contains("abierto");

            clientesSubmenuBoton.setAttribute(
                "aria-expanded",
                abierto ? "true" : "false"
            );
        });
    }

})();
