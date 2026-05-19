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
public class PagoClient {

    private static final Logger log =
            LoggerFactory.getLogger(PagoClient.class);

    private final WebClient webClient;

    public PagoClient(
            @Qualifier("pagosClient") WebClient webClient) {
        this.webClient = webClient;
    }


    public List<Map<String, Object>> obtenerTodos() {
        try {
            log.info("Consultando todos los pagos");
            List<Map<String, Object>> pagos = webClient.get()
                    .uri("/api/pagos")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference
                            <List<Map<String, Object>>>() {})
                    .block();
            return pagos != null ? pagos : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error al obtener pagos: {}",
                    e.getMessage());
            return new ArrayList<>();
        }
    }


    public List<Map<String, Object>> obtenerPorEstado(
            String estado) {
        try {
            log.info("Consultando pagos estado: {}", estado);
            List<Map<String, Object>> pagos = webClient.get()
                    .uri("/api/pagos/estado/{estado}", estado)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference
                            <List<Map<String, Object>>>() {})
                    .block();
            return pagos != null ? pagos : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error al obtener pagos por estado: {}",
                    e.getMessage());
            return new ArrayList<>();
        }
    }
}