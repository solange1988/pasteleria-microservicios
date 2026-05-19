package com.pasteleria.ms_inventario.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "ingredientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    @Size(min = 2, max = 100,
            message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    @Column(length = 255)
    private String descripcion;

    @NotNull(message = "El stock actual es obligatorio")
    @DecimalMin(value = "0.0",
            message = "El stock no puede ser negativo")
    @Column(name = "stock_actual", nullable = false)
    private Double stockActual;

    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.0",
            message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo", nullable = false)
    private Double stockMinimo;

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
