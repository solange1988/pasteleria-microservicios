package com.pasteleria.ms_pagos.controller;


import com.pasteleria.ms_pagos.dto.PagoRequestDTO;
import com.pasteleria.ms_pagos.dto.PagoResponseDTO;
import com.pasteleria.ms_pagos.service.PagoService;
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
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")

public class PagoController {

    private static final Logger log =
            LoggerFactory.getLogger(PagoController.class);

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }


    @PostMapping
    public ResponseEntity<PagoResponseDTO> registrar(
            @Valid @RequestBody PagoRequestDTO dto) {
        log.info("POST /api/pagos — pedido ID: {}",
                dto.getPedidoId());
        PagoResponseDTO creado = pagoService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }


    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        log.info("GET /api/pagos");
        return ResponseEntity.ok(pagoService.listarTodos());
    }


    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/pagos/{}", id);
        return ResponseEntity.ok(pagoService.buscarPorId(id));
    }


    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponseDTO> buscarPorPedido(
            @PathVariable Long pedidoId) {
        log.info("GET /api/pagos/pedido/{}", pedidoId);
        return ResponseEntity.ok(pagoService.buscarPorPedido(pedidoId));
    }


    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PagoResponseDTO>> listarPorEstado(
            @PathVariable String estado) {
        log.info("GET /api/pagos/estado/{}", estado);
        return ResponseEntity.ok(pagoService.listarPorEstado(estado));
    }


    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<PagoResponseDTO> aprobar(
            @PathVariable Long id) {
        log.info("PATCH /api/pagos/{}/aprobar", id);
        return ResponseEntity.ok(pagoService.aprobar(id));
    }


    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<PagoResponseDTO> rechazar(
            @PathVariable Long id) {
        log.info("PATCH /api/pagos/{}/rechazar", id);
        return ResponseEntity.ok(pagoService.rechazar(id));
    }


    @PatchMapping("/{id}/anular")
    public ResponseEntity<PagoResponseDTO> anular(
            @PathVariable Long id) {
        log.info("PATCH /api/pagos/{}/anular", id);
        return ResponseEntity.ok(pagoService.anular(id));
    }


    @GetMapping("/fechas")
    public ResponseEntity<List<PagoResponseDTO>> buscarPorFecha(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {
        log.info("GET /api/pagos/fechas?inicio={}&fin={}",
                inicio, fin);
        return ResponseEntity.ok(
                pagoService.buscarPorFecha(inicio, fin));
    }


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/pagos/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-pagos",
                "puerto", "8086"
        ));
    }
}

