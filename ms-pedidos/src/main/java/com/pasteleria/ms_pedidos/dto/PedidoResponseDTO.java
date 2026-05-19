package com.pasteleria.ms_pedidos.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoResponseDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String estado;
    private BigDecimal total;
    private String notas;
    private LocalDateTime fechaEntrega;
    private List<DetallePedidoDTO> detalles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
