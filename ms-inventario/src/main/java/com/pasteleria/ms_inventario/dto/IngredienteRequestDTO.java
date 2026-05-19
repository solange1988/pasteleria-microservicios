package com.pasteleria.ms_inventario.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data

public class IngredienteRequestDTO {

    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    @Size(min = 2, max = 100,
            message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(max = 255,
            message = "La descripción no puede superar 255 caracteres")
    private String descripcion;

    @NotNull(message = "El stock actual es obligatorio")
    @DecimalMin(value = "0.0",
            message = "El stock no puede ser negativo")
    private Double stockActual;

    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.0",
            message = "El stock mínimo no puede ser negativo")
    private Double stockMinimo;

    @NotBlank(message = "La unidad de medida es obligatoria")
    private String unidadMedida;
}
