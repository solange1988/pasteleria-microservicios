package com.pasteleria.ms_productos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
public class CategoriaClient {

    private static final Logger log = LoggerFactory.getLogger(CategoriaClient.class);


    private final WebClient webClient;


    public CategoriaClient(WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.baseUrl("http://ms-categorias").build();
    }

    public boolean existeCategoria(Long categoriaId) {
        try {
            log.info("Consultando ms-categorias para ID: {}", categoriaId);

            Map<?, ?> response = webClient.get()
                    .uri("/api/categorias/{id}", categoriaId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // Bloquea para obtener el resultado de forma sincrónica

            boolean existe = response != null;
            log.info("Categoría ID {} {}", categoriaId, existe ? "encontrada" : "no encontrada");
            return existe;

        } catch (Exception e) {
            log.error("Error al consultar ms-categorias: {}", e.getMessage());
            return false;
        }
    }

    public String obtenerNombreCategoria(Long categoriaId) {
        try {
            log.info("Obteniendo nombre de categoría ID: {}", categoriaId);

            Map<?, ?> response = webClient.get()
                    .uri("/api/categorias/{id}", categoriaId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.get("nombre") != null) {
                return (String) response.get("nombre");
            }
            return "Sin categoría";

        } catch (Exception e) {
            log.error("Error al obtener nombre de categoría: {}", e.getMessage());
            return "Sin categoría";
        }
    }
}