package com.pasteleria.ms_pedidos.cliente;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;


@Component
public class UsuarioClient {

    private static final Logger log =
            LoggerFactory.getLogger(UsuarioClient.class);

    private final WebClient webClient;

    public UsuarioClient(
            @Qualifier("usuariosClient") WebClient webClient) {
        this.webClient = webClient;
    }


    public boolean existeUsuario(Long usuarioId) {
        try {
            log.info("Consultando ms-usuarios — ID: {}", usuarioId);
            Map response = webClient.get()
                    .uri("/api/usuarios/{id}", usuarioId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            boolean existe = response != null;
            log.info("Usuario ID {} — {}",
                    usuarioId, existe ? "ENCONTRADO" : "NO ENCONTRADO");
            return existe;
        } catch (Exception e) {
            log.error("Error al consultar ms-usuarios ID {}: {}",
                    usuarioId, e.getMessage());
            return false;
        }
    }


    public String obtenerNombreUsuario(Long usuarioId) {
        try {
            log.debug("Obteniendo nombre usuario ID: {}", usuarioId);
            Map response = webClient.get()
                    .uri("/api/usuarios/{id}", usuarioId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null) {
                String nombre = (String) response
                        .getOrDefault("nombre", "");
                String apellido = (String) response
                        .getOrDefault("apellido", "");
                return (nombre + " " + apellido).trim();
            }
            return "Usuario desconocido";
        } catch (Exception e) {
            log.error("Error al obtener nombre usuario ID {}: {}",
                    usuarioId, e.getMessage());
            return "Usuario desconocido";
        }
    }
}
