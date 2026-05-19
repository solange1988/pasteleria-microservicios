package com.pasteleria.ms_inventario.controller;
import com.pasteleria.ms_inventario.dto.*;
import com.pasteleria.ms_inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")

public class InventarioController {

    private static final Logger log =
            LoggerFactory.getLogger(InventarioController.class);

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }


    @PostMapping
    public ResponseEntity<IngredienteResponseDTO> crear(
            @Valid @RequestBody IngredienteRequestDTO dto) {
        log.info("POST /api/inventario — nombre: {}", dto.getNombre());
        IngredienteResponseDTO creado = inventarioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }


    @GetMapping
    public ResponseEntity<List<IngredienteResponseDTO>> listarTodos() {
        log.info("GET /api/inventario");
        return ResponseEntity.ok(inventarioService.listarTodos());
    }


    @GetMapping("/activos")
    public ResponseEntity<List<IngredienteResponseDTO>> listarActivos() {
        log.info("GET /api/inventario/activos");
        return ResponseEntity.ok(inventarioService.listarActivos());
    }


    @GetMapping("/{id}")
    public ResponseEntity<IngredienteResponseDTO> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/inventario/{}", id);
        return ResponseEntity.ok(inventarioService.buscarPorId(id));
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<IngredienteResponseDTO>> buscarPorNombre(
            @RequestParam String nombre) {
        log.info("GET /api/inventario/buscar?nombre={}", nombre);
        return ResponseEntity.ok(
                inventarioService.buscarPorNombre(nombre));
    }


    @GetMapping("/stock-bajo")
    public ResponseEntity<List<IngredienteResponseDTO>> stockBajo() {
        log.info("GET /api/inventario/stock-bajo");
        return ResponseEntity.ok(
                inventarioService.listarConStockBajo());
    }


    @GetMapping("/sin-stock")
    public ResponseEntity<List<IngredienteResponseDTO>> sinStock() {
        log.info("GET /api/inventario/sin-stock");
        return ResponseEntity.ok(inventarioService.listarSinStock());
    }


    @PutMapping("/{id}")
    public ResponseEntity<IngredienteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody IngredienteRequestDTO dto) {
        log.info("PUT /api/inventario/{}", id);
        return ResponseEntity.ok(
                inventarioService.actualizar(id, dto));
    }


    @PatchMapping("/{id}/agregar")
    public ResponseEntity<IngredienteResponseDTO> agregarStock(
            @PathVariable Long id,
            @RequestParam Double cantidad) {
        log.info("PATCH /api/inventario/{}/agregar → {}",
                id, cantidad);
        return ResponseEntity.ok(
                inventarioService.agregarStock(id, cantidad));
    }


    @PatchMapping("/{id}/reducir")
    public ResponseEntity<IngredienteResponseDTO> reducirStock(
            @PathVariable Long id,
            @RequestParam Double cantidad) {
        log.info("PATCH /api/inventario/{}/reducir → {}",
                id, cantidad);
        return ResponseEntity.ok(
                inventarioService.reducirStock(id, cantidad));
    }


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


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/inventario/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-inventario",
                "puerto", "8085"
        ));
    }
}
