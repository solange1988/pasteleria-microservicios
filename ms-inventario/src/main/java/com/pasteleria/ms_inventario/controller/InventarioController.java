package com.pasteleria.ms_inventario.controller;

import com.pasteleria.ms_inventario.dto.*;
import com.pasteleria.ms_inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
@Tag(name = "Inventario", description = "Endpoints para gestión de inventario e ingredientes")
public class InventarioController {

    private static final Logger log =
            LoggerFactory.getLogger(InventarioController.class);

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @Operation(summary = "Crear ingrediente", description = "Crea un nuevo ingrediente en el inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ingrediente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<IngredienteResponseDTO>> crear(
            @Valid @RequestBody IngredienteRequestDTO dto) {
        log.info("POST /api/inventario — nombre: {}", dto.getNombre());
        IngredienteResponseDTO creado = inventarioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(agregarLinks(creado));
    }

    @Operation(summary = "Listar todos los ingredientes")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes obtenida")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<IngredienteResponseDTO>>> listarTodos() {
        log.info("GET /api/inventario");
        List<EntityModel<IngredienteResponseDTO>> ingredientes = inventarioService.listarTodos()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<IngredienteResponseDTO>> recurso = CollectionModel.of(ingredientes);
        recurso.add(linkTo(methodOn(InventarioController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Listar ingredientes activos")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes activos obtenida")
    @GetMapping("/activos")
    public ResponseEntity<CollectionModel<EntityModel<IngredienteResponseDTO>>> listarActivos() {
        log.info("GET /api/inventario/activos");
        List<EntityModel<IngredienteResponseDTO>> ingredientes = inventarioService.listarActivos()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<IngredienteResponseDTO>> recurso = CollectionModel.of(ingredientes);
        recurso.add(linkTo(methodOn(InventarioController.class).listarActivos()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Buscar ingrediente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingrediente encontrado"),
            @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<IngredienteResponseDTO>> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/inventario/{}", id);
        IngredienteResponseDTO ingrediente = inventarioService.buscarPorId(id);
        return ResponseEntity.ok(agregarLinks(ingrediente));
    }

    @Operation(summary = "Buscar ingredientes por nombre")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes encontrados")
    @GetMapping("/buscar")
    public ResponseEntity<List<IngredienteResponseDTO>> buscarPorNombre(
            @RequestParam String nombre) {
        log.info("GET /api/inventario/buscar?nombre={}", nombre);
        return ResponseEntity.ok(inventarioService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Listar ingredientes con stock bajo")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes con stock bajo")
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<IngredienteResponseDTO>> stockBajo() {
        log.info("GET /api/inventario/stock-bajo");
        return ResponseEntity.ok(inventarioService.listarConStockBajo());
    }

    @Operation(summary = "Listar ingredientes sin stock")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes sin stock")
    @GetMapping("/sin-stock")
    public ResponseEntity<List<IngredienteResponseDTO>> sinStock() {
        log.info("GET /api/inventario/sin-stock");
        return ResponseEntity.ok(inventarioService.listarSinStock());
    }

    @Operation(summary = "Actualizar ingrediente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingrediente actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<IngredienteResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody IngredienteRequestDTO dto) {
        log.info("PUT /api/inventario/{}", id);
        IngredienteResponseDTO actualizado = inventarioService.actualizar(id, dto);
        return ResponseEntity.ok(agregarLinks(actualizado));
    }

    @Operation(summary = "Agregar stock a un ingrediente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock agregado correctamente"),
            @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    @PatchMapping("/{id}/agregar")
    public ResponseEntity<EntityModel<IngredienteResponseDTO>> agregarStock(
            @PathVariable Long id,
            @RequestParam Double cantidad) {
        log.info("PATCH /api/inventario/{}/agregar → {}", id, cantidad);
        IngredienteResponseDTO actualizado = inventarioService.agregarStock(id, cantidad);
        return ResponseEntity.ok(agregarLinks(actualizado));
    }

    @Operation(summary = "Reducir stock de un ingrediente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock reducido correctamente"),
            @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    @PatchMapping("/{id}/reducir")
    public ResponseEntity<EntityModel<IngredienteResponseDTO>> reducirStock(
            @PathVariable Long id,
            @RequestParam Double cantidad) {
        log.info("PATCH /api/inventario/{}/reducir → {}", id, cantidad);
        IngredienteResponseDTO actualizado = inventarioService.reducirStock(id, cantidad);
        return ResponseEntity.ok(agregarLinks(actualizado));
    }

    @Operation(summary = "Desactivar ingrediente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingrediente desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, String>> desactivar(
            @PathVariable Long id) {
        log.info("PATCH /api/inventario/{}/desactivar", id);
        inventarioService.desactivar(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Ingrediente desactivado correctamente",
                "id", String.valueOf(id)
        ));
    }

    @Operation(summary = "Eliminar ingrediente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingrediente eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(
            @PathVariable Long id) {
        log.info("DELETE /api/inventario/{}", id);
        inventarioService.eliminar(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Ingrediente eliminado correctamente",
                "id", String.valueOf(id)
        ));
    }

    @Operation(summary = "Health check", description = "Verifica que el microservicio esté activo")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/inventario/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-inventario",
                "puerto", "8085"
        ));
    }

    private EntityModel<IngredienteResponseDTO> agregarLinks(IngredienteResponseDTO ingrediente) {
        EntityModel<IngredienteResponseDTO> recurso = EntityModel.of(ingrediente);
        recurso.add(linkTo(methodOn(InventarioController.class).buscarPorId(ingrediente.getId())).withSelfRel());
        recurso.add(linkTo(methodOn(InventarioController.class).listarTodos()).withRel("todos-los-ingredientes"));
        recurso.add(linkTo(methodOn(InventarioController.class).eliminar(ingrediente.getId())).withRel("eliminar"));
        recurso.add(linkTo(methodOn(InventarioController.class).desactivar(ingrediente.getId())).withRel("desactivar"));
        return recurso;
    }
}