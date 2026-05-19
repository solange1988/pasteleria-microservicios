package com.pasteleria.ms_reportes.cliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class ProductoClient {

    private static final Logger log =
            LoggerFactory.getLogger(ProductoClient.class);

    private final WebClient webClient;

    public ProductoClient(
            @Qualifier("productosClient") WebClient webClient) {
        this.webClient = webClient;
    }


    public List<Map<String, Object>> obtenerTodos() {
        try {
            log.info("Consultando todos los productos");
            List<Map<String, Object>> productos = webClient.get()
                    .uri("/api/productos")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference
                            <List<Map<String, Object>>>() {})
                    .block();
            return productos != null
                    ? productos : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error al obtener productos: {}",
                    e.getMessage());
            return new ArrayList<>();
        }
    }


    public List<Map<String, Object>> obtenerDisponibles() {
        try {
            log.info("Consultando productos disponibles");
            List<Map<String, Object>> productos = webClient.get()
                    .uri("/api/productos/disponibles")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference
                            <List<Map<String, Object>>>() {})
                    .block();
            return productos != null
                    ? productos : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error al obtener disponibles: {}",
                    e.getMessage());
            return new ArrayList<>();
        }
    }


    public List<Map<String, Object>> obtenerStockBajo() {
        try {
            log.info("Consultando productos con stock bajo");
            List<Map<String, Object>> productos = webClient.get()
                    .uri("/api/productos/stock-bajo")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference
                            <List<Map<String, Object>>>() {})
                    .block();
            return productos != null
                    ? productos : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error al obtener stock bajo: {}",
                    e.getMessage());
            return new ArrayList<>();
        }
    }
}