package com.pasteleria.ms_auth;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasteleria.ms_auth.controller.AuthController;
import com.pasteleria.ms_auth.dto.AuthResponseDTO;
import com.pasteleria.ms_auth.dto.LoginRequestDTO;
import com.pasteleria.ms_auth.dto.RegisterRequestDTO;
import com.pasteleria.ms_auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)

public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginDTO;
    private AuthResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        registerDTO = new RegisterRequestDTO();
        registerDTO.setNombre("Juan Perez");
        registerDTO.setEmail("juan@test.com");
        registerDTO.setPassword("123456");
        registerDTO.setRol("CLIENTE");

        loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("juan@test.com");
        loginDTO.setPassword("123456");

        responseDTO = AuthResponseDTO.builder()
                .id(1L)
                .nombre("Juan Perez")
                .email("juan@test.com")
                .rol("CLIENTE")
                .activo(true)
                .mensaje("Registro exitoso")
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register devuelve 201 cuando es exitoso")
    void registrar_devuelve201() throws Exception {
        when(authService.registrar(any(RegisterRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login devuelve 200 cuando es exitoso")
    void login_devuelve200() throws Exception {
        responseDTO.setMensaje("Login exitoso");
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("GET /api/auth/health devuelve 200 y estado activo")
    void health_devuelve200() throws Exception {
        mockMvc.perform(get("/api/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("activo"))
                .andExpect(jsonPath("$.servicio").value("ms-auth"));
    }

    @Test
    @DisplayName("POST /api/auth/register con datos inválidos devuelve 400")
    void registrar_datosInvalidos_devuelve400() throws Exception {
        RegisterRequestDTO dtoInvalido = new RegisterRequestDTO();
        dtoInvalido.setEmail("no-es-un-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }
}

