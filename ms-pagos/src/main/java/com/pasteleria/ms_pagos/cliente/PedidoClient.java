package com.pasteleria.ms_pagos.cliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;


@Component
public class PedidoClient {

    private static final Logger log =
            LoggerFactory.getLogger(PedidoClient.class);

    private final WebClient webClient;

    public PedidoClient(WebClient webClient) {
        this.webClient = webClient;
    }


    public boolean existePedido(Long pedidoId) {
        try {
            log.info("Consultando ms-pedidos — ID: {}", pedidoId);
            Map response = webClient.get()
                    .uri("/api/pedidos/{id}", pedidoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            boolean existe = response != null;
            log.info("Pedido ID {} — {}",
                    pedidoId, existe ? "ENCONTRADO" : "NO ENCONTRADO");
            return existe;
        } catch (Exception e) {
            log.error("Error al consultar ms-pedidos ID {}: {}",
                    pedidoId, e.getMessage());
            return false;
        }
    }


    public BigDecimal obtenerTotalPedido(Long pedidoId) {
        try {
            log.debug("Obteniendo total pedido ID: {}", pedidoId);
            Map response = webClient.get()
                    .uri("/api/pedidos/{id}", pedidoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey("total")) {
                return new BigDecimal(
                        response.get("total").toString());
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("Error al obtener total pedido ID {}: {}",
                    pedidoId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }


    public String obtenerEstadoPedido(Long pedidoId) {
        try {
            log.debug("Obteniendo estado pedido ID: {}", pedidoId);
            Map response = webClient.get()
                    .uri("/api/pedidos/{id}", pedidoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey("estado")) {
                return (String) response.get("estado");
            }
            return "DESCONOCIDO";
        } catch (Exception e) {
            log.error("Error al obtener estado pedido ID {}: {}",
                    pedidoId, e.getMessage());
            return "DESCONOCIDO";
        }
    }


    public boolean confirmarPedido(Long pedidoId) {
        try {
            log.info("Confirmando pedido ID: {}", pedidoId);
            webClient.patch()
                    .uri("/api/pedidos/{id}/estado?estado=CONFIRMADO",
                            pedidoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            log.info("Pedido ID {} confirmado exitosamente", pedidoId);
            return true;
        } catch (Exception e) {
            log.error("Error al confirmar pedido ID {}: {}",
                    pedidoId, e.getMessage());
            return false;
        }
    }
}
