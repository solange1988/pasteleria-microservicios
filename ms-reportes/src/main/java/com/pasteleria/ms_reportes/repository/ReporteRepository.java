package com.pasteleria.ms_reportes.repository;
import com.pasteleria.ms_reportes.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;


@Repository

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByTipoReporte(String tipoReporte);


    List<Reporte> findByEstado(Reporte.EstadoReporte estado);

    List<Reporte> findByGeneradoPor(String generadoPor);


    @Query("SELECT r FROM Reporte r WHERE " +
            "r.fechaGeneracion BETWEEN :inicio AND :fin " +
            "ORDER BY r.fechaGeneracion DESC")
    List<Reporte> buscarPorRangoFecha(
            LocalDateTime inicio, LocalDateTime fin);


    @Query("SELECT r FROM Reporte r WHERE " +
            "r.tipoReporte = :tipo " +
            "ORDER BY r.fechaGeneracion DESC")
    List<Reporte> buscarPorTipo(String tipo);
}



