package com.pasteleria.ms_auth.controller;


import com.pasteleria.ms_auth.service.AuthService;
import com.pasteleria.ms_auth.dto.AuthResponseDTO;
import com.pasteleria.ms_auth.dto.LoginRequestDTO;
import com.pasteleria.ms_auth.dto.RegisterRequestDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class  AuthController {

    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(
            @Valid @RequestBody RegisterRequestDTO dto) {
        log.info("POST /api/auth/register — email: {}", dto.getEmail());
        AuthResponseDTO response = authService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        log.info("POST /api/auth/login — email: {}", dto.getEmail());
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/auth/health");
        return ResponseEntity.ok(Map.of(
                "status", "activo",
                "servicio", "ms-auth",
                "puerto", "8080"
        ));
    }
}
