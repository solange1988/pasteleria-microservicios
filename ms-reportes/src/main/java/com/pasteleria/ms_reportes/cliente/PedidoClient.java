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
public class PedidoClient {

    private static final Logger log =
            LoggerFactory.getLogger(PedidoClient.class);

    private final WebClient webClient;

    public PedidoClient(
            @Qualifier("pedidosClient") WebClient webClient) {
        this.webClient = webClient;
    }


    public List<Map<String, Object>> obtenerTodos() {
        try {
            log.info("Consultando todos los pedidos");
            List<Map<String, Object>> pedidos = webClient.get()
                    .uri("/api/pedidos")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference
                            <List<Map<String, Object>>>() {})
                    .block();
            return pedidos != null ? pedidos : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error al obtener pedidos: {}",
                    e.getMessage());
            return new ArrayList<>();
        }
    }


    public List<Map<String, Object>> obtenerPorEstado(
            String estado) {
        try {
            log.info("Consultando pedidos estado: {}", estado);
            List<Map<String, Object>> pedidos = webClient.get()
                    .uri("/api/pedidos/estado/{estado}", estado)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference
                            <List<Map<String, Object>>>() {})
                    .block();
            return pedidos != null ? pedidos : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error al obtener pedidos por estado: {}",
                    e.getMessage());
            return new ArrayList<>();
        }
    }
}

