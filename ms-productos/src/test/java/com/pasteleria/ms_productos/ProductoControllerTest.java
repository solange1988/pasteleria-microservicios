package com.pasteleria.ms_productos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasteleria.ms_productos.controller.ProductoController;
import com.pasteleria.ms_productos.dto.ProductoRequestDTO;
import com.pasteleria.ms_productos.dto.ProductoResponseDTO;
import com.pasteleria.ms_productos.service.ProductoService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private ObjectMapper objectMapper;
    private ProductoRequestDTO requestDTO;
    private ProductoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
        objectMapper = new ObjectMapper();

        requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Torta de chocolate");
        requestDTO.setDescripcion("Torta de chocolate con relleno");
        requestDTO.setPrecio(BigDecimal.valueOf(15000));
        requestDTO.setStock(10);
        requestDTO.setCategoriaId(1L);

        responseDTO = ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Torta de chocolate")
                .descripcion("Torta de chocolate con relleno")
                .precio(BigDecimal.valueOf(15000))
                .stock(10)
                .categoriaId(1L)
                .categoriaNombre("Tortas")
                .disponible(true)
                .build();
    }

    @Test
    @DisplayName("POST /api/productos devuelve 201 cuando es exitoso")
    void crear_devuelve201() throws Exception {
        when(productoService.crear(any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Torta de chocolate"));
    }

    @Test
    @DisplayName("GET /api/productos devuelve 200 con lista")
    void listarTodos_devuelve200() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/productos/disponibles devuelve 200")
    void listarDisponibles_devuelve200() throws Exception {
        when(productoService.listarDisponibles()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/productos/disponibles"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/productos/{id} devuelve 200 cuando existe")
    void buscarPorId_devuelve200() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/productos/categoria/{categoriaId} devuelve 200")
    void listarPorCategoria_devuelve200() throws Exception {
        when(productoService.listarPorCategoria(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/productos/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoriaId").value(1));
    }

    @Test
    @DisplayName("GET /api/productos/precio devuelve 200")
    void buscarPorRangoPrecio_devuelve200() throws Exception {
        when(productoService.buscarPorRangoPrecio(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/productos/precio")
                        .param("min", "1000")
                        .param("max", "20000"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/productos/{id} devuelve 200 cuando es exitoso")
    void actualizar_devuelve200() throws Exception {
        when(productoService.actualizar(eq(1L), any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Torta de chocolate"));
    }

    @Test
    @DisplayName("PATCH /api/productos/{id}/stock devuelve 200")
    void actualizarStock_devuelve200() throws Exception {
        when(productoService.actualizarStock(1L, 20)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/productos/1/stock")
                        .param("cantidad", "20"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/productos/{id}/desactivar devuelve 200")
    void desactivar_devuelve200() throws Exception {
        doNothing().when(productoService).desactivar(1L);

        mockMvc.perform(patch("/api/productos/1/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Producto desactivado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/productos/{id} devuelve 200")
    void eliminar_devuelve200() throws Exception {
        doNothing().when(productoService).eliminar(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Producto eliminado correctamente"));
    }

    @Test
    @DisplayName("GET /api/productos/health devuelve 200")
    void health_devuelve200() throws Exception {
        mockMvc.perform(get("/api/productos/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("activo"))
                .andExpect(jsonPath("$.servicio").value("ms-productos"));
    }
}
