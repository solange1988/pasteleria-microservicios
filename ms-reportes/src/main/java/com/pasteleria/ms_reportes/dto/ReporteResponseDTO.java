package com.pasteleria.ms_reportes.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ReporteResponseDTO {


    private String tipoReporte;
    private LocalDateTime fechaGeneracion;
    private String periodo;


    private Long totalPedidos;
    private Long pedidosPendientes;
    private Long pedidosConfirmados;
    private Long pedidosEnPreparacion;
    private Long pedidosListos;
    private Long pedidosEntregados;
    private Long pedidosCancelados;


    private Long totalPagos;
    private Long pagosPendientes;
    private Long pagosAprobados;
    private Long pagosRechazados;
    private BigDecimal totalRecaudado;


    private Long totalProductos;
    private Long productosDisponibles;
    private Long productosStockBajo;


    private List<Map<String, Object>> detalle;
    private String mensaje;


}
