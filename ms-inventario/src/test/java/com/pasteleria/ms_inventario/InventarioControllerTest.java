package com.pasteleria.ms_inventario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasteleria.ms_inventario.controller.InventarioController;
import com.pasteleria.ms_inventario.dto.IngredienteRequestDTO;
import com.pasteleria.ms_inventario.dto.IngredienteResponseDTO;
import com.pasteleria.ms_inventario.service.InventarioService;
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
class InventarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private InventarioController inventarioController;

    private ObjectMapper objectMapper;
    private IngredienteRequestDTO requestDTO;
    private IngredienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventarioController).build();
        objectMapper = new ObjectMapper();

        requestDTO = new IngredienteRequestDTO();
        requestDTO.setNombre("Harina");
        requestDTO.setDescripcion("Harina de trigo");
        requestDTO.setStockActual(100.0);
        requestDTO.setStockMinimo(10.0);
        requestDTO.setUnidadMedida("kg");

        responseDTO = IngredienteResponseDTO.builder()
                .id(1L)
                .nombre("Harina")
                .descripcion("Harina de trigo")
                .stockActual(100.0)
                .stockMinimo(10.0)
                .unidadMedida("kg")
                .activo(true)
                .stockBajo(false)
                .build();
    }

    @Test
    @DisplayName("POST /api/inventario devuelve 201 cuando es exitoso")
    void crear_devuelve201() throws Exception {
        when(inventarioService.crear(any(IngredienteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Harina"));
    }

    @Test
    @DisplayName("GET /api/inventario devuelve 200 con lista")
    void listarTodos_devuelve200() throws Exception {
        when(inventarioService.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/inventario/activos devuelve 200 con lista")
    void listarActivos_devuelve200() throws Exception {
        when(inventarioService.listarActivos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/inventario/activos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/inventario/{id} devuelve 200 cuando existe")
    void buscarPorId_devuelve200() throws Exception {
        when(inventarioService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/inventario/stock-bajo devuelve 200")
    void stockBajo_devuelve200() throws Exception {
        when(inventarioService.listarConStockBajo()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/inventario/stock-bajo"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/inventario/sin-stock devuelve 200")
    void sinStock_devuelve200() throws Exception {
        when(inventarioService.listarSinStock()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/inventario/sin-stock"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/inventario/{id} devuelve 200 cuando es exitoso")
    void actualizar_devuelve200() throws Exception {
        when(inventarioService.actualizar(eq(1L), any(IngredienteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Harina"));
    }

    @Test
    @DisplayName("PATCH /api/inventario/{id}/agregar devuelve 200")
    void agregarStock_devuelve200() throws Exception {
        when(inventarioService.agregarStock(1L, 50.0)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/inventario/1/agregar")
                        .param("cantidad", "50.0"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/inventario/{id}/reducir devuelve 200")
    void reducirStock_devuelve200() throws Exception {
        when(inventarioService.reducirStock(1L, 20.0)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/inventario/1/reducir")
                        .param("cantidad", "20.0"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/inventario/{id}/desactivar devuelve 200")
    void desactivar_devuelve200() throws Exception {
        doNothing().when(inventarioService).desactivar(1L);

        mockMvc.perform(patch("/api/inventario/1/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Ingrediente desactivado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/inventario/{id} devuelve 200")
    void eliminar_devuelve200() throws Exception {
        doNothing().when(inventarioService).eliminar(1L);

        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Ingrediente eliminado correctamente"));
    }

    @Test
    @DisplayName("GET /api/inventario/health devuelve 200")
    void health_devuelve200() throws Exception {
        mockMvc.perform(get("/api/inventario/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("activo"))
                .andExpect(jsonPath("$.servicio").value("ms-inventario"));
    }
}




