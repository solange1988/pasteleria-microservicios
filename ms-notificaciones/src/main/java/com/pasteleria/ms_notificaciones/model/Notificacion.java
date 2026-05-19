package com.pasteleria.ms_notificaciones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;


    @Column(name = "usuario_nombre", length = 150)
    private String usuarioNombre;


    @Column(name = "usuario_email", length = 150)
    private String usuarioEmail;


    @Column(name = "pedido_id")
    private Long pedidoId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 500, message = "El mensaje no puede superar 500 caracteres")
    @Column(nullable = false, length = 500)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoNotificacion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoNotificacion estado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoNotificacion.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum TipoNotificacion {
        CONFIRMACION_PEDIDO,
        PEDIDO_EN_PREPARACION,
        PEDIDO_LISTO,
        PEDIDO_ENTREGADO,
        PEDIDO_CANCELADO,
        PAGO_APROBADO,
        PAGO_RECHAZADO,
        OTRO
    }

    public enum EstadoNotificacion {
        PENDIENTE,
        ENVIADA,
        FALLIDA
    }
}
