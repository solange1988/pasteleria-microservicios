package com.pasteleria.ms_pedidos.controller;

import com.pasteleria.ms_pedidos.dto.*;
import com.pasteleria.ms_pedidos.service.PedidoService;
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
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private static final Logger log =
            LoggerFactory.getLogger(PedidoController.class);

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }


    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crear(
            @Valid @RequestBody PedidoRequestDTO dto) {
        log.info("POST /api/pedidos — usuario ID: {}",
                dto.getUsuarioId());
        PedidoResponseDTO creado = pedidoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }


    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        log.info("GET /api/pedidos");
        return ResponseEntity.ok(pedidoService.listarTodos());
    }


    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/pedidos/{}", id);
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }


    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        log.info("GET /api/pedidos/usuario/{}", usuarioId);
        return ResponseEntity.ok(
                pedidoService.listarPorUsuario(usuarioId));
    }


    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorEstado(
            @PathVariable String estado) {
        log.info("GET /api/pedidos/estado/{}", estado);
        return ResponseEntity.ok(
                pedidoService.listarPorEstado(estado));
    }


    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("PATCH /api/pedidos/{}/estado → {}", id, estado);
        return ResponseEntity.ok(
                pedidoService.cambiarEstado(id, estado));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelar(
            @PathVariable Long id) {
        log.info("PATCH /api/pedidos/{}/cancelar", id);
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }


    @GetMapping("/fechas")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorFecha(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {
        log.info("GET /api/pedidos/fechas?inicio={}&fin={}",
                inicio, fin);
        return ResponseEntity.ok(
                pedidoService.buscarPorFecha(inicio, fin));
    }


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/pedidos/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-pedidos",
                "puerto", "8084"
        ));
    }
}
