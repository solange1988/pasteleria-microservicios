package com.pasteleria.ms_auth.controller;

import com.pasteleria.ms_auth.service.AuthService;
import com.pasteleria.ms_auth.dto.AuthResponseDTO;
import com.pasteleria.ms_auth.dto.LoginRequestDTO;
import com.pasteleria.ms_auth.dto.RegisterRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Endpoints para registro y login de usuarios")
public class AuthController {

    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    @PostMapping("/register")
    public ResponseEntity<EntityModel<AuthResponseDTO>> registrar(
            @Valid @RequestBody RegisterRequestDTO dto) {
        log.info("POST /api/auth/register — email: {}", dto.getEmail());
        AuthResponseDTO response = authService.registrar(dto);

        EntityModel<AuthResponseDTO> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(AuthController.class).registrar(dto)).withSelfRel());
        recurso.add(linkTo(methodOn(AuthController.class).login(null)).withRel("login"));
        recurso.add(linkTo(methodOn(AuthController.class).health()).withRel("health"));

        return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
    }

    @Operation(summary = "Login de usuario", description = "Autentica un usuario y retorna sus datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public ResponseEntity<EntityModel<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        log.info("POST /api/auth/login — email: {}", dto.getEmail());
        AuthResponseDTO response = authService.login(dto);

        EntityModel<AuthResponseDTO> recurso = EntityModel.of(response);
        recurso.add(linkTo(methodOn(AuthController.class).login(dto)).withSelfRel());
        recurso.add(linkTo(methodOn(AuthController.class).health()).withRel("health"));

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Health check", description = "Verifica que el microservicio esté activo")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    @GetMapping("/health")
    public ResponseEntity<EntityModel<Map<String, String>>> health() {
        log.info("GET /api/auth/health");
        Map<String, String> status = Map.of(
                "status", "activo",
                "servicio", "ms-auth",
                "puerto", "8080"
        );

        EntityModel<Map<String, String>> recurso = EntityModel.of(status);
        recurso.add(linkTo(methodOn(AuthController.class).health()).withSelfRel());
        recurso.add(linkTo(methodOn(AuthController.class).registrar(null)).withRel("registrar"));
        recurso.add(linkTo(methodOn(AuthController.class).login(null)).withRel("login"));

        return ResponseEntity.ok(recurso);
    }
}