package com.pasteleria.ms_categorias;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasteleria.ms_categorias.controller.CategoriaController;
import com.pasteleria.ms_categorias.dto.CategoriaRequestDTO;
import com.pasteleria.ms_categorias.dto.CategoriaResponseDTO;
import com.pasteleria.ms_categorias.service.CategoriaService;
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
public class CategoriaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    private ObjectMapper objectMapper;
    private CategoriaRequestDTO requestDTO;
    private CategoriaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController).build();
        objectMapper = new ObjectMapper();

        requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("Tortas");
        requestDTO.setDescripcion("Categoría de tortas");

        responseDTO = CategoriaResponseDTO.builder()
                .id(1L)
                .nombre("Tortas")
                .descripcion("Categoría de tortas")
                .activa(true)
                .build();
    }

    @Test
    @DisplayName("POST /api/categorias devuelve 201 cuando es exitoso")
    void crear_devuelve201() throws Exception {
        when(categoriaService.crear(any(CategoriaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Tortas"));
    }

    @Test
    @DisplayName("GET /api/categorias devuelve 200 con lista")
    void listarTodas_devuelve200() throws Exception {
        when(categoriaService.listarTodas()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/categorias/activas devuelve 200 con lista")
    void listarActivas_devuelve200() throws Exception {
        when(categoriaService.listarActivas()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/categorias/activas"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/categorias/{id} devuelve 200 cuando existe")
    void buscarPorId_devuelve200() throws Exception {
        when(categoriaService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/categorias/{id} devuelve 200 cuando es exitoso")
    void actualizar_devuelve200() throws Exception {
        when(categoriaService.actualizar(eq(1L), any(CategoriaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Tortas"));
    }

    @Test
    @DisplayName("PATCH /api/categorias/{id}/desactivar devuelve 200")
    void desactivar_devuelve200() throws Exception {
        doNothing().when(categoriaService).desactivar(1L);

        mockMvc.perform(patch("/api/categorias/1/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Categoría desactivada correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} devuelve 200")
    void eliminar_devuelve200() throws Exception {
        doNothing().when(categoriaService).eliminar(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Categoría eliminada correctamente"));
    }

    @Test
    @DisplayName("GET /api/categorias/health devuelve 200")
    void health_devuelve200() throws Exception {
        mockMvc.perform(get("/api/categorias/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("activo"))
                .andExpect(jsonPath("$.servicio").value("ms-categorias"));
    }

}
