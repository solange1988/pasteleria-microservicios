package com.pasteleria.ms_pagos.service;

import com.pasteleria.ms_pagos.cliente.PedidoClient;
import com.pasteleria.ms_pagos.dto.PagoRequestDTO;
import com.pasteleria.ms_pagos.exception.PagoInvalidoException;
import com.pasteleria.ms_pagos.exception.RecursoNoEncontradoException;
import com.pasteleria.ms_pagos.model.Pago;
import com.pasteleria.ms_pagos.repository.PagoRepository;
import com.pasteleria.ms_pagos.dto.PagoResponseDTO;
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
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepository;
    private final PedidoClient pedidoClient;


    public PagoService(PagoRepository pagoRepository, PedidoClient pedidoClient) {
        this.pagoRepository = pagoRepository;
        this.pedidoClient = pedidoClient;
    }

    public PagoResponseDTO registrar(PagoRequestDTO dto) {
        log.info("Registrando pago — pedido ID: {}, monto: ${}", dto.getPedidoId(), dto.getMonto());


        if (!pedidoClient.existePedido(dto.getPedidoId())) {
            log.warn("Pago rechazado — pedido no encontrado ID: {}", dto.getPedidoId());
            throw new RecursoNoEncontradoException(
                    "El pedido con ID " + dto.getPedidoId() + " no existe"
            );
        }

        if (pagoRepository.existsByPedidoIdAndEstado(dto.getPedidoId(), Pago.EstadoPago.APROBADO)) {
            log.warn("Pago rechazado — pedido ID {} ya tiene pago aprobado", dto.getPedidoId());
            throw new PagoInvalidoException(
                    "El pedido ID " + dto.getPedidoId() + " ya tiene un pago aprobado"
            );
        }

        String estadoPedido = pedidoClient.obtenerEstadoPedido(dto.getPedidoId());
        if ("CANCELADO".equals(estadoPedido)) {
            log.warn("Pago rechazado — pedido ID {} está CANCELADO", dto.getPedidoId());
            throw new PagoInvalidoException("No se puede pagar un pedido CANCELADO");
        }

        BigDecimal totalPedido = pedidoClient.obtenerTotalPedido(dto.getPedidoId());
        log.debug("Total del pedido: ${}, monto pagado: ${}", totalPedido, dto.getMonto());

        Pago.MetodoPago metodoPago;
        try {
            metodoPago = Pago.MetodoPago.valueOf(dto.getMetodoPago().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Método de pago inválido: {}", dto.getMetodoPago());
            throw new PagoInvalidoException(
                    "Método de pago inválido: " + dto.getMetodoPago()
                            + ". Válidos: EFECTIVO, TARJETA_DEBITO, "
                            + "TARJETA_CREDITO, TRANSFERENCIA, OTRO"
            );
        }

        Pago pago = Pago.builder()
                .pedidoId(dto.getPedidoId())
                .monto(dto.getMonto())
                .metodoPago(metodoPago)
                .estado(Pago.EstadoPago.PENDIENTE)
                .referencia(dto.getReferencia())
                .notas(dto.getNotas())
                .build();

        Pago guardado = pagoRepository.save(pago);
        log.info("Pago registrado OK — ID: {}, pedido: {}, estado: PENDIENTE", guardado.getId(), guardado.getPedidoId());
        return mapearADTO(guardado);
    }

    public PagoResponseDTO aprobar(Long id) {
        log.info("Aprobando pago ID: {}", id);

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado — ID: {}", id);
                    return new RecursoNoEncontradoException("Pago no encontrado con ID: " + id);
                });

        if (pago.getEstado() != Pago.EstadoPago.PENDIENTE) {
            log.warn("Aprobación rechazada — pago ID {} en estado {}", id, pago.getEstado());
            throw new PagoInvalidoException("Solo se pueden aprobar pagos en estado PENDIENTE");
        }

        pago.setEstado(Pago.EstadoPago.APROBADO);
        pago.setFechaPago(LocalDateTime.now());
        Pago aprobado = pagoRepository.save(pago);

        boolean confirmado = pedidoClient.confirmarPedido(pago.getPedidoId());
        if (confirmado) {
            log.info("Pedido ID {} confirmado tras aprobación del pago", pago.getPedidoId());
        } else {
            log.warn("No se pudo confirmar pedido ID {} en ms-pedidos", pago.getPedidoId());
        }

        log.info("Pago aprobado OK — ID: {}, pedido: {}", aprobado.getId(), aprobado.getPedidoId());
        return mapearADTO(aprobado);
    }

    public PagoResponseDTO rechazar(Long id) {
        log.info("Rechazando pago ID: {}", id);

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado — ID: {}", id);
                    return new RecursoNoEncontradoException("Pago no encontrado con ID: " + id);
                });

        if (pago.getEstado() != Pago.EstadoPago.PENDIENTE) {
            log.warn("Rechazo rechazado — pago ID {} en estado {}", id, pago.getEstado());
            throw new PagoInvalidoException("Solo se pueden rechazar pagos en estado PENDIENTE");
        }

        pago.setEstado(Pago.EstadoPago.RECHAZADO);
        Pago rechazado = pagoRepository.save(pago);
        log.info("Pago rechazado OK — ID: {}", rechazado.getId());
        return mapearADTO(rechazado);
    }

    public PagoResponseDTO anular(Long id) {
        log.info("Anulando pago ID: {}", id);

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado — ID: {}", id);
                    return new RecursoNoEncontradoException("Pago no encontrado con ID: " + id);
                });

        if (pago.getEstado() != Pago.EstadoPago.APROBADO) {
            log.warn("Anulación rechazada — pago ID {} en estado {}", id, pago.getEstado());
            throw new PagoInvalidoException("Solo se pueden anular pagos APROBADOS");
        }

        pago.setEstado(Pago.EstadoPago.ANULADO);
        Pago anulado = pagoRepository.save(pago);
        log.info("Pago anulado OK — ID: {}", anulado.getId());
        return mapearADTO(anulado);
    }

    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarTodos() {
        log.info("Listando todos los pagos");
        return pagoRepository.findAll()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagoResponseDTO buscarPorId(Long id) {
        log.info("Buscando pago ID: {}", id);
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado — ID: {}", id);
                    return new RecursoNoEncontradoException("Pago no encontrado con ID: " + id);
                });
        return mapearADTO(pago);
    }

    @Transactional(readOnly = true)
    public PagoResponseDTO buscarPorPedido(Long pedidoId) {
        log.info("Buscando pago por pedido ID: {}", pedidoId);
        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> {
                    log.warn("Pago no encontrado para pedido ID: {}", pedidoId);
                    return new RecursoNoEncontradoException("Pago no encontrado para pedido ID: " + pedidoId);
                });
        return mapearADTO(pago);
    }

    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarPorEstado(String estado) {
        log.info("Listando pagos con estado: {}", estado);
        try {
            Pago.EstadoPago estadoEnum = Pago.EstadoPago.valueOf(estado.toUpperCase());
            return pagoRepository.findByEstado(estadoEnum)
                    .stream()
                    .map(this::mapearADTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("Estado de pago inválido: {}", estado);
            throw new PagoInvalidoException(
                    "Estado inválido: " + estado
                            + ". Válidos: PENDIENTE, APROBADO, RECHAZADO, ANULADO"
            );
        }
    }

    @Transactional(readOnly = true)
    public List<PagoResponseDTO> buscarPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        log.info("Buscando pagos entre {} y {}", inicio, fin);
        return pagoRepository.buscarPorRangoFecha(inicio, fin)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    private PagoResponseDTO mapearADTO(Pago p) {
        return PagoResponseDTO.builder()
                .id(p.getId())
                .pedidoId(p.getPedidoId())
                .monto(p.getMonto())
                .metodoPago(p.getMetodoPago().name())
                .estado(p.getEstado().name())
                .referencia(p.getReferencia())
                .notas(p.getNotas())
                .fechaPago(p.getFechaPago())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}