package com.pasteleria.ms_notificaciones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasteleria.ms_notificaciones.controller.NotificacionController;
import com.pasteleria.ms_notificaciones.dto.NotificacionRequestDTO;
import com.pasteleria.ms_notificaciones.dto.NotificacionResponseDTO;
import com.pasteleria.ms_notificaciones.service.NotificacionService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private NotificacionController notificacionController;

    private ObjectMapper objectMapper;
    private NotificacionRequestDTO requestDTO;
    private NotificacionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificacionController).build();
        objectMapper = new ObjectMapper();

        requestDTO = new NotificacionRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setPedidoId(1L);
        requestDTO.setTitulo("Pedido confirmado");
        requestDTO.setMensaje("Tu pedido ha sido confirmado");
        requestDTO.setTipo("CONFIRMACION_PEDIDO");

        responseDTO = NotificacionResponseDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .usuarioNombre("Juan Perez")
                .usuarioEmail("juan@test.com")
                .pedidoId(1L)
                .titulo("Pedido confirmado")
                .mensaje("Tu pedido ha sido confirmado")
                .tipo("CONFIRMACION_PEDIDO")
                .estado("ENVIADA")
                .build();
    }

    @Test
    @DisplayName("POST /api/notificaciones devuelve 201 cuando es exitoso")
    void crear_devuelve201() throws Exception {
        when(notificacionService.crear(any(NotificacionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioNombre").value("Juan Perez"));
    }

    @Test
    @DisplayName("GET /api/notificaciones devuelve 200 con lista")
    void listarTodas_devuelve200() throws Exception {
        when(notificacionService.listarTodas()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/notificaciones/{id} devuelve 200 cuando existe")
    void buscarPorId_devuelve200() throws Exception {
        when(notificacionService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/notificaciones/usuario/{usuarioId} devuelve 200")
    void listarPorUsuario_devuelve200() throws Exception {
        when(notificacionService.listarPorUsuario(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/notificaciones/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(1));
    }

    @Test
    @DisplayName("GET /api/notificaciones/pedido/{pedidoId} devuelve 200")
    void listarPorPedido_devuelve200() throws Exception {
        when(notificacionService.listarPorPedido(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/notificaciones/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pedidoId").value(1));
    }

    @Test
    @DisplayName("GET /api/notificaciones/estado/{estado} devuelve 200")
    void listarPorEstado_devuelve200() throws Exception {
        when(notificacionService.listarPorEstado("ENVIADA")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/notificaciones/estado/ENVIADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("ENVIADA"));
    }

    @Test
    @DisplayName("GET /api/notificaciones/pendientes devuelve 200")
    void listarPendientes_devuelve200() throws Exception {
        when(notificacionService.listarPendientes()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/notificaciones/pendientes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/notificaciones/{id}/reenviar devuelve 200")
    void reenviar_devuelve200() throws Exception {
        when(notificacionService.reenviar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/notificaciones/1/reenviar"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/notificaciones/health devuelve 200")
    void health_devuelve200() throws Exception {
        mockMvc.perform(get("/api/notificaciones/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("activo"))
                .andExpect(jsonPath("$.servicio").value("ms-notificaciones"));
    }
}
