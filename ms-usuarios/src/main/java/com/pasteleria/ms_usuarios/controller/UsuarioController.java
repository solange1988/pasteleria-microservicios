package com.pasteleria.ms_usuarios.controller;


import com.pasteleria.ms_usuarios.dto.UsuarioRequestDTO;
import com.pasteleria.ms_usuarios.dto.UsuarioResponseDTO;
import com.pasteleria.ms_usuarios.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios")
public class UsuarioController {

    private static final Logger log =
            LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> crear(
            @Valid @RequestBody UsuarioRequestDTO dto) {
        log.info("POST /api/usuarios — email: {}", dto.getEmail());
        UsuarioResponseDTO creado = usuarioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(agregarLinks(creado));
    }

    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UsuarioResponseDTO>>> listarTodos() {
        log.info("GET /api/usuarios");
        List<EntityModel<UsuarioResponseDTO>> usuarios = usuarioService.listarTodos()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UsuarioResponseDTO>> recurso = CollectionModel.of(usuarios);
        recurso.add(linkTo(methodOn(UsuarioController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Listar usuarios activos")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios activos obtenida")
    @GetMapping("/activos")
    public ResponseEntity<CollectionModel<EntityModel<UsuarioResponseDTO>>> listarActivos() {
        log.info("GET /api/usuarios/activos");
        List<EntityModel<UsuarioResponseDTO>> usuarios = usuarioService.listarActivos()
                .stream()
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UsuarioResponseDTO>> recurso = CollectionModel.of(usuarios);
        recurso.add(linkTo(methodOn(UsuarioController.class).listarActivos()).withSelfRel());

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/usuarios/{}", id);
        UsuarioResponseDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(agregarLinks(usuario));
    }

    @Operation(summary = "Buscar usuario por email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> buscarPorEmail(
            @PathVariable String email) {
        log.info("GET /api/usuarios/email/{}", email);
        UsuarioResponseDTO usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(agregarLinks(usuario));
    }

    @Operation(summary = "Listar usuarios por rol")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios por rol obtenida")
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorRol(
            @PathVariable String rol) {
        log.info("GET /api/usuarios/rol/{}", rol);
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    @Operation(summary = "Buscar usuarios por nombre")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios encontrados")
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNombre(
            @RequestParam String nombre) {
        log.info("GET /api/usuarios/buscar?nombre={}", nombre);
        return ResponseEntity.ok(usuarioService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Actualizar usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        log.info("PUT /api/usuarios/{}", id);
        UsuarioResponseDTO actualizado = usuarioService.actualizar(id, dto);
        return ResponseEntity.ok(agregarLinks(actualizado));
    }

    @Operation(summary = "Desactivar usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, String>> desactivar(
            @PathVariable Long id) {
        log.info("PATCH /api/usuarios/{}/desactivar", id);
        usuarioService.desactivar(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Usuario desactivado correctamente",
                "id", String.valueOf(id)
        ));
    }

    @Operation(summary = "Eliminar usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(
            @PathVariable Long id) {
        log.info("DELETE /api/usuarios/{}", id);
        usuarioService.eliminar(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Usuario eliminado correctamente",
                "id", String.valueOf(id)
        ));
    }

    @Operation(summary = "Health check", description = "Verifica que el microservicio esté activo")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-usuarios",
                "puerto", "8081"
        ));
    }

    private EntityModel<UsuarioResponseDTO> agregarLinks(UsuarioResponseDTO usuario) {
        EntityModel<UsuarioResponseDTO> recurso = EntityModel.of(usuario);
        recurso.add(linkTo(methodOn(UsuarioController.class).buscarPorId(usuario.getId())).withSelfRel());
        recurso.add(linkTo(methodOn(UsuarioController.class).listarTodos()).withRel("todos-los-usuarios"));
        recurso.add(linkTo(methodOn(UsuarioController.class).eliminar(usuario.getId())).withRel("eliminar"));
        recurso.add(linkTo(methodOn(UsuarioController.class).desactivar(usuario.getId())).withRel("desactivar"));
        return recurso;
    }
}