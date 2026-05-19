package com.pasteleria.ms_pedidos.service;
import com.pasteleria.ms_pedidos.cliente.ProductoClient;
import com.pasteleria.ms_pedidos.cliente.UsuarioClient;
import com.pasteleria.ms_pedidos.dto.*;
import com.pasteleria.ms_pedidos.exception.*;
import com.pasteleria.ms_pedidos.model.DetallePedido;
import com.pasteleria.ms_pedidos.model.Pedido;
import com.pasteleria.ms_pedidos.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional

public class PedidoService {

    private static final Logger log =
            LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final ProductoClient productoClient;
    private final UsuarioClient usuarioClient;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProductoClient productoClient,
                         UsuarioClient usuarioClient) {
        this.pedidoRepository = pedidoRepository;
        this.productoClient = productoClient;
        this.usuarioClient = usuarioClient;
    }


    public PedidoResponseDTO crear(PedidoRequestDTO dto) {
        log.info("Iniciando creación de pedido — usuario ID: {}",
                dto.getUsuarioId());


        if (!usuarioClient.existeUsuario(dto.getUsuarioId())) {
            log.warn("Creación rechazada — usuario no encontrado ID: {}",
                    dto.getUsuarioId());
            throw new RecursoNoEncontradoException(
                    "El usuario con ID " + dto.getUsuarioId() + " no existe"
            );
        }

        String nombreUsuario = usuarioClient
                .obtenerNombreUsuario(dto.getUsuarioId());
        log.debug("Usuario encontrado: {}", nombreUsuario);

        Pedido pedido = Pedido.builder()
                .usuarioId(dto.getUsuarioId())
                .usuarioNombre(nombreUsuario)
                .estado(Pedido.EstadoPedido.PENDIENTE)
                .notas(dto.getNotas())
                .fechaEntrega(dto.getFechaEntrega())
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal totalPedido = BigDecimal.ZERO;


        for (DetallePedidoDTO detalleDTO : dto.getDetalles()) {

            // Validar que el producto existe
            if (!productoClient.existeProducto(
                    detalleDTO.getProductoId())) {
                log.warn("Producto no encontrado — ID: {}",
                        detalleDTO.getProductoId());
                throw new RecursoNoEncontradoException(
                        "El producto con ID "
                                + detalleDTO.getProductoId() + " no existe"
                );
            }


            if (!productoClient.tieneStockSuficiente(
                    detalleDTO.getProductoId(),
                    detalleDTO.getCantidad())) {
                log.warn("Stock insuficiente — producto ID: {}",
                        detalleDTO.getProductoId());
                throw new StockInsuficienteException(
                        "Stock insuficiente para producto ID: "
                                + detalleDTO.getProductoId()
                );
            }

            BigDecimal precio = productoClient
                    .obtenerPrecio(detalleDTO.getProductoId());
            String nombreProducto = productoClient
                    .obtenerNombre(detalleDTO.getProductoId());
            BigDecimal subtotal = precio.multiply(
                    BigDecimal.valueOf(detalleDTO.getCantidad()));

            log.debug("Detalle procesado — producto: {}, cantidad: {}, subtotal: ${}",
                    nombreProducto, detalleDTO.getCantidad(), subtotal);

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .productoId(detalleDTO.getProductoId())
                    .productoNombre(nombreProducto)
                    .cantidad(detalleDTO.getCantidad())
                    .precioUnitario(precio)
                    .subtotal(subtotal)
                    .build();

            pedido.getDetalles().add(detalle);
            totalPedido = totalPedido.add(subtotal);
        }

        pedido.setTotal(totalPedido);
        Pedido guardado = pedidoRepository.save(pedido);

        log.info("Pedido creado OK — ID: {}, usuario: {}, total: ${}",
                guardado.getId(), nombreUsuario, guardado.getTotal());

        return mapearADTO(guardado);
    }


    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        log.info("Listando todos los pedidos");
        return pedidoRepository.findAll()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        log.info("Buscando pedido ID: {}", id);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pedido no encontrado — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Pedido no encontrado con ID: " + id
                    );
                });
        log.debug("Pedido encontrado — ID: {}, estado: {}",
                id, pedido.getEstado());
        return mapearADTO(pedido);
    }


    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPorUsuario(Long usuarioId) {
        log.info("Listando pedidos — usuario ID: {}", usuarioId);
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPorEstado(String estado) {
        log.info("Listando pedidos con estado: {}", estado);
        try {
            Pedido.EstadoPedido estadoEnum =
                    Pedido.EstadoPedido.valueOf(estado.toUpperCase());
            return pedidoRepository.findByEstado(estadoEnum)
                    .stream()
                    .map(this::mapearADTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("Estado inválido recibido: {}", estado);
            throw new RuntimeException(
                    "Estado inválido: " + estado +
                            ". Válidos: PENDIENTE, CONFIRMADO, EN_PREPARACION, " +
                            "LISTO, ENTREGADO, CANCELADO"
            );
        }
    }


    public PedidoResponseDTO cambiarEstado(Long id, String nuevoEstado) {
        log.info("Cambiando estado pedido ID: {} → {}", id, nuevoEstado);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pedido no encontrado para cambiar estado — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Pedido no encontrado con ID: " + id
                    );
                });


        if (pedido.getEstado() == Pedido.EstadoPedido.ENTREGADO ||
                pedido.getEstado() == Pedido.EstadoPedido.CANCELADO) {
            log.warn("Cambio de estado rechazado — pedido {} ya está {}",
                    id, pedido.getEstado());
            throw new RuntimeException(
                    "No se puede cambiar el estado de un pedido "
                            + pedido.getEstado().name()
            );
        }

        try {
            Pedido.EstadoPedido estadoEnum =
                    Pedido.EstadoPedido.valueOf(nuevoEstado.toUpperCase());
            pedido.setEstado(estadoEnum);
        } catch (IllegalArgumentException e) {
            log.error("Estado inválido: {}", nuevoEstado);
            throw new RuntimeException("Estado inválido: " + nuevoEstado);
        }

        Pedido actualizado = pedidoRepository.save(pedido);
        log.info("Estado cambiado OK — pedido ID: {}, nuevo estado: {}",
                id, actualizado.getEstado());
        return mapearADTO(actualizado);
    }


    public PedidoResponseDTO cancelar(Long id) {
        log.info("Cancelando pedido ID: {}", id);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pedido no encontrado para cancelar — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Pedido no encontrado con ID: " + id
                    );
                });


        if (pedido.getEstado() != Pedido.EstadoPedido.PENDIENTE &&
                pedido.getEstado() != Pedido.EstadoPedido.CONFIRMADO) {
            log.warn("Cancelación rechazada — pedido {} está en estado {}",
                    id, pedido.getEstado());
            throw new RuntimeException(
                    "Solo se pueden cancelar pedidos PENDIENTES o CONFIRMADOS"
            );
        }

        pedido.setEstado(Pedido.EstadoPedido.CANCELADO);
        Pedido cancelado = pedidoRepository.save(pedido);
        log.info("Pedido cancelado OK — ID: {}", id);
        return mapearADTO(cancelado);
    }


    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPorFecha(
            LocalDateTime inicio, LocalDateTime fin) {
        log.info("Buscando pedidos entre {} y {}", inicio, fin);
        return pedidoRepository.buscarPorRangoFecha(inicio, fin)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    private PedidoResponseDTO mapearADTO(Pedido p) {
        List<DetallePedidoDTO> detallesDTO = p.getDetalles()
                .stream()
                .map(d -> DetallePedidoDTO.builder()
                        .id(d.getId())
                        .productoId(d.getProductoId())
                        .productoNombre(d.getProductoNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return PedidoResponseDTO.builder()
                .id(p.getId())
                .usuarioId(p.getUsuarioId())
                .usuarioNombre(p.getUsuarioNombre())
                .estado(p.getEstado().name())
                .total(p.getTotal())
                .notas(p.getNotas())
                .fechaEntrega(p.getFechaEntrega())
                .detalles(detallesDTO)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
