package com.pasteleria.ms_pagos.dto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class PagoResponseDTO {

    private Long id;
    private Long pedidoId;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private String referencia;
    private String notas;
    private LocalDateTime fechaPago;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
