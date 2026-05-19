package com.pasteleria.ms_reportes.controller;

import com.pasteleria.ms_reportes.dto.ReporteResponseDTO;
import com.pasteleria.ms_reportes.model.Reporte;
import com.pasteleria.ms_reportes.service.ReporteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")


public class ReporteController {

    private static final Logger log =
            LoggerFactory.getLogger(ReporteController.class);

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }


    @GetMapping("/general")
    public ResponseEntity<ReporteResponseDTO> reporteGeneral() {
        log.info("GET /api/reportes/general");
        return ResponseEntity.ok(reporteService.reporteGeneral());
    }


    @GetMapping("/ventas")
    public ResponseEntity<ReporteResponseDTO> reporteVentas() {
        log.info("GET /api/reportes/ventas");
        return ResponseEntity.ok(reporteService.reporteVentas());
    }


    @GetMapping("/pedidos")
    public ResponseEntity<ReporteResponseDTO> reportePedidos() {
        log.info("GET /api/reportes/pedidos");
        return ResponseEntity.ok(reporteService.reportePedidos());
    }


    @GetMapping("/productos")
    public ResponseEntity<ReporteResponseDTO> reporteProductos() {
        log.info("GET /api/reportes/productos");
        return ResponseEntity.ok(reporteService.reporteProductos());
    }


    @GetMapping("/pedidos-entregados")
    public ResponseEntity<ReporteResponseDTO> pedidosEntregados() {
        log.info("GET /api/reportes/pedidos-entregados");
        return ResponseEntity.ok(
                reporteService.reportePedidosEntregados());
    }


    @GetMapping("/historial")
    public ResponseEntity<List<Reporte>> historial() {
        log.info("GET /api/reportes/historial");
        return ResponseEntity.ok(reporteService.listarHistorial());
    }


    @GetMapping("/historial/{tipo}")
    public ResponseEntity<List<Reporte>> historialPorTipo(
            @PathVariable String tipo) {
        log.info("GET /api/reportes/historial/{}", tipo);
        return ResponseEntity.ok(
                reporteService.historialPorTipo(tipo));
    }


    @GetMapping("/historial/id/{id}")
    public ResponseEntity<Reporte> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/reportes/historial/id/{}", id);
        return ResponseEntity.ok(reporteService.buscarPorId(id));
    }


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/reportes/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-reportes",
                "puerto", "8088"
        ));
    }
}
