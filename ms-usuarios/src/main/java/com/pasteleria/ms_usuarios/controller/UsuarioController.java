package com.pasteleria.ms_usuarios.controller;


import com.pasteleria.ms_usuarios.dto.UsuarioRequestDTO;
import com.pasteleria.ms_usuarios.dto.UsuarioResponseDTO;
import com.pasteleria.ms_usuarios.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private static final Logger log =
            LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(
            @Valid @RequestBody UsuarioRequestDTO dto) {
        log.info("POST /api/usuarios — email: {}", dto.getEmail());
        UsuarioResponseDTO creado = usuarioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }


    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        log.info("GET /api/usuarios");
        return ResponseEntity.ok(usuarioService.listarTodos());
    }


    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponseDTO>> listarActivos() {
        log.info("GET /api/usuarios/activos");
        return ResponseEntity.ok(usuarioService.listarActivos());
    }


    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @PathVariable Long id) {
        log.info("GET /api/usuarios/{}", id);
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorEmail(
            @PathVariable String email) {
        log.info("GET /api/usuarios/email/{}", email);
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }


    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorRol(
            @PathVariable String rol) {
        log.info("GET /api/usuarios/rol/{}", rol);
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNombre(
            @RequestParam String nombre) {
        log.info("GET /api/usuarios/buscar?nombre={}", nombre);
        return ResponseEntity.ok(usuarioService.buscarPorNombre(nombre));
    }


    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        log.info("PUT /api/usuarios/{}", id);
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }


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


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-usuarios",
                "puerto", "8081"
        ));
    }


}
