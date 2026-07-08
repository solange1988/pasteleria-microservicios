package com.pasteleria.ms_pagos;
import com.pasteleria.ms_pagos.cliente.PedidoClient;
import com.pasteleria.ms_pagos.dto.PagoRequestDTO;
import com.pasteleria.ms_pagos.dto.PagoResponseDTO;
import com.pasteleria.ms_pagos.exception.PagoInvalidoException;
import com.pasteleria.ms_pagos.exception.RecursoNoEncontradoException;
import com.pasteleria.ms_pagos.model.Pago;
import com.pasteleria.ms_pagos.repository.PagoRepository;
import com.pasteleria.ms_pagos.service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private PagoService pagoService;

    private PagoRequestDTO requestDTO;
    private Pago pago;

    @BeforeEach
    void setUp() {
        requestDTO = new PagoRequestDTO();
        requestDTO.setPedidoId(1L);
        requestDTO.setMonto(BigDecimal.valueOf(15000));
        requestDTO.setMetodoPago("EFECTIVO");
        requestDTO.setReferencia("REF-001");

        pago = Pago.builder()
                .id(1L)
                .pedidoId(1L)
                .monto(BigDecimal.valueOf(15000))
                .metodoPago(Pago.MetodoPago.EFECTIVO)
                .estado(Pago.EstadoPago.PENDIENTE)
                .referencia("REF-001")
                .build();
    }

    @Test
    @DisplayName("Registrar pago exitosamente")
    void registrar_exitoso() {

        when(pedidoClient.existePedido(1L)).thenReturn(true);
        when(pagoRepository.existsByPedidoIdAndEstado(1L, Pago.EstadoPago.APROBADO)).thenReturn(false);
        when(pedidoClient.obtenerEstadoPedido(1L)).thenReturn("PENDIENTE");
        when(pedidoClient.obtenerTotalPedido(1L)).thenReturn(BigDecimal.valueOf(15000));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);


        PagoResponseDTO response = pagoService.registrar(requestDTO);


        assertNotNull(response);
        assertEquals(1L, response.getPedidoId());
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    @DisplayName("Registrar pago con pedido inexistente lanza excepción")
    void registrar_pedidoInexistente_lanzaExcepcion() {

        when(pedidoClient.existePedido(1L)).thenReturn(false);


        assertThrows(RecursoNoEncontradoException.class,
                () -> pagoService.registrar(requestDTO));
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("Registrar pago con pedido ya aprobado lanza excepción")
    void registrar_pedidoYaAprobado_lanzaExcepcion() {

        when(pedidoClient.existePedido(1L)).thenReturn(true);
        when(pagoRepository.existsByPedidoIdAndEstado(1L, Pago.EstadoPago.APROBADO)).thenReturn(true);


        assertThrows(PagoInvalidoException.class,
                () -> pagoService.registrar(requestDTO));
    }

    @Test
    @DisplayName("Registrar pago para pedido cancelado lanza excepción")
    void registrar_pedidoCancelado_lanzaExcepcion() {

        when(pedidoClient.existePedido(1L)).thenReturn(true);
        when(pagoRepository.existsByPedidoIdAndEstado(1L, Pago.EstadoPago.APROBADO)).thenReturn(false);
        when(pedidoClient.obtenerEstadoPedido(1L)).thenReturn("CANCELADO");


        assertThrows(PagoInvalidoException.class,
                () -> pagoService.registrar(requestDTO));
    }

    @Test
    @DisplayName("Registrar pago con método de pago inválido lanza excepción")
    void registrar_metodoInvalido_lanzaExcepcion() {

        when(pedidoClient.existePedido(1L)).thenReturn(true);
        when(pagoRepository.existsByPedidoIdAndEstado(1L, Pago.EstadoPago.APROBADO)).thenReturn(false);
        when(pedidoClient.obtenerEstadoPedido(1L)).thenReturn("PENDIENTE");
        when(pedidoClient.obtenerTotalPedido(1L)).thenReturn(BigDecimal.valueOf(15000));
        requestDTO.setMetodoPago("METODO_INVALIDO");


        assertThrows(PagoInvalidoException.class,
                () -> pagoService.registrar(requestDTO));
    }

    @Test
    @DisplayName("Aprobar pago exitosamente")
    void aprobar_exitoso() {

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(pedidoClient.confirmarPedido(1L)).thenReturn(true);


        PagoResponseDTO response = pagoService.aprobar(1L);


        assertNotNull(response);
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    @DisplayName("Aprobar pago que no está pendiente lanza excepción")
    void aprobar_noPendiente_lanzaExcepcion() {

        pago.setEstado(Pago.EstadoPago.APROBADO);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));


        assertThrows(PagoInvalidoException.class,
                () -> pagoService.aprobar(1L));
    }

    @Test
    @DisplayName("Rechazar pago exitosamente")
    void rechazar_exitoso() {

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);


        PagoResponseDTO response = pagoService.rechazar(1L);


        assertNotNull(response);
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    @DisplayName("Anular pago exitosamente")
    void anular_exitoso() {

        pago.setEstado(Pago.EstadoPago.APROBADO);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);


        PagoResponseDTO response = pagoService.anular(1L);


        assertNotNull(response);
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    @DisplayName("Anular pago no aprobado lanza excepción")
    void anular_noAprobado_lanzaExcepcion() {

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));


        assertThrows(PagoInvalidoException.class,
                () -> pagoService.anular(1L));
    }

    @Test
    @DisplayName("Buscar pago por ID exitosamente")
    void buscarPorId_exitoso() {

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));


        PagoResponseDTO response = pagoService.buscarPorId(1L);


        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Buscar pago por ID no encontrado lanza excepción")
    void buscarPorId_noEncontrado_lanzaExcepcion() {

        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());


        assertThrows(RecursoNoEncontradoException.class,
                () -> pagoService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Listar todos los pagos")
    void listarTodos_exitoso() {

        when(pagoRepository.findAll()).thenReturn(List.of(pago));


        List<PagoResponseDTO> response = pagoService.listarTodos();


        assertNotNull(response);
        assertEquals(1, response.size());
    }

}
