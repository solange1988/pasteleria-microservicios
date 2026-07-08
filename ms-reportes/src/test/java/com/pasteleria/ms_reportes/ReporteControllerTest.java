package com.pasteleria.ms_reportes;
import com.pasteleria.ms_reportes.controller.ReporteController;
import com.pasteleria.ms_reportes.dto.ReporteResponseDTO;
import com.pasteleria.ms_reportes.model.Reporte;
import com.pasteleria.ms_reportes.service.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReporteService reporteService;

    @InjectMocks
    private ReporteController reporteController;

    private ReporteResponseDTO responseDTO;
    private Reporte reporte;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reporteController).build();

        responseDTO = ReporteResponseDTO.builder()
                .tipoReporte("REPORTE_GENERAL")
                .periodo("Todo el tiempo")
                .totalPedidos(10L)
                .mensaje("Reporte generado exitosamente")
                .build();

        reporte = Reporte.builder()
                .id(1L)
                .tipoReporte("REPORTE_GENERAL")
                .generadoPor("SISTEMA")
                .estado(Reporte.EstadoReporte.GENERADO)
                .build();
    }

    @Test
    @DisplayName("GET /api/reportes/general devuelve 200")
    void reporteGeneral_devuelve200() throws Exception {
        when(reporteService.reporteGeneral()).thenReturn(responseDTO);

        mockMvc.perform(get("/api/reportes/general"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoReporte").value("REPORTE_GENERAL"));
    }

    @Test
    @DisplayName("GET /api/reportes/ventas devuelve 200")
    void reporteVentas_devuelve200() throws Exception {
        when(reporteService.reporteVentas()).thenReturn(responseDTO);

        mockMvc.perform(get("/api/reportes/ventas"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/reportes/pedidos devuelve 200")
    void reportePedidos_devuelve200() throws Exception {
        when(reporteService.reportePedidos()).thenReturn(responseDTO);

        mockMvc.perform(get("/api/reportes/pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/reportes/productos devuelve 200")
    void reporteProductos_devuelve200() throws Exception {
        when(reporteService.reporteProductos()).thenReturn(responseDTO);

        mockMvc.perform(get("/api/reportes/productos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/reportes/pedidos-entregados devuelve 200")
    void pedidosEntregados_devuelve200() throws Exception {
        when(reporteService.reportePedidosEntregados()).thenReturn(responseDTO);

        mockMvc.perform(get("/api/reportes/pedidos-entregados"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/reportes/historial devuelve 200")
    void historial_devuelve200() throws Exception {
        when(reporteService.listarHistorial()).thenReturn(List.of(reporte));

        mockMvc.perform(get("/api/reportes/historial"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/reportes/historial/{tipo} devuelve 200")
    void historialPorTipo_devuelve200() throws Exception {
        when(reporteService.historialPorTipo("REPORTE_GENERAL")).thenReturn(List.of(reporte));

        mockMvc.perform(get("/api/reportes/historial/REPORTE_GENERAL"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/reportes/historial/id/{id} devuelve 200")
    void buscarPorId_devuelve200() throws Exception {
        when(reporteService.buscarPorId(1L)).thenReturn(reporte);

        mockMvc.perform(get("/api/reportes/historial/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/reportes/health devuelve 200")
    void health_devuelve200() throws Exception {
        mockMvc.perform(get("/api/reportes/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("activo"))
                .andExpect(jsonPath("$.servicio").value("ms-reportes"));
    }
}
