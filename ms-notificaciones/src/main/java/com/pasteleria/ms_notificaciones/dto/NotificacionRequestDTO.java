package com.pasteleria.ms_notificaciones.dto;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data

public class NotificacionRequestDTO {
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    private Long pedidoId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150,
            message = "El título no puede superar 150 caracteres")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500,
            message = "El mensaje no puede superar 500 caracteres")
    private String mensaje;

    @NotBlank(message = "El tipo de notificación es obligatorio")
    private String tipo;
}
