<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Detalle del alojamiento</title>

    <!-- Fuentes utilizadas en toda la web -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

    <link href="https://fonts.googleapis.com/css2?family=Libre+Baskerville:wght@400;700&family=Poppins:wght@400;600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">


    <!-- CSS propio de la pantalla -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/alojamiento-detalle.css">


    <!-- CSS comunes de toda la web -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">
</head>
<body>

<%@ include file="components/header.jsp" %>

<main class="detalle">

    <h1>${alojamiento.nombre}</h1>
    <p>${alojamiento.descripcion}</p>

    <div class="detalle-datos">
        <p><strong>Capacidad:</strong> ${alojamiento.capacidad} personas</p>
        <p><strong>Precio:</strong> $${alojamiento.precio}</p>
        <p><strong>Ubicación:</strong> ${alojamiento.ubicacion}</p>
        <p>
            <strong>Distancia a piscina:</strong>
            ${alojamiento.distanciaApiscina}
        </p>
    </div>

</main>
<%@ include file="components/footer.jsp" %>
</body>
</html>