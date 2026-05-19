package com.pasteleria.ms_reportes.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_reporte", nullable = false, length = 50)
    private String tipoReporte;

    @Column(name = "generado_por", length = 100)
    private String generadoPor;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "total_pedidos")
    private Long totalPedidos;

    @Column(name = "total_pagos")
    private Long totalPagos;

    @Column(name = "total_productos")
    private Long totalProductos;

    @Column(name = "total_recaudado")
    private BigDecimal totalRecaudado;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReporte estado;

    @Column(name = "fecha_generacion", updatable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.fechaGeneracion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoReporte.GENERADO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum EstadoReporte {
        GENERADO,
        PROCESANDO,
        ERROR
    }
}
