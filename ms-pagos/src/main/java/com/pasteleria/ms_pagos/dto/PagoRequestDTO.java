package com.pasteleria.ms_pagos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoRequestDTO {
    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotNull(message = "El método de pago es obligatorio")
    private String metodoPago;

    @Size(max = 100, message = "La referencia no puede superar 100 caracteres")
    private String referencia;

    @Size(max = 255, message = "Las notas no pueden superar 255 caracteres")
    private String notas;
}
