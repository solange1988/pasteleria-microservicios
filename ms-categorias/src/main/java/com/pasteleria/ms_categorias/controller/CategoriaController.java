package com.pasteleria.ms_categorias.controller;

import com.pasteleria.ms_categorias.dto.*;
import com.pasteleria.ms_categorias.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
@Tag(name = "Categorías", description = "Endpoints para gestión de categorías")
public class CategoriaController {

    private static final Logger log =
            LoggerFactory.getLogger(CategoriaController.class);

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<CategoriaResponseDTO>> crear(
            @Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("POST /api/categorias — nombre: {}", dto.getNombre());
        CategoriaResponseDTO creada = categoriaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(agregarLinks(creada));
    }

    @Operation(summary = "Listar todas las categorías")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CategoriaResponseDTO>>> listarTodas() {
        log.info("GET /api/categorias");
        List<EntityModel<CategoriaResponseDTO>> categorias = categoriaService.listarTodas()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<CategoriaResponseDTO>> recurso = CollectionModel.of(categorias);
        recurso.add(linkTo(methodOn(CategoriaController.class).listarTodas()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Listar categorías activas")
    @ApiResponse(responseCode = "200", description = "Lista de categorías activas obtenida")
    @GetMapping("/activas")
    public ResponseEntity<CollectionModel<EntityModel<CategoriaResponseDTO>>> listarActivas() {
        log.info("GET /api/categorias/activas");
        List<EntityModel<CategoriaResponseDTO>> categorias = categoriaService.listarActivas()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<CategoriaResponseDTO>> recurso = CollectionModel.of(categorias);
        recurso.add(linkTo(methodOn(CategoriaController.class).listarActivas()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Buscar categoría por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaResponseDTO>> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/categorias/{}", id);
        CategoriaResponseDTO categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(agregarLinks(categoria));
    }

    @Operation(summary = "Buscar categorías por nombre")
    @ApiResponse(responseCode = "200", description = "Lista de categorías encontradas")
    @GetMapping("/buscar")
    public ResponseEntity<List<CategoriaResponseDTO>> buscarPorNombre(
            @RequestParam String nombre) {
        log.info("GET /api/categorias/buscar?nombre={}", nombre);
        return ResponseEntity.ok(categoriaService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Actualizar categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO dto) {
        log.info("PUT /api/categorias/{}", id);
        CategoriaResponseDTO actualizada = categoriaService.actualizar(id, dto);
        return ResponseEntity.ok(agregarLinks(actualizada));
    }

    @Operation(summary = "Desactivar categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría desactivada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
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

    @Operation(summary = "Eliminar categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
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

    @Operation(summary = "Health check", description = "Verifica que el microservicio esté activo")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-categorias",
                "puerto", "8082"
        ));
    }

    private EntityModel<CategoriaResponseDTO> agregarLinks(CategoriaResponseDTO categoria) {
        EntityModel<CategoriaResponseDTO> recurso = EntityModel.of(categoria);
        recurso.add(linkTo(methodOn(CategoriaController.class).buscarPorId(categoria.getId())).withSelfRel());
        recurso.add(linkTo(methodOn(CategoriaController.class).listarTodas()).withRel("todas-las-categorias"));
        recurso.add(linkTo(methodOn(CategoriaController.class).eliminar(categoria.getId())).withRel("eliminar"));
        recurso.add(linkTo(methodOn(CategoriaController.class).desactivar(categoria.getId())).withRel("desactivar"));
        return recurso;
    }
}
