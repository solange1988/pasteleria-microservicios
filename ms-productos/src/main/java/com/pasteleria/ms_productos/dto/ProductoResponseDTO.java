package com.pasteleria.ms_productos.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor



public class ProductoResponseDTO {


    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Long categoriaId;
    private String categoriaNombre;
    private boolean disponible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
