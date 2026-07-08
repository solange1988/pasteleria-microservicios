package com.pasteleria.ms_notificaciones.controller;

import com.pasteleria.ms_notificaciones.dto.*;
import com.pasteleria.ms_notificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
@Tag(name = "Notificaciones", description = "Endpoints para gestión de notificaciones")
public class NotificacionController {

    private static final Logger log =
            LoggerFactory.getLogger(NotificacionController.class);

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @Operation(summary = "Crear notificación", description = "Crea una nueva notificación en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<NotificacionResponseDTO>> crear(
            @Valid @RequestBody NotificacionRequestDTO dto) {
        log.info("POST /api/notificaciones — usuario ID: {}, tipo: {}",
                dto.getUsuarioId(), dto.getTipo());
        NotificacionResponseDTO creada = notificacionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(agregarLinks(creada));
    }

    @Operation(summary = "Listar todas las notificaciones")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<NotificacionResponseDTO>>> listarTodas() {
        log.info("GET /api/notificaciones");
        List<EntityModel<NotificacionResponseDTO>> notificaciones = notificacionService.listarTodas()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<NotificacionResponseDTO>> recurso = CollectionModel.of(notificaciones);
        recurso.add(linkTo(methodOn(NotificacionController.class).listarTodas()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Buscar notificación por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<NotificacionResponseDTO>> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/notificaciones/{}", id);
        NotificacionResponseDTO notificacion = notificacionService.buscarPorId(id);
        return ResponseEntity.ok(agregarLinks(notificacion));
    }

    @Operation(summary = "Listar notificaciones por usuario")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        log.info("GET /api/notificaciones/usuario/{}", usuarioId);
        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Listar notificaciones por pedido")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones del pedido")
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorPedido(
            @PathVariable Long pedidoId) {
        log.info("GET /api/notificaciones/pedido/{}", pedidoId);
        return ResponseEntity.ok(notificacionService.listarPorPedido(pedidoId));
    }

    @Operation(summary = "Listar notificaciones por estado")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorEstado(
            @PathVariable String estado) {
        log.info("GET /api/notificaciones/estado/{}", estado);
        return ResponseEntity.ok(notificacionService.listarPorEstado(estado));
    }

    @Operation(summary = "Listar notificaciones pendientes")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones pendientes")
    @GetMapping("/pendientes")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPendientes() {
        log.info("GET /api/notificaciones/pendientes");
        return ResponseEntity.ok(notificacionService.listarPendientes());
    }

    @Operation(summary = "Reenviar notificación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación reenviada correctamente"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @PatchMapping("/{id}/reenviar")
    public ResponseEntity<EntityModel<NotificacionResponseDTO>> reenviar(
            @PathVariable Long id) {
        log.info("PATCH /api/notificaciones/{}/reenviar", id);
        NotificacionResponseDTO reenviada = notificacionService.reenviar(id);
        return ResponseEntity.ok(agregarLinks(reenviada));
    }

    @Operation(summary = "Buscar notificaciones por rango de fechas")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones en el rango de fechas")
    @GetMapping("/fechas")
    public ResponseEntity<List<NotificacionResponseDTO>> buscarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/notificaciones/fechas");
        return ResponseEntity.ok(notificacionService.buscarPorFecha(inicio, fin));
    }

    @Operation(summary = "Health check", description = "Verifica que el microservicio esté activo")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/notificaciones/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-notificaciones",
                "puerto", "8087"
        ));
    }

    private EntityModel<NotificacionResponseDTO> agregarLinks(NotificacionResponseDTO notificacion) {
        EntityModel<NotificacionResponseDTO> recurso = EntityModel.of(notificacion);
        recurso.add(linkTo(methodOn(NotificacionController.class).buscarPorId(notificacion.getId())).withSelfRel());
        recurso.add(linkTo(methodOn(NotificacionController.class).listarTodas()).withRel("todas-las-notificaciones"));
        recurso.add(linkTo(methodOn(NotificacionController.class).reenviar(notificacion.getId())).withRel("reenviar"));
        return recurso;
    }
}