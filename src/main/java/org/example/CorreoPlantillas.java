package org.example;

import java.time.format.DateTimeFormatter;

public class CorreoPlantillas {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    //El cliente recibe en su mail que su solicitud quedo en estado APROBADA
    public static String reservaConfirmada(Cliente cliente, ReservaDetalle detalle) {

        String fechaIngreso = detalle.getFechaIngreso().toLocalDate().format(FORMATO_FECHA);

        String fechaSalida = detalle.getFechaSalida().toLocalDate().format(FORMATO_FECHA);

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>

                <body style="
                    margin:0;
                    padding:0;
                    background:#f4f6f8;
                    font-family:Arial, sans-serif;
                    color:#2b2b2b;
                ">

                    <div style="
                        max-width:600px;
                        margin:30px auto;
                        background:#ffffff;
                        border-radius:14px;
                        overflow:hidden;
                        box-shadow:0 4px 18px rgba(0,0,0,0.08);
                    ">

                        <!-- ENCABEZADO -->
                        <div style="
                            background:#1f5fae;
                            padding:28px;
                            text-align:center;
                            color:white;
                        ">

                            <img
                                src="cid:logoGuaviyu"
                                alt="Alojamientos Guaviyú"
                                style="
                                    width:105px;
                                    height:auto;
                                    margin-bottom:12px;
                                "
                            >

                            <h1 style="
                                margin:0;
                                font-size:25px;
                            ">
                                Alojamientos Guaviyú
                            </h1>

                        </div>


                        <!-- CONTENIDO -->
                        <div style="
                            padding:32px;
                            text-align:center;
                        ">

                            <div style="
                                width:52px;
                                height:52px;
                                line-height:52px;
                                margin:0 auto 18px;
                                border-radius:50%%;
                                background:#eaf2fb;
                                color:#1f5fae;
                                font-size:28px;
                                font-weight:bold;
                            ">
                                ✓
                            </div>

                            <h2 style="
                                margin:0 0 12px;
                                font-size:24px;
                                color:#1f5fae;
                            ">
                                ¡Tu reserva fue confirmada!
                            </h2>

                            <p style="
                                margin:0;
                                color:#666;
                                font-size:15px;
                                line-height:1.6;
                            ">
                                Hola %s, tu reserva fue confirmada correctamente.
                                A continuación podés ver los datos de tu estadía.
                            </p>


                            <!-- DATOS -->
                            <div style="
                                text-align:left;
                                background:#f7f9f8;
                                border-radius:10px;
                                padding:22px;
                                margin:28px 0;
                            ">

                                <p style="margin:9px 0;">
                                    <strong>Reserva:</strong> #%d
                                </p>

                                <p style="margin:9px 0;">
                                    <strong>Alojamiento:</strong> %s
                                </p>

                                <p style="margin:9px 0;">
                                    <strong>Ingreso:</strong> %s
                                </p>

                                <p style="margin:9px 0;">
                                    <strong>Salida:</strong> %s
                                </p>

                                <p style="margin:9px 0;">
                                    <strong>Huéspedes:</strong> %d
                                </p>

                            </div>


                            <h3 style="
                                color:#1f5fae;
                                margin-bottom:10px;
                            ">
                                Te esperamos para tu estadía
                            </h3>

                            <p style="
                                margin:0;
                                color:#666;
                                font-size:14px;
                                line-height:1.6;
                            ">
                                Si necesitás realizar alguna consulta,
                                podés comunicarte con la administración
                                de Alojamientos Guaviyú.
                            </p>

                        </div>


                        <!-- PIE -->
                        <div style="
                            background:#eef2f0;
                            padding:17px;
                            text-align:center;
                            font-size:12px;
                            color:#777;
                        ">
                            Alojamientos Guaviyú
                        </div>

                    </div>

                </body>
                </html>
                """.formatted(cliente.getNombre(), detalle.getId(), detalle.getAlojamientoNombre(), fechaIngreso, fechaSalida, detalle.getCantidadHuespedes());
    }

    //El cliente recibe en su mail que su solicitud quedo en estado CANCELADA
    public static String reservaCancelada(Cliente cliente, ReservaDetalle detalle) {

        String fechaIngreso = detalle.getFechaIngreso().toLocalDate().format(FORMATO_FECHA);

        String fechaSalida = detalle.getFechaSalida().toLocalDate().format(FORMATO_FECHA);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>

            <body style="
                margin:0;
                padding:0;
                background:#f4f6f8;
                font-family:Arial, sans-serif;
                color:#2b2b2b;
            ">

                <div style="
                    max-width:600px;
                    margin:30px auto;
                    background:#ffffff;
                    border-radius:14px;
                    overflow:hidden;
                    box-shadow:0 4px 18px rgba(0,0,0,0.08);
                ">

                    <!-- ENCABEZADO -->
                    <div style="
                        background:#b84040;
                        padding:28px;
                        text-align:center;
                        color:white;
                    ">

                        <img
                            src="cid:logoGuaviyu"
                            alt="Alojamientos Guaviyú"
                            style="
                                width:105px;
                                height:auto;
                                margin-bottom:12px;
                            "
                        >

                        <h1 style="
                            margin:0;
                            font-size:25px;
                        ">
                            Alojamientos Guaviyú
                        </h1>

                    </div>


                    <!-- CONTENIDO -->
                    <div style="
                        padding:32px;
                        text-align:center;
                    ">

                        <div style="
                            width:52px;
                            height:52px;
                            line-height:52px;
                            margin:0 auto 18px;
                            border-radius:50%%;
                            background:#fbeaea;
                            color:#b84040;
                            font-size:26px;
                            font-weight:bold;
                        ">
                            ✕
                        </div>

                        <h2 style="
                            margin:0 0 12px;
                            font-size:24px;
                            color:#b84040;
                        ">
                            Tu reserva fue cancelada
                        </h2>

                        <p style="
                            margin:0;
                            color:#666;
                            font-size:15px;
                            line-height:1.6;
                        ">
                            Hola %s, te informamos que tu reserva
                            en Alojamientos Guaviyú fue cancelada.
                        </p>


                        <!-- DATOS -->
                        <div style="
                            text-align:left;
                            background:#f7f9f8;
                            border-radius:10px;
                            padding:22px;
                            margin:28px 0;
                        ">

                            <p style="margin:9px 0;">
                                <strong>Reserva:</strong> #%d
                            </p>

                            <p style="margin:9px 0;">
                                <strong>Alojamiento:</strong> %s
                            </p>

                            <p style="margin:9px 0;">
                                <strong>Ingreso:</strong> %s
                            </p>

                            <p style="margin:9px 0;">
                                <strong>Salida:</strong> %s
                            </p>

                            <p style="margin:9px 0;">
                                <strong>Huéspedes:</strong> %d
                            </p>

                        </div>


                        <h3 style="
                            color:#1f5fae;
                            margin-bottom:10px;
                        ">
                            ¿Necesitás realizar otra reserva?
                        </h3>

                        <p style="
                            margin:0;
                            color:#666;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            Podés consultar nuevamente la disponibilidad
                            de nuestros alojamientos o comunicarte con
                            la administración ante cualquier consulta.
                        </p>

                    </div>


                    <!-- PIE -->
                    <div style="
                        background:#eef2f0;
                        padding:17px;
                        text-align:center;
                        font-size:12px;
                        color:#777;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

            </body>
            </html>
            """.formatted(cliente.getNombre(), detalle.getId(), detalle.getAlojamientoNombre(), fechaIngreso, fechaSalida, detalle.getCantidadHuespedes());
    }

    //El cliente recibe en su mail que su solicitud quedo en estado PENDIENTE
    public static String solicitudRecibida(Cliente cliente, Alojamiento alojamiento, int idReserva, java.time.LocalDate fechaIngreso, java.time.LocalDate fechaSalida, int cantidadHuespedes) {

        String ingreso = fechaIngreso.format(FORMATO_FECHA);

        String salida = fechaSalida.format(FORMATO_FECHA);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>

            <body style="
                margin:0;
                padding:0;
                background:#f4f6f8;
                font-family:Arial, sans-serif;
                color:#2b2b2b;
            ">

                <div style="
                    max-width:600px;
                    margin:30px auto;
                    background:#ffffff;
                    border-radius:14px;
                    overflow:hidden;
                    box-shadow:0 4px 18px rgba(0,0,0,0.08);
                ">

                    <div style="
                        background:#1f5fae;
                        padding:28px;
                        text-align:center;
                        color:white;
                    ">

                        <img
                            src="cid:logoGuaviyu"
                            alt="Alojamientos Guaviyú"
                            style="
                                width:105px;
                                height:auto;
                                margin-bottom:12px;
                            "
                        >

                        <h1 style="
                            margin:0;
                            font-size:25px;
                        ">
                            Alojamientos Guaviyú
                        </h1>

                    </div>


                    <div style="
                        padding:32px;
                        text-align:center;
                    ">

                        <div style="
                            width:52px;
                            height:52px;
                            line-height:52px;
                            margin:0 auto 18px;
                            border-radius:50%%;
                            background:#eaf2fb;
                            color:#1f5fae;
                            font-size:26px;
                            font-weight:bold;
                        ">
                            ✓
                        </div>

                        <h2 style="
                            margin:0 0 12px;
                            font-size:24px;
                            color:#1f5fae;
                        ">
                            Recibimos tu solicitud
                        </h2>

                        <p style="
                            margin:0;
                            color:#666;
                            font-size:15px;
                            line-height:1.6;
                        ">
                            Hola %s, recibimos correctamente tu solicitud
                            de reserva.
                        </p>

                        <p style="
                            color:#666;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            La solicitud se encuentra pendiente de confirmación
                            por parte de la administración.
                        </p>


                        <div style="
                            text-align:left;
                            background:#f7f9f8;
                            border-radius:10px;
                            padding:22px;
                            margin:28px 0;
                        ">

                            <p style="margin:9px 0;">
                                <strong>Solicitud:</strong> #%d
                            </p>

                            <p style="margin:9px 0;">
                                <strong>Alojamiento:</strong> %s
                            </p>

                            <p style="margin:9px 0;">
                                <strong>Ingreso:</strong> %s
                            </p>

                            <p style="margin:9px 0;">
                                <strong>Salida:</strong> %s
                            </p>

                            <p style="margin:9px 0;">
                                <strong>Huéspedes:</strong> %d
                            </p>

                            <p style="margin:9px 0;">
                                <strong>Estado:</strong> Pendiente
                            </p>

                        </div>


                        <h3 style="
                            color:#1f5fae;
                            margin-bottom:10px;
                        ">
                            ¿Qué pasa ahora?
                        </h3>

                        <p style="
                            margin:0;
                            color:#666;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            La administración revisará tu solicitud.
                            Cuando sea confirmada o cancelada,
                            recibirás un nuevo correo.
                        </p>

                    </div>


                    <div style="
                        background:#eef2f0;
                        padding:17px;
                        text-align:center;
                        font-size:12px;
                        color:#777;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

            </body>
            </html>
            """.formatted(cliente.getNombre(), idReserva, alojamiento.getNombre(), ingreso, salida, cantidadHuespedes);
    }

}