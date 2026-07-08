package com.pasteleria.ms_usuarios;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasteleria.ms_usuarios.controller.UsuarioController;
import com.pasteleria.ms_usuarios.dto.UsuarioRequestDTO;
import com.pasteleria.ms_usuarios.dto.UsuarioResponseDTO;
import com.pasteleria.ms_usuarios.service.UsuarioService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private ObjectMapper objectMapper;
    private UsuarioRequestDTO requestDTO;
    private UsuarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        objectMapper = new ObjectMapper();

        requestDTO = new UsuarioRequestDTO();
        requestDTO.setNombre("Juan");
        requestDTO.setApellido("Perez");
        requestDTO.setEmail("juan@test.com");
        requestDTO.setTelefono("123456789");
        requestDTO.setDireccion("Calle 123");
        requestDTO.setRol("CLIENTE");

        responseDTO = UsuarioResponseDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .rol("CLIENTE")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("POST /api/usuarios devuelve 201 cuando es exitoso")
    void crear_devuelve201() throws Exception {
        when(usuarioService.crear(any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("GET /api/usuarios devuelve 200 con lista")
    void listarTodos_devuelve200() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/usuarios/activos devuelve 200 con lista")
    void listarActivos_devuelve200() throws Exception {
        when(usuarioService.listarActivos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/usuarios/activos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} devuelve 200 cuando existe")
    void buscarPorId_devuelve200() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/usuarios/email/{email} devuelve 200 cuando existe")
    void buscarPorEmail_devuelve200() throws Exception {
        when(usuarioService.buscarPorEmail("juan@test.com")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/usuarios/email/juan@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("GET /api/usuarios/rol/{rol} devuelve 200 con lista")
    void listarPorRol_devuelve200() throws Exception {
        when(usuarioService.listarPorRol("CLIENTE")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/usuarios/rol/CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rol").value("CLIENTE"));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} devuelve 200 cuando es exitoso")
    void actualizar_devuelve200() throws Exception {
        when(usuarioService.actualizar(eq(1L), any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("PATCH /api/usuarios/{id}/desactivar devuelve 200")
    void desactivar_devuelve200() throws Exception {
        doNothing().when(usuarioService).desactivar(1L);

        mockMvc.perform(patch("/api/usuarios/1/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario desactivado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} devuelve 200")
    void eliminar_devuelve200() throws Exception {
        doNothing().when(usuarioService).eliminar(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario eliminado correctamente"));
    }

    @Test
    @DisplayName("GET /api/usuarios/health devuelve 200")
    void health_devuelve200() throws Exception {
        mockMvc.perform(get("/api/usuarios/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("activo"))
                .andExpect(jsonPath("$.servicio").value("ms-usuarios"));
    }
}