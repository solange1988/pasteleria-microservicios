package com.pasteleria.ms_reportes.controller;

import com.pasteleria.ms_reportes.dto.ReporteResponseDTO;
import com.pasteleria.ms_reportes.model.Reporte;
import com.pasteleria.ms_reportes.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
@Tag(name = "Reportes", description = "Endpoints para generación de reportes")
public class ReporteController {

    private static final Logger log =
            LoggerFactory.getLogger(ReporteController.class);

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @Operation(summary = "Reporte general", description = "Genera un reporte general del sistema")
    @ApiResponse(responseCode = "200", description = "Reporte general generado exitosamente")
    @GetMapping("/general")
    public ResponseEntity<ReporteResponseDTO> reporteGeneral() {
        log.info("GET /api/reportes/general");
        return ResponseEntity.ok(reporteService.reporteGeneral());
    }

    @Operation(summary = "Reporte de ventas", description = "Genera un reporte de ventas")
    @ApiResponse(responseCode = "200", description = "Reporte de ventas generado exitosamente")
    @GetMapping("/ventas")
    public ResponseEntity<ReporteResponseDTO> reporteVentas() {
        log.info("GET /api/reportes/ventas");
        return ResponseEntity.ok(reporteService.reporteVentas());
    }

    @Operation(summary = "Reporte de pedidos", description = "Genera un reporte de pedidos")
    @ApiResponse(responseCode = "200", description = "Reporte de pedidos generado exitosamente")
    @GetMapping("/pedidos")
    public ResponseEntity<ReporteResponseDTO> reportePedidos() {
        log.info("GET /api/reportes/pedidos");
        return ResponseEntity.ok(reporteService.reportePedidos());
    }

    @Operation(summary = "Reporte de productos", description = "Genera un reporte de productos")
    @ApiResponse(responseCode = "200", description = "Reporte de productos generado exitosamente")
    @GetMapping("/productos")
    public ResponseEntity<ReporteResponseDTO> reporteProductos() {
        log.info("GET /api/reportes/productos");
        return ResponseEntity.ok(reporteService.reporteProductos());
    }

    @Operation(summary = "Reporte de pedidos entregados", description = "Genera un reporte de pedidos entregados")
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    @GetMapping("/pedidos-entregados")
    public ResponseEntity<ReporteResponseDTO> pedidosEntregados() {
        log.info("GET /api/reportes/pedidos-entregados");
        return ResponseEntity.ok(reporteService.reportePedidosEntregados());
    }

    @Operation(summary = "Historial de reportes", description = "Lista el historial de reportes generados")
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    @GetMapping("/historial")
    public ResponseEntity<CollectionModel<EntityModel<Reporte>>> historial() {
        log.info("GET /api/reportes/historial");
        List<EntityModel<Reporte>> reportes = reporteService.listarHistorial()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Reporte>> recurso = CollectionModel.of(reportes);
        recurso.add(linkTo(methodOn(ReporteController.class).historial()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Historial de reportes por tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial por tipo obtenido"),
            @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    })
    @GetMapping("/historial/{tipo}")
    public ResponseEntity<List<Reporte>> historialPorTipo(
            @PathVariable String tipo) {
        log.info("GET /api/reportes/historial/{}", tipo);
        return ResponseEntity.ok(reporteService.historialPorTipo(tipo));
    }

    @Operation(summary = "Buscar reporte por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte encontrado"),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @GetMapping("/historial/id/{id}")
    public ResponseEntity<EntityModel<Reporte>> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/reportes/historial/id/{}", id);
        Reporte reporte = reporteService.buscarPorId(id);
        return ResponseEntity.ok(agregarLinks(reporte));
    }

    @Operation(summary = "Health check", description = "Verifica que el microservicio esté activo")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/reportes/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-reportes",
                "puerto", "8088"
        ));
    }

    private EntityModel<Reporte> agregarLinks(Reporte reporte) {
        EntityModel<Reporte> recurso = EntityModel.of(reporte);
        recurso.add(linkTo(methodOn(ReporteController.class).buscarPorId(reporte.getId())).withSelfRel());
        recurso.add(linkTo(methodOn(ReporteController.class).historial()).withRel("todo-el-historial"));
        return recurso;
    }
}