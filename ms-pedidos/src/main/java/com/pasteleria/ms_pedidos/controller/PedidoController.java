package com.pasteleria.ms_pedidos.controller;

import com.pasteleria.ms_pedidos.dto.*;
import com.pasteleria.ms_pedidos.service.PedidoService;
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
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Endpoints para gestión de pedidos")
public class PedidoController {

    private static final Logger log =
            LoggerFactory.getLogger(PedidoController.class);

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Crear pedido", description = "Crea un nuevo pedido en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<PedidoResponseDTO>> crear(
            @Valid @RequestBody PedidoRequestDTO dto) {
        log.info("POST /api/pedidos — usuario ID: {}", dto.getUsuarioId());
        PedidoResponseDTO creado = pedidoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(agregarLinks(creado));
    }

    @Operation(summary = "Listar todos los pedidos")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PedidoResponseDTO>>> listarTodos() {
        log.info("GET /api/pedidos");
        List<EntityModel<PedidoResponseDTO>> pedidos = pedidoService.listarTodos()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<PedidoResponseDTO>> recurso = CollectionModel.of(pedidos);
        recurso.add(linkTo(methodOn(PedidoController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PedidoResponseDTO>> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/pedidos/{}", id);
        PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(agregarLinks(pedido));
    }

    @Operation(summary = "Listar pedidos por usuario")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        log.info("GET /api/pedidos/usuario/{}", usuarioId);
        return ResponseEntity.ok(pedidoService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Listar pedidos por estado")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorEstado(
            @PathVariable String estado) {
        log.info("GET /api/pedidos/estado/{}", estado);
        return ResponseEntity.ok(pedidoService.listarPorEstado(estado));
    }

    @Operation(summary = "Cambiar estado de pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado cambiado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EntityModel<PedidoResponseDTO>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("PATCH /api/pedidos/{}/estado → {}", id, estado);
        PedidoResponseDTO actualizado = pedidoService.cambiarEstado(id, estado);
        return ResponseEntity.ok(agregarLinks(actualizado));
    }

    @Operation(summary = "Cancelar pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado correctamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<EntityModel<PedidoResponseDTO>> cancelar(
            @PathVariable Long id) {
        log.info("PATCH /api/pedidos/{}/cancelar", id);
        PedidoResponseDTO cancelado = pedidoService.cancelar(id);
        return ResponseEntity.ok(agregarLinks(cancelado));
    }

    @Operation(summary = "Buscar pedidos por rango de fechas")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos en el rango de fechas")
    @GetMapping("/fechas")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        log.info("GET /api/pedidos/fechas?inicio={}&fin={}", inicio, fin);
        return ResponseEntity.ok(pedidoService.buscarPorFecha(inicio, fin));
    }

    @Operation(summary = "Health check", description = "Verifica que el microservicio esté activo")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/pedidos/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-pedidos",
                "puerto", "8084"
        ));
    }

    private EntityModel<PedidoResponseDTO> agregarLinks(PedidoResponseDTO pedido) {
        EntityModel<PedidoResponseDTO> recurso = EntityModel.of(pedido);
        recurso.add(linkTo(methodOn(PedidoController.class).buscarPorId(pedido.getId())).withSelfRel());
        recurso.add(linkTo(methodOn(PedidoController.class).listarTodos()).withRel("todos-los-pedidos"));
        recurso.add(linkTo(methodOn(PedidoController.class).cancelar(pedido.getId())).withRel("cancelar"));
        return recurso;
    }
}

