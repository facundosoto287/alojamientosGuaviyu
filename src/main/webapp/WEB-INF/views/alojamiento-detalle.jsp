<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Detalle del alojamiento</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/header.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/alojamiento-detalle.css">
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
</body>
</html>