package com.pasteleria.ms_pedidos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;



@Entity
@Table(name = "detalle_pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;


    @NotNull(message = "El producto es obligatorio")
    @Column(name = "producto_id", nullable = false)
    private Long productoId;


    @Column(name = "producto_nombre", length = 150)
    private String productoNombre;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;


    @Column(name = "precio_unitario", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal precioUnitario;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
