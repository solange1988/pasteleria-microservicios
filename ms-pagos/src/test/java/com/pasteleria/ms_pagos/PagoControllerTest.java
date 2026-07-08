package com.pasteleria.ms_pagos;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasteleria.ms_pagos.controller.PagoController;
import com.pasteleria.ms_pagos.dto.PagoRequestDTO;
import com.pasteleria.ms_pagos.dto.PagoResponseDTO;
import com.pasteleria.ms_pagos.service.PagoService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController pagoController;

    private ObjectMapper objectMapper;
    private PagoRequestDTO requestDTO;
    private PagoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pagoController).build();
        objectMapper = new ObjectMapper();

        requestDTO = new PagoRequestDTO();
        requestDTO.setPedidoId(1L);
        requestDTO.setMonto(BigDecimal.valueOf(15000));
        requestDTO.setMetodoPago("EFECTIVO");
        requestDTO.setReferencia("REF-001");

        responseDTO = PagoResponseDTO.builder()
                .id(1L)
                .pedidoId(1L)
                .monto(BigDecimal.valueOf(15000))
                .metodoPago("EFECTIVO")
                .estado("PENDIENTE")
                .referencia("REF-001")
                .build();
    }

    @Test
    @DisplayName("POST /api/pagos devuelve 201 cuando es exitoso")
    void registrar_devuelve201() throws Exception {
        when(pagoService.registrar(any(PagoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidoId").value(1));
    }

    @Test
    @DisplayName("GET /api/pagos devuelve 200 con lista")
    void listarTodos_devuelve200() throws Exception {
        when(pagoService.listarTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/pagos/{id} devuelve 200 cuando existe")
    void buscarPorId_devuelve200() throws Exception {
        when(pagoService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/pagos/pedido/{pedidoId} devuelve 200")
    void buscarPorPedido_devuelve200() throws Exception {
        when(pagoService.buscarPorPedido(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/pagos/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(1));
    }

    @Test
    @DisplayName("GET /api/pagos/estado/{estado} devuelve 200 con lista")
    void listarPorEstado_devuelve200() throws Exception {
        when(pagoService.listarPorEstado("PENDIENTE")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/pagos/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("PATCH /api/pagos/{id}/aprobar devuelve 200")
    void aprobar_devuelve200() throws Exception {
        when(pagoService.aprobar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/pagos/1/aprobar"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/pagos/{id}/rechazar devuelve 200")
    void rechazar_devuelve200() throws Exception {
        when(pagoService.rechazar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/pagos/1/rechazar"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/pagos/{id}/anular devuelve 200")
    void anular_devuelve200() throws Exception {
        when(pagoService.anular(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/pagos/1/anular"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/pagos/health devuelve 200")
    void health_devuelve200() throws Exception {
        mockMvc.perform(get("/api/pagos/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("activo"))
                .andExpect(jsonPath("$.servicio").value("ms-pagos"));
    }
}

