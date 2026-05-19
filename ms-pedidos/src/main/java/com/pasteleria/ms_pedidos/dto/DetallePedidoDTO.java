package com.pasteleria.ms_pedidos.dto;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetallePedidoDTO {

    private Long id;

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    private String productoNombre;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
