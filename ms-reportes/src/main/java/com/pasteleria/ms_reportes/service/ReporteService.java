package com.pasteleria.ms_reportes.service;

import com.pasteleria.ms_reportes.cliente.PagoClient;
import com.pasteleria.ms_reportes.cliente.PedidoClient;
import com.pasteleria.ms_reportes.cliente.ProductoClient;
import com.pasteleria.ms_reportes.dto.ReporteResponseDTO;
import com.pasteleria.ms_reportes.model.Reporte;
import com.pasteleria.ms_reportes.repository.ReporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional
public class ReporteService {

    private static final Logger log =
            LoggerFactory.getLogger(ReporteService.class);


    private final PedidoClient pedidoClient;
    private final PagoClient pagoClient;
    private final ProductoClient productoClient;
    private final ReporteRepository reporteRepository;

    public ReporteService(PedidoClient pedidoClient,
                          PagoClient pagoClient,
                          ProductoClient productoClient,
                          ReporteRepository reporteRepository) {
        this.pedidoClient = pedidoClient;
        this.pagoClient = pagoClient;
        this.productoClient = productoClient;
        this.reporteRepository = reporteRepository;
    }


    public ReporteResponseDTO reporteGeneral() {
        log.info("Generando reporte general del sistema");

        List<Map<String, Object>> pedidos =
                pedidoClient.obtenerTodos();
        List<Map<String, Object>> pagos =
                pagoClient.obtenerTodos();
        List<Map<String, Object>> productos =
                productoClient.obtenerTodos();
        List<Map<String, Object>> stockBajo =
                productoClient.obtenerStockBajo();

        log.debug("Datos obtenidos — pedidos: {}, pagos: {}, " +
                        "productos: {}",
                pedidos.size(), pagos.size(), productos.size());

        long totalPedidos  = pedidos.size();
        long pendientes    = contarPorEstado(pedidos, "PENDIENTE");
        long confirmados   = contarPorEstado(pedidos, "CONFIRMADO");
        long enPreparacion = contarPorEstado(pedidos, "EN_PREPARACION");
        long listos        = contarPorEstado(pedidos, "LISTO");
        long entregados    = contarPorEstado(pedidos, "ENTREGADO");
        long cancelados    = contarPorEstado(pedidos, "CANCELADO");

        long totalPagos      = pagos.size();
        long pagosPendientes = contarPorEstado(pagos, "PENDIENTE");
        long pagosAprobados  = contarPorEstado(pagos, "APROBADO");
        long pagosRechazados = contarPorEstado(pagos, "RECHAZADO");

        BigDecimal totalRecaudado = pagos.stream()
                .filter(p -> "APROBADO".equals(
                        p.getOrDefault("estado", "")))
                .map(p -> {
                    try {
                        return new BigDecimal(
                                p.get("monto").toString());
                    } catch (Exception e) {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalProductos = productos.size();
        long disponibles = productos.stream()
                .filter(p -> Boolean.TRUE.equals(
                        p.get("disponible")))
                .count();

        guardarHistorial(
                "REPORTE_GENERAL",
                "SISTEMA",
                "Reporte general del sistema",
                totalPedidos,
                totalPagos,
                totalProductos,
                totalRecaudado
        );

        log.info("Reporte general generado — pedidos: {}, " +
                "recaudado: ${}", totalPedidos, totalRecaudado);

        return ReporteResponseDTO.builder()
                .tipoReporte("REPORTE_GENERAL")
                .fechaGeneracion(LocalDateTime.now())
                .periodo("Todo el tiempo")
                .totalPedidos(totalPedidos)
                .pedidosPendientes(pendientes)
                .pedidosConfirmados(confirmados)
                .pedidosEnPreparacion(enPreparacion)
                .pedidosListos(listos)
                .pedidosEntregados(entregados)
                .pedidosCancelados(cancelados)
                .totalPagos(totalPagos)
                .pagosPendientes(pagosPendientes)
                .pagosAprobados(pagosAprobados)
                .pagosRechazados(pagosRechazados)
                .totalRecaudado(totalRecaudado)
                .totalProductos(totalProductos)
                .productosDisponibles(disponibles)
                .productosStockBajo((long) stockBajo.size())
                .mensaje("Reporte general generado exitosamente")
                .build();
    }


    public ReporteResponseDTO reporteVentas() {
        log.info("Generando reporte de ventas");

        List<Map<String, Object>> pagosAprobados =
                pagoClient.obtenerPorEstado("APROBADO");

        BigDecimal totalRecaudado = pagosAprobados.stream()
                .map(p -> {
                    try {
                        return new BigDecimal(
                                p.get("monto").toString());
                    } catch (Exception e) {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> porMetodo = pagosAprobados.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getOrDefault(
                                "metodoPago",
                                "DESCONOCIDO").toString(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> detalle = new ArrayList<>();
        porMetodo.forEach((metodo, cantidad) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("metodoPago", metodo);
            item.put("cantidadPagos", cantidad);
            detalle.add(item);
        });

        guardarHistorial(
                "REPORTE_VENTAS",
                "SISTEMA",
                "Reporte de ventas",
                0L,
                (long) pagosAprobados.size(),
                0L,
                totalRecaudado
        );

        log.info("Reporte ventas generado — recaudado: ${}",
                totalRecaudado);

        return ReporteResponseDTO.builder()
                .tipoReporte("REPORTE_VENTAS")
                .fechaGeneracion(LocalDateTime.now())
                .periodo("Todo el tiempo")
                .totalPagos((long) pagosAprobados.size())
                .pagosAprobados((long) pagosAprobados.size())
                .totalRecaudado(totalRecaudado)
                .detalle(detalle)
                .mensaje("Reporte de ventas generado exitosamente")
                .build();
    }


    public ReporteResponseDTO reportePedidos() {
        log.info("Generando reporte de pedidos");

        List<Map<String, Object>> pedidos =
                pedidoClient.obtenerTodos();

        Map<String, Long> porEstado = pedidos.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getOrDefault(
                                "estado",
                                "DESCONOCIDO").toString(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> detalle = new ArrayList<>();
        porEstado.forEach((estado, cantidad) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("estado", estado);
            item.put("cantidad", cantidad);
            detalle.add(item);
        });

        guardarHistorial(
                "REPORTE_PEDIDOS",
                "SISTEMA",
                "Reporte de pedidos",
                (long) pedidos.size(),
                0L, 0L,
                BigDecimal.ZERO
        );

        log.info("Reporte pedidos generado — total: {}",
                pedidos.size());

        return ReporteResponseDTO.builder()
                .tipoReporte("REPORTE_PEDIDOS")
                .fechaGeneracion(LocalDateTime.now())
                .periodo("Todo el tiempo")
                .totalPedidos((long) pedidos.size())
                .pedidosPendientes(
                        contarPorEstado(pedidos, "PENDIENTE"))
                .pedidosConfirmados(
                        contarPorEstado(pedidos, "CONFIRMADO"))
                .pedidosEnPreparacion(
                        contarPorEstado(pedidos, "EN_PREPARACION"))
                .pedidosListos(
                        contarPorEstado(pedidos, "LISTO"))
                .pedidosEntregados(
                        contarPorEstado(pedidos, "ENTREGADO"))
                .pedidosCancelados(
                        contarPorEstado(pedidos, "CANCELADO"))
                .detalle(detalle)
                .mensaje("Reporte de pedidos generado exitosamente")
                .build();
    }


    public ReporteResponseDTO reporteProductos() {
        log.info("Generando reporte de productos");

        List<Map<String, Object>> todos =
                productoClient.obtenerTodos();
        List<Map<String, Object>> disponibles =
                productoClient.obtenerDisponibles();
        List<Map<String, Object>> stockBajo =
                productoClient.obtenerStockBajo();

        List<Map<String, Object>> detalle = todos.stream()
                .sorted((a, b) -> {
                    int sA = a.get("stock") != null
                            ? (int) a.get("stock") : 0;
                    int sB = b.get("stock") != null
                            ? (int) b.get("stock") : 0;
                    return Integer.compare(sB, sA);
                })
                .limit(5)
                .map(p -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("nombre", p.get("nombre"));
                    item.put("stock", p.get("stock"));
                    item.put("precio", p.get("precio"));
                    item.put("disponible", p.get("disponible"));
                    return item;
                })
                .collect(Collectors.toList());

        guardarHistorial(
                "REPORTE_PRODUCTOS",
                "SISTEMA",
                "Reporte de productos",
                0L, 0L,
                (long) todos.size(),
                BigDecimal.ZERO
        );

        log.info("Reporte productos generado — total: {}, " +
                        "disponibles: {}, stockBajo: {}",
                todos.size(), disponibles.size(),
                stockBajo.size());

        return ReporteResponseDTO.builder()
                .tipoReporte("REPORTE_PRODUCTOS")
                .fechaGeneracion(LocalDateTime.now())
                .periodo("Estado actual")
                .totalProductos((long) todos.size())
                .productosDisponibles((long) disponibles.size())
                .productosStockBajo((long) stockBajo.size())
                .detalle(detalle)
                .mensaje("Reporte de productos generado exitosamente")
                .build();
    }


    public ReporteResponseDTO reportePedidosEntregados() {
        log.info("Generando reporte pedidos entregados");

        List<Map<String, Object>> entregados =
                pedidoClient.obtenerPorEstado("ENTREGADO");

        BigDecimal totalVendido = entregados.stream()
                .map(p -> {
                    try {
                        return new BigDecimal(
                                p.get("total").toString());
                    } catch (Exception e) {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        guardarHistorial(
                "REPORTE_PEDIDOS_ENTREGADOS",
                "SISTEMA",
                "Pedidos entregados",
                (long) entregados.size(),
                0L, 0L,
                totalVendido
        );

        log.info("Reporte entregados generado — total: {}, " +
                "vendido: ${}", entregados.size(), totalVendido);

        return ReporteResponseDTO.builder()
                .tipoReporte("REPORTE_PEDIDOS_ENTREGADOS")
                .fechaGeneracion(LocalDateTime.now())
                .periodo("Todo el tiempo")
                .totalPedidos((long) entregados.size())
                .pedidosEntregados((long) entregados.size())
                .totalRecaudado(totalVendido)
                .detalle(entregados)
                .mensaje("Reporte entregados generado exitosamente")
                .build();
    }


    @Transactional(readOnly = true)
    public List<Reporte> listarHistorial() {
        log.info("Listando historial de reportes");
        return reporteRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<Reporte> historialPorTipo(String tipo) {
        log.info("Listando historial tipo: {}", tipo);
        return reporteRepository.buscarPorTipo(tipo);
    }


    @Transactional(readOnly = true)
    public Reporte buscarPorId(Long id) {
        log.info("Buscando reporte ID: {}", id);
        return reporteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Reporte no encontrado — ID: {}", id);
                    return new RuntimeException(
                            "Reporte no encontrado con ID: " + id
                    );
                });
    }


    private void guardarHistorial(String tipo,
                                  String generadoPor,
                                  String descripcion,
                                  Long totalPedidos,
                                  Long totalPagos,
                                  Long totalProductos,
                                  BigDecimal totalRecaudado) {
        try {
            Reporte reporte = Reporte.builder()
                    .tipoReporte(tipo)
                    .generadoPor(generadoPor)
                    .descripcion(descripcion)
                    .totalPedidos(totalPedidos)
                    .totalPagos(totalPagos)
                    .totalProductos(totalProductos)
                    .totalRecaudado(totalRecaudado)
                    .estado(Reporte.EstadoReporte.GENERADO)
                    .build();
            reporteRepository.save(reporte);
            log.info("Historial guardado en BD — tipo: {}", tipo);
        } catch (Exception e) {
            log.error("Error al guardar historial: {}",
                    e.getMessage());
        }
    }


    private long contarPorEstado(
            List<Map<String, Object>> lista,
            String valor) {
        return lista.stream()
                .filter(item -> valor.equals(
                        item.getOrDefault("estado", "")
                                .toString()))
                .count();
    }
}