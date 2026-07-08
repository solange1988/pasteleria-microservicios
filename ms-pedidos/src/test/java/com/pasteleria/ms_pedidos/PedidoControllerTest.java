package com.pasteleria.ms_pedidos;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pasteleria.ms_pedidos.controller.PedidoController;
import com.pasteleria.ms_pedidos.dto.DetallePedidoDTO;
import com.pasteleria.ms_pedidos.dto.PedidoRequestDTO;
import com.pasteleria.ms_pedidos.dto.PedidoResponseDTO;
import com.pasteleria.ms_pedidos.service.PedidoService;
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
class PedidoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private ObjectMapper objectMapper;
    private PedidoRequestDTO requestDTO;
    private PedidoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        DetallePedidoDTO detalleDTO = new DetallePedidoDTO();
        detalleDTO.setProductoId(1L);
        detalleDTO.setCantidad(2);

        requestDTO = new PedidoRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setNotas("Sin azúcar");
        requestDTO.setDetalles(List.of(detalleDTO));

        responseDTO = PedidoResponseDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .usuarioNombre("Juan Perez")
                .estado("PENDIENTE")
                .total(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    @DisplayName("POST /api/pedidos devuelve 201 cuando es exitoso")
    void crear_devuelve201() throws Exception {
        when(pedidoService.crear(any(PedidoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioNombre").value("Juan Perez"));
    }

    @Test
    @DisplayName("GET /api/pedidos devuelve 200 con lista")
    void listarTodos_devuelve200() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/pedidos/{id} devuelve 200 cuando existe")
    void buscarPorId_devuelve200() throws Exception {
        when(pedidoService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/pedidos/usuario/{usuarioId} devuelve 200 con lista")
    void listarPorUsuario_devuelve200() throws Exception {
        when(pedidoService.listarPorUsuario(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/pedidos/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(1));
    }

    @Test
    @DisplayName("GET /api/pedidos/estado/{estado} devuelve 200 con lista")
    void listarPorEstado_devuelve200() throws Exception {
        when(pedidoService.listarPorEstado("PENDIENTE")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/pedidos/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("PATCH /api/pedidos/{id}/estado devuelve 200")
    void cambiarEstado_devuelve200() throws Exception {
        when(pedidoService.cambiarEstado(eq(1L), eq("CONFIRMADO"))).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/pedidos/1/estado")
                        .param("estado", "CONFIRMADO"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/pedidos/{id}/cancelar devuelve 200")
    void cancelar_devuelve200() throws Exception {
        when(pedidoService.cancelar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/pedidos/1/cancelar"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/pedidos/health devuelve 200")
    void health_devuelve200() throws Exception {
        mockMvc.perform(get("/api/pedidos/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("activo"))
                .andExpect(jsonPath("$.servicio").value("ms-pedidos"));
    }
}