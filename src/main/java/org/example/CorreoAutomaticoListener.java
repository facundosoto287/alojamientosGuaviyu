package org.example;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class CorreoAutomaticoListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
                    try {

                        CorreoAutomaticoServicio servicio = new CorreoAutomaticoServicio();

                        servicio.procesarCorreos();

                    } catch (Exception e) {

                        System.err.println("Error al procesar los correos automáticos");

                        e.printStackTrace();
                    }
                },

                10,
                3600,
                TimeUnit.SECONDS);

        System.out.println("Servicio de correos automaticos iniciado.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        if (scheduler != null) {
            scheduler.shutdownNow();
        }

        System.out.println("Servicio de correos automáticos detenido.");
    }
}