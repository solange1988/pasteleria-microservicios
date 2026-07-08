package com.pasteleria.ms_productos.controller;


import com.pasteleria.ms_productos.dto.*;
import com.pasteleria.ms_productos.service.ProductoService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@Tag(name = "Productos", description = "Endpoints para gestión de productos")
public class ProductoController {

    private static final Logger log =
            LoggerFactory.getLogger(ProductoController.class);

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Crear producto", description = "Crea un nuevo producto en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<ProductoResponseDTO>> crear(
            @Valid @RequestBody ProductoRequestDTO dto) {
        log.info("POST /api/productos — nombre: {}", dto.getNombre());
        ProductoResponseDTO creado = productoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(agregarLinks(creado));
    }

    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponseDTO>>> listarTodos() {
        log.info("GET /api/productos");
        List<EntityModel<ProductoResponseDTO>> productos = productoService.listarTodos()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<ProductoResponseDTO>> recurso = CollectionModel.of(productos);
        recurso.add(linkTo(methodOn(ProductoController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Listar productos disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de productos disponibles")
    @GetMapping("/disponibles")
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponseDTO>>> listarDisponibles() {
        log.info("GET /api/productos/disponibles");
        List<EntityModel<ProductoResponseDTO>> productos = productoService.listarDisponibles()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<ProductoResponseDTO>> recurso = CollectionModel.of(productos);
        recurso.add(linkTo(methodOn(ProductoController.class).listarDisponibles()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Buscar producto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/productos/{}", id);
        ProductoResponseDTO producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(agregarLinks(producto));
    }

    @Operation(summary = "Listar productos por categoría")
    @ApiResponse(responseCode = "200", description = "Lista de productos por categoría")
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponseDTO>> listarPorCategoria(
            @PathVariable Long categoriaId) {
        log.info("GET /api/productos/categoria/{}", categoriaId);
        return ResponseEntity.ok(productoService.listarPorCategoria(categoriaId));
    }

    @Operation(summary = "Buscar productos por nombre")
    @ApiResponse(responseCode = "200", description = "Lista de productos encontrados")
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorNombre(
            @RequestParam String nombre) {
        log.info("GET /api/productos/buscar?nombre={}", nombre);
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Buscar productos por rango de precio")
    @ApiResponse(responseCode = "200", description = "Lista de productos en el rango de precio")
    @GetMapping("/precio")
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorRangoPrecio(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        log.info("GET /api/productos/precio?min={}&max={}", min, max);
        return ResponseEntity.ok(productoService.buscarPorRangoPrecio(min, max));
    }

    @Operation(summary = "Listar productos con stock bajo")
    @ApiResponse(responseCode = "200", description = "Lista de productos con stock bajo")
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponseDTO>> stockBajo(
            @RequestParam(defaultValue = "5") Integer limite) {
        log.info("GET /api/productos/stock-bajo?limite={}", limite);
        return ResponseEntity.ok(productoService.productosConStockBajo(limite));
    }

    @Operation(summary = "Actualizar producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto) {
        log.info("PUT /api/productos/{}", id);
        ProductoResponseDTO actualizado = productoService.actualizar(id, dto);
        return ResponseEntity.ok(agregarLinks(actualizado));
    }

    @Operation(summary = "Actualizar stock de producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> actualizarStock(
            @PathVariable Long id,
            @RequestParam Integer cantidad) {
        log.info("PATCH /api/productos/{}/stock → {}", id, cantidad);
        ProductoResponseDTO actualizado = productoService.actualizarStock(id, cantidad);
        return ResponseEntity.ok(agregarLinks(actualizado));
    }

    @Operation(summary = "Desactivar producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
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

    @Operation(summary = "Eliminar producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
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

    @Operation(summary = "Health check", description = "Verifica que el microservicio esté activo")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-productos",
                "puerto", "8083"
        ));
    }

    private EntityModel<ProductoResponseDTO> agregarLinks(ProductoResponseDTO producto) {
        EntityModel<ProductoResponseDTO> recurso = EntityModel.of(producto);
        recurso.add(linkTo(methodOn(ProductoController.class).buscarPorId(producto.getId())).withSelfRel());
        recurso.add(linkTo(methodOn(ProductoController.class).listarTodos()).withRel("todos-los-productos"));
        recurso.add(linkTo(methodOn(ProductoController.class).eliminar(producto.getId())).withRel("eliminar"));
        recurso.add(linkTo(methodOn(ProductoController.class).desactivar(producto.getId())).withRel("desactivar"));
        return recurso;
    }
}
