package com.pasteleria.ms_pagos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Pago {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "El pedido es obligatorio")
    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "El monto debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado;


    @Column(name = "referencia", length = 100)
    private String referencia;

    @Size(max = 255, message = "Las notas no pueden superar 255 caracteres")
    @Column(length = 255)
    private String notas;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoPago.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum MetodoPago {
        EFECTIVO,
        TARJETA_DEBITO,
        TARJETA_CREDITO,
        TRANSFERENCIA,
        OTRO
    }

    public enum EstadoPago {
        PENDIENTE,
        APROBADO,
        RECHAZADO,
        ANULADO
    }
}
