package com.pasteleria.ms_notificaciones.repository;


import com.pasteleria.ms_notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface NotificacionRepository extends JpaRepository<Notificacion, Long>{


    List<Notificacion> findByUsuarioId(Long usuarioId);


    List<Notificacion> findByPedidoId(Long pedidoId);

    List<Notificacion> findByEstado(
            Notificacion.EstadoNotificacion estado);


    List<Notificacion> findByTipo(
            Notificacion.TipoNotificacion tipo);


    List<Notificacion> findByUsuarioIdAndEstado(
            Long usuarioId,
            Notificacion.EstadoNotificacion estado);


    @Query("SELECT n FROM Notificacion n WHERE " +
            "n.estado = 'PENDIENTE' " +
            "ORDER BY n.createdAt ASC")
    List<Notificacion> buscarPendientes();


    @Query("SELECT n FROM Notificacion n WHERE " +
            "n.createdAt BETWEEN :inicio AND :fin " +
            "ORDER BY n.createdAt DESC")
    List<Notificacion> buscarPorRangoFecha(
            LocalDateTime inicio, LocalDateTime fin);
}
