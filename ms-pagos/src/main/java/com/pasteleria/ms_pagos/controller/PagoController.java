package com.pasteleria.ms_pagos.controller;


import com.pasteleria.ms_pagos.dto.PagoRequestDTO;
import com.pasteleria.ms_pagos.dto.PagoResponseDTO;
import com.pasteleria.ms_pagos.service.PagoService;
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
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
@Tag(name = "Pagos", description = "Endpoints para gestión de pagos")
public class PagoController {

    private static final Logger log =
            LoggerFactory.getLogger(PagoController.class);

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(summary = "Registrar pago", description = "Registra un nuevo pago en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<PagoResponseDTO>> registrar(
            @Valid @RequestBody PagoRequestDTO dto) {
        log.info("POST /api/pagos — pedido ID: {}", dto.getPedidoId());
        PagoResponseDTO creado = pagoService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(agregarLinks(creado));
    }

    @Operation(summary = "Listar todos los pagos")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PagoResponseDTO>>> listarTodos() {
        log.info("GET /api/pagos");
        List<EntityModel<PagoResponseDTO>> pagos = pagoService.listarTodos()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<PagoResponseDTO>> recurso = CollectionModel.of(pagos);
        recurso.add(linkTo(methodOn(PagoController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Buscar pago por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PagoResponseDTO>> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/pagos/{}", id);
        PagoResponseDTO pago = pagoService.buscarPorId(id);
        return ResponseEntity.ok(agregarLinks(pago));
    }

    @Operation(summary = "Buscar pago por pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EntityModel<PagoResponseDTO>> buscarPorPedido(
            @PathVariable Long pedidoId) {
        log.info("GET /api/pagos/pedido/{}", pedidoId);
        PagoResponseDTO pago = pagoService.buscarPorPedido(pedidoId);
        return ResponseEntity.ok(agregarLinks(pago));
    }

    @Operation(summary = "Listar pagos por estado")
    @ApiResponse(responseCode = "200", description = "Lista de pagos por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PagoResponseDTO>> listarPorEstado(
            @PathVariable String estado) {
        log.info("GET /api/pagos/estado/{}", estado);
        return ResponseEntity.ok(pagoService.listarPorEstado(estado));
    }

    @Operation(summary = "Aprobar pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago aprobado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<EntityModel<PagoResponseDTO>> aprobar(
            @PathVariable Long id) {
        log.info("PATCH /api/pagos/{}/aprobar", id);
        PagoResponseDTO aprobado = pagoService.aprobar(id);
        return ResponseEntity.ok(agregarLinks(aprobado));
    }

    @Operation(summary = "Rechazar pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago rechazado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<EntityModel<PagoResponseDTO>> rechazar(
            @PathVariable Long id) {
        log.info("PATCH /api/pagos/{}/rechazar", id);
        PagoResponseDTO rechazado = pagoService.rechazar(id);
        return ResponseEntity.ok(agregarLinks(rechazado));
    }

    @Operation(summary = "Anular pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago anulado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @PatchMapping("/{id}/anular")
    public ResponseEntity<EntityModel<PagoResponseDTO>> anular(
            @PathVariable Long id) {
        log.info("PATCH /api/pagos/{}/anular", id);
        PagoResponseDTO anulado = pagoService.anular(id);
        return ResponseEntity.ok(agregarLinks(anulado));
    }

    @Operation(summary = "Buscar pagos por rango de fechas")
    @ApiResponse(responseCode = "200", description = "Lista de pagos en el rango de fechas")
    @GetMapping("/fechas")
    public ResponseEntity<List<PagoResponseDTO>> buscarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/pagos/fechas?inicio={}&fin={}", inicio, fin);
        return ResponseEntity.ok(pagoService.buscarPorFecha(inicio, fin));
    }

    @Operation(summary = "Health check", description = "Verifica que el microservicio esté activo")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/pagos/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-pagos",
                "puerto", "8086"
        ));
    }

    private EntityModel<PagoResponseDTO> agregarLinks(PagoResponseDTO pago) {
        EntityModel<PagoResponseDTO> recurso = EntityModel.of(pago);
        recurso.add(linkTo(methodOn(PagoController.class).buscarPorId(pago.getId())).withSelfRel());
        recurso.add(linkTo(methodOn(PagoController.class).listarTodos()).withRel("todos-los-pagos"));
        return recurso;
    }
}



