package org.example;

import java.time.format.DateTimeFormatter;

public class CorreoPlantillas {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // El cliente recibe en su mail que su solicitud quedó en estado CONFIRMADA
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
                background:#f3f5f7;
                font-family:'Segoe UI', Arial, sans-serif;
                color:#333333;
            ">

                <div style="
                    max-width:600px;
                    margin:32px auto;
                    background:#ffffff;
                    border-radius:16px;
                    overflow:hidden;
                    box-shadow:0 6px 20px rgba(0,0,0,0.08);
                ">

                    <div style="
                        background:#1f5fae;
                        padding:24px 28px;
                        text-align:center;
                        color:white;
                    ">

                        <img
                            src="cid:logoGuaviyu"
                            alt="Alojamientos Guaviyú"
                            style="
                                width:95px;
                                height:auto;
                                margin-bottom:10px;
                            "
                        >

                        <div style="
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:20px;
                            font-weight:700;
                            letter-spacing:0.2px;
                        ">
                            Alojamientos Guaviyú
                        </div>

                    </div>

                    <div style="
                        padding:34px 34px 30px;
                        text-align:center;
                    ">

                        <div style="
                            width:54px;
                            height:54px;
                            line-height:54px;
                            margin:0 auto 20px;
                            border-radius:50%%;
                            background:#eaf2fb;
                            color:#1f5fae;
                            font-size:27px;
                            font-weight:bold;
                        ">
                            ✓
                        </div>

                        <h2 style="
                            margin:0 0 12px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:26px;
                            font-weight:700;
                        ">
                            ¡Tu reserva fue confirmada!
                        </h2>

                        <p style="
                            margin:0;
                            color:#68717c;
                            font-size:15px;
                            line-height:1.7;
                        ">
                            Hola %s, tu reserva ya fue confirmada.
                            Te dejamos los datos de tu estadía.
                        </p>

                        <div style="
                            text-align:left;
                            background:#f8fafc;
                            border:1px solid #e5e9ee;
                            border-radius:12px;
                            padding:22px 24px;
                            margin:28px 0;
                        ">

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Alojamiento:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Ingreso:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Salida:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Huéspedes:</strong>
                                %s
                            </p>

                            <p style="margin:0; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Estado:</strong>

                                <span style="
                                    display:inline-block;
                                    margin-left:6px;
                                    padding:4px 10px;
                                    background:#e8f5eb;
                                    color:#2f7a3f;
                                    border-radius:20px;
                                    font-size:12px;
                                    font-weight:700;
                                ">
                                    Confirmada
                                </span>
                            </p>

                        </div>

                        <h3 style="
                            margin:0 0 8px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:18px;
                        ">
                            Te esperamos para tu estadía
                        </h3>

                        <p style="
                            margin:0;
                            color:#7a838d;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            Si tenés alguna consulta sobre tu reserva,
                            podés comunicarte con la administración
                            de Alojamientos Guaviyú.
                        </p>

                    </div>

                    <div style="
                        background:#f1f4f6;
                        padding:18px;
                        text-align:center;
                        font-size:12px;
                        color:#7c858e;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

            </body>
            </html>
            """.formatted(cliente.getNombre(), detalle.getAlojamientoNombre(), fechaIngreso, fechaSalida, detalle.getCantidadHuespedes());
    }


    // El cliente recibe en su mail que su solicitud quedó en estado CANCELADA
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
                background:#f3f5f7;
                font-family:'Segoe UI', Arial, sans-serif;
                color:#333333;
            ">

                <div style="
                    max-width:600px;
                    margin:32px auto;
                    background:#ffffff;
                    border-radius:16px;
                    overflow:hidden;
                    box-shadow:0 6px 20px rgba(0,0,0,0.08);
                ">

                    <div style="
                        background:#1f5fae;
                        padding:24px 28px;
                        text-align:center;
                        color:white;
                    ">

                        <img
                            src="cid:logoGuaviyu"
                            alt="Alojamientos Guaviyú"
                            style="
                                width:95px;
                                height:auto;
                                margin-bottom:10px;
                            "
                        >

                        <div style="
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:20px;
                            font-weight:700;
                            letter-spacing:0.2px;
                        ">
                            Alojamientos Guaviyú
                        </div>

                    </div>

                    <div style="
                        padding:34px 34px 30px;
                        text-align:center;
                    ">

                        <div style="
                            width:54px;
                            height:54px;
                            line-height:54px;
                            margin:0 auto 20px;
                            border-radius:50%%;
                            background:#fbeaea;
                            color:#b84040;
                            font-size:27px;
                            font-weight:bold;
                        ">
                            ✕
                        </div>

                        <h2 style="
                            margin:0 0 12px;
                            color:#b84040;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:26px;
                            font-weight:700;
                        ">
                            Tu reserva fue cancelada
                        </h2>

                        <p style="
                            margin:0;
                            color:#68717c;
                            font-size:15px;
                            line-height:1.7;
                        ">
                            Hola %s, te informamos que tu reserva
                            en Alojamientos Guaviyú fue cancelada.
                        </p>

                        <div style="
                            text-align:left;
                            background:#f8fafc;
                            border:1px solid #e5e9ee;
                            border-radius:12px;
                            padding:22px 24px;
                            margin:28px 0;
                        ">

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Alojamiento:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Ingreso:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Salida:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Huéspedes:</strong>
                                %d
                            </p>

                            <p style="margin:0; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Estado:</strong>

                                <span style="
                                    display:inline-block;
                                    margin-left:6px;
                                    padding:4px 10px;
                                    background:#fbeaea;
                                    color:#a63d3d;
                                    border-radius:20px;
                                    font-size:12px;
                                    font-weight:700;
                                ">
                                    Cancelada
                                </span>
                            </p>

                        </div>
                        
                        <div style="
                                text-align:left;
                                background:#fff8e8;
                                border:1px solid #f0dfb3;
                                border-radius:12px;
                                padding:18px 20px;
                                margin:0 0 28px;
                            ">
                
                                <p style="
                                    margin:0 0 8px;
                                    font-size:14px;
                                    font-weight:700;
                                    color:#8a6518;
                                ">
                                    Sobre tu seña
                                </p>
                
                                <p style="
                                    margin:0;
                                    font-size:14px;
                                    line-height:1.6;
                                    color:#6f6756;
                                ">
                                    La seña realizada permanecerá válida durante 6 meses
                                    a partir de la fecha de ingreso de esta reserva.
                                    Durante ese período podrás utilizarla para realizar
                                    una nueva reserva en cualquiera de nuestros alojamientos.
                                </p>
                
                            </div>

                        <h3 style="
                            margin:0 0 8px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:18px;
                        ">
                            ¿Necesitás realizar otra reserva?
                        </h3>

                        <p style="
                            margin:0;
                            color:#7a838d;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            Podés consultar nuevamente la disponibilidad
                            de nuestros alojamientos o comunicarte con
                            la administración ante cualquier consulta.
                        </p>

                    </div>

                    <div style="
                        background:#f1f4f6;
                        padding:18px;
                        text-align:center;
                        font-size:12px;
                        color:#7c858e;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

            </body>
            </html>
            """.formatted(cliente.getNombre(), detalle.getAlojamientoNombre(), fechaIngreso, fechaSalida, detalle.getCantidadHuespedes());
    }


    // El cliente recibe en su mail que su solicitud quedó en estado PENDIENTE
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
                background:#f3f5f7;
                font-family:'Segoe UI', Arial, sans-serif;
                color:#333333;
            ">

                <div style="
                    max-width:600px;
                    margin:32px auto;
                    background:#ffffff;
                    border-radius:16px;
                    overflow:hidden;
                    box-shadow:0 6px 20px rgba(0,0,0,0.08);
                ">

                    <div style="
                        background:#1f5fae;
                        padding:24px 28px;
                        text-align:center;
                        color:white;
                    ">

                        <img
                            src="cid:logoGuaviyu"
                            alt="Alojamientos Guaviyú"
                            style="
                                width:95px;
                                height:auto;
                                margin-bottom:10px;
                            "
                        >

                        <div style="
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:20px;
                            font-weight:700;
                            letter-spacing:0.2px;
                        ">
                            Alojamientos Guaviyú
                        </div>

                    </div>

                    <div style="
                        padding:34px 34px 30px;
                        text-align:center;
                    ">

                        <div style="
                            width:54px;
                            height:54px;
                            line-height:54px;
                            margin:0 auto 20px;
                            border-radius:50%%;
                            background:#fff4dd;
                            color:#b7791f;
                            font-size:27px;
                            font-weight:bold;
                        ">
                            !
                        </div>

                        <h2 style="
                            margin:0 0 12px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:26px;
                            font-weight:700;
                        ">
                            Recibimos tu solicitud
                        </h2>

                        <p style="
                            margin:0;
                            color:#68717c;
                            font-size:15px;
                            line-height:1.7;
                        ">
                            Hola %s, recibimos correctamente tu solicitud de reserva.
                            La administración la revisará antes de confirmarla.
                        </p>

                        <div style="
                            text-align:left;
                            background:#f8fafc;
                            border:1px solid #e5e9ee;
                            border-radius:12px;
                            padding:22px 24px;
                            margin:28px 0;
                        ">

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Alojamiento:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Ingreso:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Salida:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Huéspedes:</strong>
                                %d
                            </p>

                            <p style="margin:0; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Estado:</strong>

                                <span style="
                                    display:inline-block;
                                    margin-left:6px;
                                    padding:4px 10px;
                                    background:#fff4dd;
                                    color:#9a681d;
                                    border-radius:20px;
                                    font-size:12px;
                                    font-weight:700;
                                ">
                                    Pendiente
                                </span>
                            </p>

                        </div>

                        <h3 style="
                            margin:0 0 8px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:18px;
                        ">
                            ¿Qué pasa ahora?
                        </h3>

                        <p style="
                            margin:0;
                            color:#7a838d;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            La administración revisará tu solicitud.
                            Cuando sea confirmada o cancelada,
                            recibirás un nuevo correo.
                        </p>

                    </div>

                    <div style="
                        background:#f1f4f6;
                        padding:18px;
                        text-align:center;
                        font-size:12px;
                        color:#7c858e;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

            </body>
            </html>
            """.formatted(cliente.getNombre(), alojamiento.getNombre(), ingreso, salida, cantidadHuespedes);
    }


    // Si el admin modifica los datos de la reserva, el cliente recibe un correo
    public static String reservaModificada(Cliente cliente, ReservaDetalle detalle) {

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
                background:#f3f5f7;
                font-family:'Segoe UI', Arial, sans-serif;
                color:#333333;
            ">

                <div style="
                    max-width:600px;
                    margin:32px auto;
                    background:#ffffff;
                    border-radius:16px;
                    overflow:hidden;
                    box-shadow:0 6px 20px rgba(0,0,0,0.08);
                ">

                    <div style="
                        background:#1f5fae;
                        padding:24px 28px;
                        text-align:center;
                        color:white;
                    ">

                        <img
                            src="cid:logoGuaviyu"
                            alt="Alojamientos Guaviyú"
                            style="
                                width:95px;
                                height:auto;
                                margin-bottom:10px;
                            "
                        >

                        <div style="
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:20px;
                            font-weight:700;
                            letter-spacing:0.2px;
                        ">
                            Alojamientos Guaviyú
                        </div>

                    </div>

                    <div style="
                        padding:34px 34px 30px;
                        text-align:center;
                    ">

                        <div style="
                            width:54px;
                            height:54px;
                            line-height:54px;
                            margin:0 auto 20px;
                            border-radius:50%%;
                            background:#eaf2fb;
                            color:#1f5fae;
                            font-size:27px;
                            font-weight:bold;
                        ">
                            ✎
                        </div>

                        <h2 style="
                            margin:0 0 12px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:26px;
                            font-weight:700;
                        ">
                            Tu reserva fue modificada
                        </h2>

                        <p style="
                            margin:0;
                            color:#68717c;
                            font-size:15px;
                            line-height:1.7;
                        ">
                            Hola %s, se realizaron cambios en los datos de tu reserva.
                            Estos son los datos actualizados de tu estadía.
                        </p>

                        <div style="
                            text-align:left;
                            background:#f8fafc;
                            border:1px solid #e5e9ee;
                            border-radius:12px;
                            padding:22px 24px;
                            margin:28px 0;
                        ">

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Alojamiento:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Ingreso:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Salida:</strong>
                                %s
                            </p>

                            <p style="margin:0; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Huéspedes:</strong>
                                %d
                            </p>

                        </div>

                        <h3 style="
                            margin:0 0 8px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:18px;
                        ">
                            Revisá los datos actualizados
                        </h3>

                        <p style="
                            margin:0;
                            color:#7a838d;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            Si tenés alguna consulta sobre estos cambios,
                            podés comunicarte con la administración
                            de Alojamientos Guaviyú.
                        </p>

                    </div>

                    <div style="
                        background:#f1f4f6;
                        padding:18px;
                        text-align:center;
                        font-size:12px;
                        color:#7c858e;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

            </body>
            </html>
            """.formatted(cliente.getNombre(), detalle.getAlojamientoNombre(), fechaIngreso, fechaSalida, detalle.getCantidadHuespedes());
    }


    // Cuando el cliente se registra le llega un correo de bienvenida
    public static String bienvenidaCliente(Cliente cliente) {

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>

            <body style="
                margin:0;
                padding:0;
                background:#f3f5f7;
                font-family:'Segoe UI', Arial, sans-serif;
                color:#333333;
            ">

                <div style="
                    max-width:600px;
                    margin:32px auto;
                    background:#ffffff;
                    border-radius:16px;
                    overflow:hidden;
                    box-shadow:0 6px 20px rgba(0,0,0,0.08);
                ">

                    <div style="
                        background:#1f5fae;
                        padding:24px 28px;
                        text-align:center;
                        color:white;
                    ">

                        <img
                            src="cid:logoGuaviyu"
                            alt="Alojamientos Guaviyú"
                            style="
                                width:95px;
                                height:auto;
                                margin-bottom:10px;
                            "
                        >

                        <div style="
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:20px;
                            font-weight:700;
                            letter-spacing:0.2px;
                        ">
                            Alojamientos Guaviyú
                        </div>

                    </div>

                    <div style="
                        padding:34px 34px 30px;
                        text-align:center;
                    ">

                        <div style="
                            width:54px;
                            height:54px;
                            line-height:54px;
                            margin:0 auto 20px;
                            border-radius:50%%;
                            background:#eaf2fb;
                            color:#1f5fae;
                            font-size:27px;
                            font-weight:bold;
                        ">
                            ✓
                        </div>

                        <h2 style="
                            margin:0 0 12px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:26px;
                            font-weight:700;
                        ">
                            ¡Bienvenido a Alojamientos Guaviyú!
                        </h2>

                        <p style="
                            margin:0;
                            color:#68717c;
                            font-size:15px;
                            line-height:1.7;
                        ">
                            Hola %s, tu cuenta fue creada correctamente.
                            Ya podés comenzar a utilizar Alojamientos Guaviyú.
                        </p>

                        <div style="
                            text-align:left;
                            background:#f8fafc;
                            border:1px solid #e5e9ee;
                            border-radius:12px;
                            padding:22px 24px;
                            margin:28px 0;
                        ">

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Nombre:</strong>
                                %s
                            </p>

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Correo:</strong>
                                %s
                            </p>

                            <p style="margin:0; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Procedencia:</strong>
                                %s
                            </p>

                        </div>

                        <h3 style="
                            margin:0 0 8px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:18px;
                        ">
                            Tu próxima estadía empieza acá
                        </h3>

                        <p style="
                            margin:0;
                            color:#7a838d;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            Podés consultar nuestros alojamientos,
                            verificar disponibilidad y realizar
                            solicitudes de reserva.
                        </p>

                    </div>

                    <div style="
                        background:#f1f4f6;
                        padding:18px;
                        text-align:center;
                        font-size:12px;
                        color:#7c858e;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

            </body>
            </html>
            """.formatted(cliente.getNombre(), cliente.getNombre(), cliente.getEmail(), cliente.getProcedencia());
    }


    // Si el usuario olvida su contraseña se le envía un correo de recuperación
    public static String recuperarContrasena(Cliente cliente, String enlace) {

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>

            <body style="
                margin:0;
                padding:0;
                background:#f3f5f7;
                font-family:'Segoe UI', Arial, sans-serif;
                color:#333333;
            ">

                <div style="
                    max-width:600px;
                    margin:32px auto;
                    background:#ffffff;
                    border-radius:16px;
                    overflow:hidden;
                    box-shadow:0 6px 20px rgba(0,0,0,0.08);
                ">

                    <div style="
                        background:#1f5fae;
                        padding:24px 28px;
                        text-align:center;
                        color:white;
                    ">

                        <img
                            src="cid:logoGuaviyu"
                            alt="Alojamientos Guaviyú"
                            style="
                                width:95px;
                                height:auto;
                                margin-bottom:10px;
                            "
                        >

                        <div style="
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:20px;
                            font-weight:700;
                            letter-spacing:0.2px;
                        ">
                            Alojamientos Guaviyú
                        </div>

                    </div>

                    <div style="
                        padding:34px 34px 30px;
                        text-align:center;
                    ">

                    <div style="
                        width:54px;
                        height:54px;
                        margin:0 auto 20px;
                        border-radius:50%%;
                        background:#eaf2fb;
                        position:relative;
                    ">
        
                        <div style="
                            position:absolute;
                            width:18px;
                            height:14px;
                            border:3px solid #1f5fae;
                            border-bottom:0;
                            border-radius:10px 10px 0 0;
                            top:10px;
                            left:15px;
                        "></div>
        
                        <div style="
                            position:absolute;
                            width:24px;
                            height:20px;
                            background:#1f5fae;
                            border-radius:4px;
                            top:24px;
                            left:18px;
                        "></div>
        
                        <div style="
                            position:absolute;
                            width:4px;
                            height:7px;
                            background:#eaf2fb;
                            border-radius:2px;
                            top:30px;
                            left:25px;
                        "></div>
            
                        </div>

                        <h2 style="
                            margin:0 0 12px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:26px;
                            font-weight:700;
                        ">
                            Restablecer contraseña
                        </h2>

                        <p style="
                            margin:0;
                            color:#68717c;
                            font-size:15px;
                            line-height:1.7;
                        ">
                            Hola %s, recibimos una solicitud para
                            restablecer la contraseña de tu cuenta.
                        </p>

                        <div style="
                            text-align:left;
                            background:#f8fafc;
                            border:1px solid #e5e9ee;
                            border-radius:12px;
                            padding:22px 24px;
                            margin:28px 0;
                        ">

                            <p style="
                                margin:0;
                                font-size:14px;
                                color:#5f6873;
                                line-height:1.6;
                            ">
                                Para elegir una nueva contraseña,
                                utilizá el botón que aparece a continuación.
                                El enlace tendrá una validez de 30 minutos.
                            </p>

                        </div>

                        <div style="margin:0 0 28px;">

                            <a href="%s"
                               style="
                                   display:inline-block;
                                   background:#1f5fae;
                                   color:#ffffff;
                                   text-decoration:none;
                                   padding:13px 24px;
                                   border-radius:8px;
                                   font-family:'Segoe UI',Arial,sans-serif;
                                   font-size:14px;
                                   font-weight:700;
                               ">
                                Restablecer contraseña
                            </a>

                        </div>

                        <h3 style="
                            margin:0 0 8px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:18px;
                        ">
                            ¿No solicitaste este cambio?
                        </h3>

                        <p style="
                            margin:0;
                            color:#7a838d;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            Podés ignorar este correo.
                            Tu contraseña actual seguirá siendo válida.
                        </p>

                    </div>

                    <div style="
                        background:#f1f4f6;
                        padding:18px;
                        text-align:center;
                        font-size:12px;
                        color:#7c858e;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

            </body>
            </html>
            """.formatted(cliente.getNombre(), enlace);
    }


    // Lleva al usuario para actualizar su contraseña
    public static String contrasenaActualizada(Cliente cliente) {

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>

            <body style="
                margin:0;
                padding:0;
                background:#f3f5f7;
                font-family:'Segoe UI', Arial, sans-serif;
                color:#333333;
            ">

                <div style="
                    max-width:600px;
                    margin:32px auto;
                    background:#ffffff;
                    border-radius:16px;
                    overflow:hidden;
                    box-shadow:0 6px 20px rgba(0,0,0,0.08);
                ">

                    <div style="
                        background:#1f5fae;
                        padding:24px 28px;
                        text-align:center;
                        color:white;
                    ">

                        <img
                            src="cid:logoGuaviyu"
                            alt="Alojamientos Guaviyú"
                            style="
                                width:95px;
                                height:auto;
                                margin-bottom:10px;
                            "
                        >

                        <div style="
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:20px;
                            font-weight:700;
                            letter-spacing:0.2px;
                        ">
                            Alojamientos Guaviyú
                        </div>

                    </div>

                    <div style="
                        padding:34px 34px 30px;
                        text-align:center;
                    ">

                        <div style="
                            width:54px;
                            height:54px;
                            line-height:54px;
                            margin:0 auto 20px;
                            border-radius:50%%;
                            background:#eaf2fb;
                            color:#1f5fae;
                            font-size:27px;
                            font-weight:bold;
                        ">
                            ✓
                        </div>

                        <h2 style="
                            margin:0 0 12px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:26px;
                            font-weight:700;
                        ">
                            Tu contraseña fue actualizada
                        </h2>

                        <p style="
                            margin:0;
                            color:#68717c;
                            font-size:15px;
                            line-height:1.7;
                        ">
                            Hola %s, la contraseña de tu cuenta
                            fue modificada correctamente.
                        </p>

                        <div style="
                            text-align:left;
                            background:#f8fafc;
                            border:1px solid #e5e9ee;
                            border-radius:12px;
                            padding:22px 24px;
                            margin:28px 0;
                        ">

                            <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Nombre:</strong>
                                %s
                            </p>

                            <p style="margin:0; font-size:14px; color:#5f6873;">
                                <strong style="color:#30363d;">Correo:</strong>
                                %s
                            </p>

                        </div>

                        <h3 style="
                            margin:0 0 8px;
                            color:#1f5fae;
                            font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                            font-size:18px;
                        ">
                            ¿No realizaste este cambio?
                        </h3>

                        <p style="
                            margin:0;
                            color:#7a838d;
                            font-size:14px;
                            line-height:1.6;
                        ">
                            Si no realizaste el cambio,
                            comunicate con la administración
                            de Alojamientos Guaviyú.
                        </p>

                    </div>

                    <div style="
                        background:#f1f4f6;
                        padding:18px;
                        text-align:center;
                        font-size:12px;
                        color:#7c858e;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

            </body>
            </html>
            """.formatted(cliente.getNombre(), cliente.getNombre(), cliente.getEmail());
    }

    public static String recordatorioEstadia(Cliente cliente, ReservaDetalle detalle) {

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
            background:#f3f5f7;
            font-family:'Segoe UI', Arial, sans-serif;
            color:#333333;
        ">

            <div style="
                max-width:600px;
                margin:32px auto;
                background:#ffffff;
                border-radius:16px;
                overflow:hidden;
                box-shadow:0 6px 20px rgba(0,0,0,0.08);
            ">

                <div style="
                    background:#1f5fae;
                    padding:24px 28px;
                    text-align:center;
                    color:white;
                ">

                    <img
                        src="cid:logoGuaviyu"
                        alt="Alojamientos Guaviyú"
                        style="
                            width:95px;
                            height:auto;
                            margin-bottom:10px;
                        "
                    >

                    <div style="
                        font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                        font-size:20px;
                        font-weight:700;
                        letter-spacing:0.2px;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

                <div style="
                    padding:34px 34px 30px;
                    text-align:center;
                ">

                    <div style="
                        width:54px;
                        height:54px;
                        line-height:54px;
                        margin:0 auto 20px;
                        border-radius:50%%;
                        background:#eaf2fb;
                        color:#1f5fae;
                        font-size:27px;
                        font-weight:bold;
                    ">
                        !
                    </div>

                    <h2 style="
                        margin:0 0 12px;
                        color:#1f5fae;
                        font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                        font-size:26px;
                        font-weight:700;
                    ">
                        Tu estadía se acerca
                    </h2>

                    <p style="
                        margin:0;
                        color:#68717c;
                        font-size:15px;
                        line-height:1.7;
                    ">
                        Hola %s, faltan pocos días para tu estadía
                        en Alojamientos Guaviyú.
                    </p>

                    <div style="
                        text-align:left;
                        background:#f8fafc;
                        border:1px solid #e5e9ee;
                        border-radius:12px;
                        padding:22px 24px;
                        margin:28px 0;
                    ">

                        <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                            <strong style="color:#30363d;">Alojamiento:</strong>
                            %s
                        </p>

                        <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                            <strong style="color:#30363d;">Ingreso:</strong>
                            %s
                        </p>

                        <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                            <strong style="color:#30363d;">Salida:</strong>
                            %s
                        </p>

                        <p style="margin:0; font-size:14px; color:#5f6873;">
                            <strong style="color:#30363d;">Huéspedes:</strong>
                            %d
                        </p>

                    </div>

                    <h3 style="
                        margin:0 0 8px;
                        color:#1f5fae;
                        font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                        font-size:18px;
                    ">
                        Te esperamos en Guaviyú
                    </h3>

                    <p style="
                        margin:0;
                        color:#7a838d;
                        font-size:14px;
                        line-height:1.6;
                    ">
                        Si tenés alguna consulta antes de tu llegada,
                        podés comunicarte con la administración
                        de Alojamientos Guaviyú.
                    </p>

                </div>

                <div style="
                    background:#f1f4f6;
                    padding:18px;
                    text-align:center;
                    font-size:12px;
                    color:#7c858e;
                ">
                    Alojamientos Guaviyú
                </div>

            </div>

        </body>
        </html>
        """.formatted(cliente.getNombre(), detalle.getAlojamientoNombre(), fechaIngreso, fechaSalida, detalle.getCantidadHuespedes());
    }

    public static String solicitarResena(Cliente cliente, ReservaDetalle detalle, String enlaceResena) {

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
            background:#f3f5f7;
            font-family:'Segoe UI', Arial, sans-serif;
            color:#333333;
        ">

            <div style="
                max-width:600px;
                margin:32px auto;
                background:#ffffff;
                border-radius:16px;
                overflow:hidden;
                box-shadow:0 6px 20px rgba(0,0,0,0.08);
            ">

                <div style="
                    background:#1f5fae;
                    padding:24px 28px;
                    text-align:center;
                    color:white;
                ">

                    <img
                        src="cid:logoGuaviyu"
                        alt="Alojamientos Guaviyú"
                        style="
                            width:95px;
                            height:auto;
                            margin-bottom:10px;
                        "
                    >

                    <div style="
                        font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                        font-size:20px;
                        font-weight:700;
                        letter-spacing:0.2px;
                    ">
                        Alojamientos Guaviyú
                    </div>

                </div>

                <div style="
                    padding:34px 34px 30px;
                    text-align:center;
                ">

                    <div style="
                        width:54px;
                        height:54px;
                        line-height:54px;
                        margin:0 auto 20px;
                        border-radius:50%%;
                        background:#eaf2fb;
                        color:#1f5fae;
                        font-size:27px;
                        font-weight:bold;
                    ">
                        ★
                    </div>

                    <h2 style="
                        margin:0 0 12px;
                        color:#1f5fae;
                        font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                        font-size:26px;
                        font-weight:700;
                    ">
                        ¿Cómo fue tu estadía?
                    </h2>

                    <p style="
                        margin:0;
                        color:#68717c;
                        font-size:15px;
                        line-height:1.7;
                    ">
                        Hola %s, esperamos que hayas disfrutado
                        tu estadía en Alojamientos Guaviyú.
                    </p>

                    <div style="
                        text-align:left;
                        background:#f8fafc;
                        border:1px solid #e5e9ee;
                        border-radius:12px;
                        padding:22px 24px;
                        margin:28px 0;
                    ">

                        <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                            <strong style="color:#30363d;">Alojamiento:</strong>
                            %s
                        </p>

                        <p style="margin:0 0 14px; font-size:14px; color:#5f6873;">
                            <strong style="color:#30363d;">Ingreso:</strong>
                            %s
                        </p>

                        <p style="margin:0; font-size:14px; color:#5f6873;">
                            <strong style="color:#30363d;">Salida:</strong>
                            %s
                        </p>

                    </div>

                    <h3 style="
                        margin:0 0 8px;
                        color:#1f5fae;
                        font-family:'Trebuchet MS','Segoe UI',Arial,sans-serif;
                        font-size:18px;
                    ">
                        Tu opinión es importante
                    </h3>

                    <p style="
                        margin:0 0 24px;
                        color:#7a838d;
                        font-size:14px;
                        line-height:1.6;
                    ">
                        Contanos cómo fue tu experiencia.
                        Tu opinión nos ayuda a seguir mejorando
                        nuestros alojamientos.
                    </p>

                    <a href="%s"
                       style="
                           display:inline-block;
                           background:#1f5fae;
                           color:#ffffff;
                           text-decoration:none;
                           padding:13px 26px;
                           border-radius:8px;
                           font-size:14px;
                           font-weight:700;
                       ">
                        Dejar mi opinión
                    </a>

                </div>

                <div style="
                    background:#f1f4f6;
                    padding:18px;
                    text-align:center;
                    font-size:12px;
                    color:#7c858e;
                ">
                    Alojamientos Guaviyú
                </div>

            </div>

        </body>
        </html>
        """.formatted(cliente.getNombre(), detalle.getAlojamientoNombre(), fechaIngreso, fechaSalida, enlaceResena);
    }

}
