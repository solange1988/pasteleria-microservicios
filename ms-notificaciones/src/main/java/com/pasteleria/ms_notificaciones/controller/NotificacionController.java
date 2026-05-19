package com.pasteleria.ms_notificaciones.controller;

import com.pasteleria.ms_notificaciones.dto.*;
import com.pasteleria.ms_notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")

public class NotificacionController {

    private static final Logger log =
            LoggerFactory.getLogger(NotificacionController.class);

    private final NotificacionService notificacionService;

    public NotificacionController(
            NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }


    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(
            @Valid @RequestBody NotificacionRequestDTO dto) {
        log.info("POST /api/notificaciones — usuario ID: {}, tipo: {}",
                dto.getUsuarioId(), dto.getTipo());
        NotificacionResponseDTO creada =
                notificacionService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }


    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarTodas() {
        log.info("GET /api/notificaciones");
        return ResponseEntity.ok(notificacionService.listarTodas());
    }


    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/notificaciones/{}", id);
        return ResponseEntity.ok(
                notificacionService.buscarPorId(id));
    }


    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>>
    listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/notificaciones/usuario/{}", usuarioId);
        return ResponseEntity.ok(
                notificacionService.listarPorUsuario(usuarioId));
    }


    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<NotificacionResponseDTO>>
    listarPorPedido(@PathVariable Long pedidoId) {
        log.info("GET /api/notificaciones/pedido/{}", pedidoId);
        return ResponseEntity.ok(
                notificacionService.listarPorPedido(pedidoId));
    }


    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDTO>>
    listarPorEstado(@PathVariable String estado) {
        log.info("GET /api/notificaciones/estado/{}", estado);
        return ResponseEntity.ok(
                notificacionService.listarPorEstado(estado));
    }


    @GetMapping("/pendientes")
    public ResponseEntity<List<NotificacionResponseDTO>>
    listarPendientes() {
        log.info("GET /api/notificaciones/pendientes");
        return ResponseEntity.ok(
                notificacionService.listarPendientes());
    }


    @PatchMapping("/{id}/reenviar")
    public ResponseEntity<NotificacionResponseDTO> reenviar(
            @PathVariable Long id) {
        log.info("PATCH /api/notificaciones/{}/reenviar", id);
        return ResponseEntity.ok(notificacionService.reenviar(id));
    }


    @GetMapping("/fechas")
    public ResponseEntity<List<NotificacionResponseDTO>>
    buscarPorFecha(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {
        log.info("GET /api/notificaciones/fechas");
        return ResponseEntity.ok(
                notificacionService.buscarPorFecha(inicio, fin));
    }


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/notificaciones/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-notificaciones",
                "puerto", "8087"
        ));
    }
}
