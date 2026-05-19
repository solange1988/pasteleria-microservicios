package com.pasteleria.ms_pedidos.repository;
import com.pasteleria.ms_pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository

public interface PedidoRepository extends JpaRepository<Pedido, Long> {


    List<Pedido> findByUsuarioId(Long usuarioId);


    List<Pedido> findByEstado(Pedido.EstadoPedido estado);


    List<Pedido> findByUsuarioIdAndEstado(
            Long usuarioId, Pedido.EstadoPedido estado);


    @Query("SELECT p FROM Pedido p WHERE " +
            "p.createdAt BETWEEN :inicio AND :fin " +
            "ORDER BY p.createdAt DESC")
    List<Pedido> buscarPorRangoFecha(
            LocalDateTime inicio, LocalDateTime fin);


    long countByEstado(Pedido.EstadoPedido estado);
}
