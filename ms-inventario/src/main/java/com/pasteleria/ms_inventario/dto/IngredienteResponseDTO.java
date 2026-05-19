package com.pasteleria.ms_inventario.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor


public class IngredienteResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double stockActual;
    private Double stockMinimo;
    private String unidadMedida;
    private boolean activo;
    private boolean stockBajo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
