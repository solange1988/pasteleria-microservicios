package com.pasteleria.ms_categorias.controller;

import com.pasteleria.ms_categorias.dto.*;
import com.pasteleria.ms_categorias.service.CategoriaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")

public class CategoriaController {

    private static final Logger log =
            LoggerFactory.getLogger(CategoriaController.class);

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }


    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crear(
            @Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("POST /api/categorias — nombre: {}", dto.getNombre());
        CategoriaResponseDTO creada = categoriaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }


    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        log.info("GET /api/categorias");
        return ResponseEntity.ok(categoriaService.listarTodas());
    }


    @GetMapping("/activas")
    public ResponseEntity<List<CategoriaResponseDTO>> listarActivas() {
        log.info("GET /api/categorias/activas");
        return ResponseEntity.ok(categoriaService.listarActivas());
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/categorias/{}", id);
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<CategoriaResponseDTO>> buscarPorNombre(
            @RequestParam String nombre) {
        log.info("GET /api/categorias/buscar?nombre={}", nombre);
        return ResponseEntity.ok(categoriaService.buscarPorNombre(nombre));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("PUT /api/categorias/{}", id);
        return ResponseEntity.ok(categoriaService.actualizar(id, dto));
    }


    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, String>> desactivar(
            @PathVariable Long id) {
        log.info("PATCH /api/categorias/{}/desactivar", id);
        categoriaService.desactivar(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Categoría desactivada correctamente",
                "id", String.valueOf(id)
        ));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(
            @PathVariable Long id) {
        log.info("DELETE /api/categorias/{}", id);
        categoriaService.eliminar(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Categoría eliminada correctamente",
                "id", String.valueOf(id)
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-categorias",
                "puerto", "8082"
        ));
    }
}
