package com.pasteleria.ms_pagos.repository;

import com.pasteleria.ms_pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long>{


    Optional<Pago> findByPedidoId(Long pedidoId);


    List<Pago> findByEstado(Pago.EstadoPago estado);


    List<Pago> findByMetodoPago(Pago.MetodoPago metodoPago);


    boolean existsByPedidoIdAndEstado(
            Long pedidoId, Pago.EstadoPago estado);


    @Query("SELECT p FROM Pago p WHERE " +
            "p.createdAt BETWEEN :inicio AND :fin " +
            "ORDER BY p.createdAt DESC")
    List<Pago> buscarPorRangoFecha(
            LocalDateTime inicio, LocalDateTime fin);
}
