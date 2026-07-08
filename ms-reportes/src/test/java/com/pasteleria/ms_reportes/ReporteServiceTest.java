package com.pasteleria.ms_reportes;
import com.pasteleria.ms_reportes.cliente.PagoClient;
import com.pasteleria.ms_reportes.cliente.PedidoClient;
import com.pasteleria.ms_reportes.cliente.ProductoClient;
import com.pasteleria.ms_reportes.dto.ReporteResponseDTO;
import com.pasteleria.ms_reportes.model.Reporte;
import com.pasteleria.ms_reportes.repository.ReporteRepository;
import com.pasteleria.ms_reportes.service.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReporteServiceTest {
    @Mock
    private PedidoClient pedidoClient;

    @Mock
    private PagoClient pagoClient;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private ReporteRepository reporteRepository;

    @InjectMocks
    private ReporteService reporteService;

    private List<Map<String, Object>> pedidosMock;
    private List<Map<String, Object>> pagosMock;
    private List<Map<String, Object>> productosMock;
    private Reporte reporte;

    @BeforeEach
    void setUp() {
        Map<String, Object> pedido1 = new HashMap<>();
        pedido1.put("estado", "PENDIENTE");
        pedidosMock = List.of(pedido1);

        Map<String, Object> pago1 = new HashMap<>();
        pago1.put("estado", "APROBADO");
        pago1.put("monto", "15000");
        pago1.put("metodoPago", "EFECTIVO");
        pagosMock = List.of(pago1);

        Map<String, Object> producto1 = new HashMap<>();
        producto1.put("nombre", "Torta de chocolate");
        producto1.put("stock", 10);
        producto1.put("precio", "15000");
        producto1.put("disponible", true);
        productosMock = List.of(producto1);

        reporte = Reporte.builder()
                .id(1L)
                .tipoReporte("REPORTE_GENERAL")
                .generadoPor("SISTEMA")
                .estado(Reporte.EstadoReporte.GENERADO)
                .build();
    }

    @Test
    @DisplayName("Generar reporte general exitosamente")
    void reporteGeneral_exitoso() {

        when(pedidoClient.obtenerTodos()).thenReturn(pedidosMock);
        when(pagoClient.obtenerTodos()).thenReturn(pagosMock);
        when(productoClient.obtenerTodos()).thenReturn(productosMock);
        when(productoClient.obtenerStockBajo()).thenReturn(List.of());
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);


        ReporteResponseDTO response = reporteService.reporteGeneral();


        assertNotNull(response);
        assertEquals("REPORTE_GENERAL", response.getTipoReporte());
        assertEquals(1L, response.getTotalPedidos());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    @DisplayName("Generar reporte de ventas exitosamente")
    void reporteVentas_exitoso() {

        when(pagoClient.obtenerPorEstado("APROBADO")).thenReturn(pagosMock);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);


        ReporteResponseDTO response = reporteService.reporteVentas();


        assertNotNull(response);
        assertEquals("REPORTE_VENTAS", response.getTipoReporte());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    @DisplayName("Generar reporte de pedidos exitosamente")
    void reportePedidos_exitoso() {

        when(pedidoClient.obtenerTodos()).thenReturn(pedidosMock);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);


        ReporteResponseDTO response = reporteService.reportePedidos();


        assertNotNull(response);
        assertEquals("REPORTE_PEDIDOS", response.getTipoReporte());
        assertEquals(1L, response.getTotalPedidos());
    }

    @Test
    @DisplayName("Generar reporte de productos exitosamente")
    void reporteProductos_exitoso() {

        when(productoClient.obtenerTodos()).thenReturn(productosMock);
        when(productoClient.obtenerDisponibles()).thenReturn(productosMock);
        when(productoClient.obtenerStockBajo()).thenReturn(List.of());
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);


        ReporteResponseDTO response = reporteService.reporteProductos();


        assertNotNull(response);
        assertEquals("REPORTE_PRODUCTOS", response.getTipoReporte());
        assertEquals(1L, response.getTotalProductos());
    }

    @Test
    @DisplayName("Generar reporte de pedidos entregados exitosamente")
    void reportePedidosEntregados_exitoso() {

        Map<String, Object> entregado = new HashMap<>();
        entregado.put("estado", "ENTREGADO");
        entregado.put("total", "15000");
        when(pedidoClient.obtenerPorEstado("ENTREGADO")).thenReturn(List.of(entregado));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);


        ReporteResponseDTO response = reporteService.reportePedidosEntregados();


        assertNotNull(response);
        assertEquals("REPORTE_PEDIDOS_ENTREGADOS", response.getTipoReporte());
        assertEquals(1L, response.getPedidosEntregados());
    }

    @Test
    @DisplayName("Listar historial de reportes")
    void listarHistorial_exitoso() {

        when(reporteRepository.findAll()).thenReturn(List.of(reporte));


        List<Reporte> response = reporteService.listarHistorial();


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Buscar historial por tipo")
    void historialPorTipo_exitoso() {

        when(reporteRepository.buscarPorTipo("REPORTE_GENERAL")).thenReturn(List.of(reporte));


        List<Reporte> response = reporteService.historialPorTipo("REPORTE_GENERAL");


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Buscar reporte por ID exitosamente")
    void buscarPorId_exitoso() {

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));


        Reporte response = reporteService.buscarPorId(1L);


        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Buscar reporte por ID no encontrado lanza excepción")
    void buscarPorId_noEncontrado_lanzaExcepcion() {

        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class,
                () -> reporteService.buscarPorId(99L));
    }

}
