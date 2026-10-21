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

    // Pedir confirmación antes de enviar formularios marcados con data-confirmar.
    document.querySelectorAll('form[data-confirmar]').forEach(function (formulario) {
        formulario.addEventListener('submit', function (evento) {
            if (!window.confirm(formulario.dataset.confirmar)) {
                evento.preventDefault();
            }
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
})();
