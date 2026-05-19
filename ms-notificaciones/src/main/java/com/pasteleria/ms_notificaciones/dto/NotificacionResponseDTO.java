package com.pasteleria.ms_notificaciones.dto;

import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class NotificacionResponseDTO {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private Long pedidoId;
    private String titulo;
    private String mensaje;
    private String tipo;
    private String estado;
    private LocalDateTime fechaEnvio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
