package com.pasteleria.ms_reportes.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data

public class ReporteRequestDTO {

    @NotBlank(message = "El tipo de reporte es obligatorio")
    private String tipoReporte;

    private String generadoPor;

    @Size(max = 255,
            message = "La descripción no puede superar 255 caracteres")
    private String descripcion;
}
