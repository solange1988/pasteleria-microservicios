package com.pasteleria.ms_productos.controller;

import com.pasteleria.ms_productos.dto.*;
import com.pasteleria.ms_productos.service.ProductoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private static final Logger log =
            LoggerFactory.getLogger(ProductoController.class);

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }


    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(
            @Valid @RequestBody ProductoRequestDTO dto) {
        log.info("POST /api/productos — nombre: {}", dto.getNombre());
        ProductoResponseDTO creado = productoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarTodos() {
        log.info("GET /api/productos");
        return ResponseEntity.ok(productoService.listarTodos());
    }


    @GetMapping("/disponibles")
    public ResponseEntity<List<ProductoResponseDTO>> listarDisponibles() {
        log.info("GET /api/productos/disponibles");
        return ResponseEntity.ok(productoService.listarDisponibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/productos/{}", id);
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }


    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponseDTO>> listarPorCategoria(
            @PathVariable Long categoriaId) {
        log.info("GET /api/productos/categoria/{}", categoriaId);
        return ResponseEntity.ok(productoService.listarPorCategoria(categoriaId));
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorNombre(
            @RequestParam String nombre) {
        log.info("GET /api/productos/buscar?nombre={}", nombre);
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }


    @GetMapping("/precio")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorRangoPrecio(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        log.info("GET /api/productos/precio?min={}&max={}", min, max);
        return ResponseEntity.ok(productoService.buscarPorRangoPrecio(min, max));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponseDTO>> stockBajo(
            @RequestParam(defaultValue = "5") Integer limite) {
        log.info("GET /api/productos/stock-bajo?limite={}", limite);
        return ResponseEntity.ok(productoService.productosConStockBajo(limite));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto) {
        log.info("PUT /api/productos/{}", id);
        return ResponseEntity.ok(productoService.actualizar(id, dto));
    }


    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoResponseDTO> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        log.info("PATCH /api/productos/{}/stock → {}", id, cantidad);
        return ResponseEntity.ok(productoService.actualizarStock(id, cantidad));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, String>> desactivar(
            @PathVariable Long id) {
        log.info("PATCH /api/productos/{}/desactivar", id);
        productoService.desactivar(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Producto desactivado correctamente",
                "id", String.valueOf(id)
        ));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(
            @PathVariable Long id) {
        log.info("DELETE /api/productos/{}", id);
        productoService.eliminar(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Producto eliminado correctamente",
                "id", String.valueOf(id)
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-productos",
                "puerto", "8083"
        ));
    }
}
