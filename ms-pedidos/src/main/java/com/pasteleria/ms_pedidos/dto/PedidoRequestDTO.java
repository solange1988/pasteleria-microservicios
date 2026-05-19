package com.pasteleria.ms_pedidos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class PedidoRequestDTO {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotEmpty(message = "El pedido debe tener al menos un producto")
    private List<DetallePedidoDTO> detalles;

    @Size(max = 500, message = "Las notas no pueden superar 500 caracteres")
    private String notas;

    private LocalDateTime fechaEntrega;
}
