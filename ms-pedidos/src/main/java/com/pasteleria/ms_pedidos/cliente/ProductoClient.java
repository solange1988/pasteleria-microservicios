package com.pasteleria.ms_pedidos.cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
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

    public boolean existeProducto(Long productoId) {
        try {
            log.info("Consultando ms-productos — ID: {}", productoId);
            Map response = webClient.get()
                    .uri("/api/productos/{id}", productoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            boolean existe = response != null;
            log.info("Producto ID {} — {}",
                    productoId, existe ? "ENCONTRADO" : "NO ENCONTRADO");
            return existe;
        } catch (Exception e) {
            log.error("Error al consultar ms-productos ID {}: {}",
                    productoId, e.getMessage());
            return false;
        }
    }


    public boolean tieneStockSuficiente(Long productoId, Integer cantidad) {
        try {
            log.info("Verificando stock — producto ID: {}, cantidad: {}",
                    productoId, cantidad);
            Map response = webClient.get()
                    .uri("/api/productos/{id}", productoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey("stock")) {
                int stock = (Integer) response.get("stock");
                boolean suficiente = stock >= cantidad;
                log.info("Stock disponible: {}, requerido: {}, suficiente: {}",
                        stock, cantidad, suficiente);
                return suficiente;
            }
            return false;
        } catch (Exception e) {
            log.error("Error al verificar stock producto ID {}: {}",
                    productoId, e.getMessage());
            return false;
        }
    }


    public BigDecimal obtenerPrecio(Long productoId) {
        try {
            log.debug("Obteniendo precio producto ID: {}", productoId);
            Map response = webClient.get()
                    .uri("/api/productos/{id}", productoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey("precio")) {
                return new BigDecimal(response.get("precio").toString());
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("Error al obtener precio producto ID {}: {}",
                    productoId, e.getMessage());
            return BigDecimal.ZERO;
        }
    }


    public String obtenerNombre(Long productoId) {
        try {
            log.debug("Obteniendo nombre producto ID: {}", productoId);
            Map response = webClient.get()
                    .uri("/api/productos/{id}", productoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null && response.containsKey("nombre")) {
                return (String) response.get("nombre");
            }
            return "Producto sin nombre";
        } catch (Exception e) {
            log.error("Error al obtener nombre producto ID {}: {}",
                    productoId, e.getMessage());
            return "Producto sin nombre";
        }
    }
}
