package com.pasteleria.ms_pedidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "El usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;


    @Column(name = "usuario_nombre", length = 150)
    private String usuarioNombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Size(max = 500, message = "Las notas no pueden superar 500 caracteres")
    @Column(length = 500)
    private String notas;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;


    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<DetallePedido> detalles = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoPedido.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum EstadoPedido {
        PENDIENTE,
        CONFIRMADO,
        EN_PREPARACION,
        LISTO,
        ENTREGADO,
        CANCELADO
    }
}
