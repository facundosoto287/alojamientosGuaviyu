<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión | Alojamientos Guaviyú</title>
    <%@ include file="/WEB-INF/views/components/favicon.jsp" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/loading.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body class="pagina-login">

<%@ include file="components/header.jsp" %>

<main class="auth-hero">
    <section class="auth-intro">
        <p class="eyebrow">Alojamientos Guaviyú</p>
        <h1>Tu descanso<br>te espera en Guaviyú</h1>
        <p class="lead">Cabañas y alojamientos cerca de las Termas de Guaviyú. Naturaleza, bienestar y la calidez de nuestra tierra.</p>
        <ul class="perks">
            <li>
                <span class="perk-icon"><svg viewBox="0 0 24 24"><path d="M3 9c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0M3 14c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0M3 19c2-1.5 4-1.5 6 0s4 1.5 6 0 4-1.5 6 0"/></svg></span>
                <span>A metros<br>de las termas</span>
            </li>
            <li>
                <span class="perk-icon"><svg viewBox="0 0 24 24"><path d="M5 19C5 10 11 4 20 4c0 9-6 15-15 15zm0 0 8-8"/></svg></span>
                <span>Naturaleza<br>todo el año</span>
            </li>
            <li>
                <span class="perk-icon"><svg viewBox="0 0 24 24"><path d="M9 11a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm8 0a2.5 2.5 0 1 0 0-5M3 19c0-3 2.7-5 6-5s6 2 6 5M16 14c2.8 0 5 1.6 5 4"/></svg></span>
                <span>Alojamientos para familias,<br>parejas y grupos</span>
            </li>
        </ul>
    </section>

    <section class="auth-card" data-pestana="${pestana}">
        <div class="auth-tabs" role="tablist">
            <a href="${ctx}/login" role="tab" data-tab="login"
               aria-selected="${pestana == 'login'}" class="${pestana == 'login' ? 'activo' : ''}">Iniciar sesión</a>
            <a href="${ctx}/registro" role="tab" data-tab="registro"
               aria-selected="${pestana == 'registro'}" class="${pestana == 'registro' ? 'activo' : ''}">Registrarse</a>
        </div>

        <div class="auth-columns">
            <%-- ===================== INICIAR SESIÓN ===================== --%>
            <div class="auth-col" data-col="login">
                <h2>¡Bienvenido de nuevo!</h2>
                <p class="muted">Ingresá a tu cuenta para continuar explorando y gestionar tus reservas.</p>

                <c:if test="${not empty loginError}">
                    <p class="alerta" role="alert"><c:out value="${loginError}"/></p>
                </c:if>

                <c:if test="${not empty loginInfo}">
                    <p class="alerta-info">
                        <c:out value="${loginInfo}"/>
                    </p>
                </c:if>

                <form action="${ctx}/login" method="post" novalidate>
                    <input type="hidden" name="destino" value="${fn:escapeXml(destino)}">

                    <label for="login-email">Correo electrónico</label>
                    <div class="input-icon">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 6h18v12H3zM3 6l9 7 9-7"/></svg>
                        <input type="email" id="login-email" name="email" placeholder="tu@correo.com"
                               autocomplete="email" maxlength="100" required value="${fn:escapeXml(loginEmail)}">
                    </div>

                    <label for="login-pass">Contraseña</label>
                    <div class="input-icon">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 11h12v9H6zM8.5 11V8a3.5 3.5 0 0 1 7 0v3"/></svg>
                        <input type="password" id="login-pass" name="contrasena" placeholder="Tu contraseña"
                               autocomplete="current-password" required>
                        <button type="button" class="ver-pass" aria-label="Mostrar contraseña" data-target="login-pass">
                            <svg viewBox="0 0 24 24"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/></svg>
                        </button>
                    </div>

                    <button type="submit" class="btn-primario">Ingresar <span aria-hidden="true">→</span></button>
                </form>
            </div>

            <%-- ===================== REGISTRARSE ===================== --%>
            <div class="auth-col" data-col="registro">
                <h2>Creá tu cuenta</h2>
                <p class="muted">Registrate y empezá a planificar tu descanso en Guaviyú.</p>

                <c:if test="${not empty registroErrores.general}">
                    <p class="alerta" role="alert"><c:out value="${registroErrores.general}"/></p>
                </c:if>

                <form action="${ctx}/registro" method="post" novalidate>
                    <input type="hidden" name="destino" value="${fn:escapeXml(destino)}">

                    <label for="reg-nombre">Nombre completo</label>
                    <div class="input-icon ${not empty registroErrores.nombre ? 'con-error' : ''}">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM4 20c0-3.3 3.6-5.5 8-5.5s8 2.2 8 5.5"/></svg>
                        <input type="text" id="reg-nombre" name="nombre" placeholder="Tu nombre y apellido"
                               autocomplete="name" maxlength="100" required value="${fn:escapeXml(registro.nombre)}">
                    </div>
                    <c:if test="${not empty registroErrores.nombre}"><p class="error-campo"><c:out value="${registroErrores.nombre}"/></p></c:if>

                    <label for="reg-documento">Documento</label>
                    <div class="input-icon ${not empty registroErrores.documento ? 'con-error' : ''}">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 5h18v14H3zM7 10h4M7 14h10M15 9h2"/></svg>
                        <input type="text" id="reg-documento" name="documento" placeholder="Cédula de identidad (ej. 1.234.567-2)"
                               autocomplete="off" inputmode="numeric" pattern="[0-9.\-\s]+" title="Solo números, puntos o guiones"
                               maxlength="20" required value="${fn:escapeXml(registro.documento)}">
                    </div>
                    <c:if test="${not empty registroErrores.documento}"><p class="error-campo"><c:out value="${registroErrores.documento}"/></p></c:if>

                    <label for="reg-telefono">Teléfono</label>
                    <div class="input-icon ${not empty registroErrores.telefono ? 'con-error' : ''}">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 4h4l2 5-2.5 1.5a11 11 0 0 0 5 5L15 13l5 2v4a1 1 0 0 1-1 1A16 16 0 0 1 4 5a1 1 0 0 1 1-1z"/></svg>
                        <input type="tel" id="reg-telefono" name="telefono" placeholder="Ej. 099 123 456"
                               autocomplete="tel" inputmode="tel" pattern="[0-9\s\-]+" title="Celular de 9 dígitos que empieza con 09" maxlength="30" value="${fn:escapeXml(registro.telefono)}">
                    </div>
                    <c:if test="${not empty registroErrores.telefono}"><p class="error-campo"><c:out value="${registroErrores.telefono}"/></p></c:if>

                    <label for="reg-email">Correo electrónico</label>
                    <div class="input-icon ${not empty registroErrores.email ? 'con-error' : ''}">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 6h18v12H3zM3 6l9 7 9-7"/></svg>
                        <input type="email" id="reg-email" name="email" placeholder="tu@correo.com"
                               autocomplete="email" maxlength="100" required value="${fn:escapeXml(registro.email)}">
                    </div>
                    <c:if test="${not empty registroErrores.email}"><p class="error-campo"><c:out value="${registroErrores.email}"/></p></c:if>

                    <label for="reg-procedencia">Procedencia</label>
                    <p class="ayuda-campo">Departamento, provincia o país donde vivís.</p>
                    <div class="input-icon ${not empty registroErrores.procedencia ? 'con-error' : ''}">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21s-7-6.2-7-11.5a7 7 0 0 1 14 0C19 14.8 12 21 12 21z"/><circle cx="12" cy="9.5" r="2.5"/></svg>
                        <select id="reg-procedencia" name="procedencia" required>
                            <option value="">Seleccioná tu procedencia</option>
                            <c:forEach var="opcion" items="${procedencias}">
                                <option value="${fn:escapeXml(opcion)}" ${registro.procedencia == opcion ? 'selected' : ''}><c:out value="${opcion}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <c:if test="${not empty registroErrores.procedencia}"><p class="error-campo"><c:out value="${registroErrores.procedencia}"/></p></c:if>

                    <label for="reg-localidad">Localidad</label>
                    <p class="ayuda-campo">Ciudad o pueblo donde vivís (por ejemplo: Paysandú, Salto o Guichón).</p>
                    <div class="input-icon ${not empty registroErrores.localidad ? 'con-error' : ''}">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 11l9-7 9 7M5 10v10h5v-6h4v6h5V10"/></svg>
                        <input type="text" id="reg-localidad" name="localidad" list="lista-localidades" placeholder="Tu ciudad o pueblo"
                               autocomplete="address-level2" maxlength="100" required value="${fn:escapeXml(registro.localidad)}">
                    </div>
                    <datalist id="lista-localidades">
                        <c:forEach var="sugerida" items="${localidades}"><option value="${fn:escapeXml(sugerida)}"></option></c:forEach>
                    </datalist>
                    <c:if test="${not empty registroErrores.localidad}"><p class="error-campo"><c:out value="${registroErrores.localidad}"/></p></c:if>

                    <label for="reg-pass">Contraseña</label>
                    <div class="input-icon ${not empty registroErrores.contrasena ? 'con-error' : ''}">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 11h12v9H6zM8.5 11V8a3.5 3.5 0 0 1 7 0v3"/></svg>
                        <input type="password" id="reg-pass" name="contrasena" placeholder="Mínimo 8 caracteres"
                               autocomplete="new-password" minlength="8" required>
                        <button type="button" class="ver-pass" aria-label="Mostrar contraseña" data-target="reg-pass">
                            <svg viewBox="0 0 24 24"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/></svg>
                        </button>
                    </div>
                    <c:if test="${not empty registroErrores.contrasena}"><p class="error-campo"><c:out value="${registroErrores.contrasena}"/></p></c:if>

                    <label for="reg-confirmacion">Confirmar contraseña</label>
                    <div class="input-icon ${not empty registroErrores.confirmacion ? 'con-error' : ''}">
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 11h12v9H6zM8.5 11V8a3.5 3.5 0 0 1 7 0v3"/></svg>
                        <input type="password" id="reg-confirmacion" name="confirmacion" placeholder="Repetí tu contraseña"
                               autocomplete="new-password" required>
                        <button type="button" class="ver-pass" aria-label="Mostrar contraseña" data-target="reg-confirmacion">
                            <svg viewBox="0 0 24 24"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/></svg>
                        </button>
                    </div>
                    <c:if test="${not empty registroErrores.confirmacion}"><p class="error-campo"><c:out value="${registroErrores.confirmacion}"/></p></c:if>

                    <button type="submit" class="btn-primario">Crear cuenta <span aria-hidden="true">→</span></button>
                </form>
            </div>
        </div>

        <div class="auth-banner">
            <span class="banner-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="M9 11a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm8 0a2.5 2.5 0 1 0 0-5M3 19c0-3 2.7-5 6-5s6 2 6 5M16 14c2.8 0 5 1.6 5 4"/></svg></span>
            <div>
                <strong>Reservá en simples pasos</strong>
                <p>Accedé a tus reservas, gestioná tus solicitudes y viví la experiencia Guaviyú.</p>
            </div>
        </div>
    </section>
</main>

<script>
    // Mostrar / ocultar contraseña
    document.querySelectorAll('.ver-pass').forEach(function (boton) {
        boton.addEventListener('click', function () {
            var campo = document.getElementById(boton.dataset.target);
            var mostrar = campo.type === 'password';
            campo.type = mostrar ? 'text' : 'password';
            boton.setAttribute('aria-label', mostrar ? 'Ocultar contraseña' : 'Mostrar contraseña');
            boton.classList.toggle('visible', mostrar);
        });
    });

    // Pestañas sin recargar la página (en celular muestran una columna; en pantalla grande se ven las dos)
    var tarjeta = document.querySelector('.auth-card');
    document.querySelectorAll('.auth-tabs a').forEach(function (pestana) {
        pestana.addEventListener('click', function (evento) {
            evento.preventDefault();
            var nombre = pestana.dataset.tab;
            tarjeta.dataset.pestana = nombre;
            document.querySelectorAll('.auth-tabs a').forEach(function (otra) {
                var activa = otra === pestana;
                otra.classList.toggle('activo', activa);
                otra.setAttribute('aria-selected', activa);
            });
            history.replaceState(null, '', pestana.href);
            var primerCampo = document.querySelector('[data-col="' + nombre + '"] input:not([type=hidden])');
            if (primerCampo) primerCampo.focus();
        });
    });
</script>
<%@ include file="/WEB-INF/views/components/loading.jsp" %>
<script src="${pageContext.request.contextPath}/js/loading.js"></script>
<%@ include file="components/footer.jsp" %>
</body>
</html>
