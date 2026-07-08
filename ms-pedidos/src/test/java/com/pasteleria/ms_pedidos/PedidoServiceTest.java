package com.pasteleria.ms_pedidos;
import com.pasteleria.ms_pedidos.cliente.ProductoClient;
import com.pasteleria.ms_pedidos.cliente.UsuarioClient;
import com.pasteleria.ms_pedidos.dto.DetallePedidoDTO;
import com.pasteleria.ms_pedidos.dto.PedidoRequestDTO;
import com.pasteleria.ms_pedidos.dto.PedidoResponseDTO;
import com.pasteleria.ms_pedidos.exception.RecursoNoEncontradoException;
import com.pasteleria.ms_pedidos.exception.StockInsuficienteException;
import com.pasteleria.ms_pedidos.model.Pedido;
import com.pasteleria.ms_pedidos.repository.PedidoRepository;
import com.pasteleria.ms_pedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private PedidoService pedidoService;

    private PedidoRequestDTO requestDTO;
    private Pedido pedido;
    private DetallePedidoDTO detalleDTO;

    @BeforeEach
    void setUp() {
        detalleDTO = new DetallePedidoDTO();
        detalleDTO.setProductoId(1L);
        detalleDTO.setCantidad(2);

        requestDTO = new PedidoRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setNotas("Sin azúcar");
        requestDTO.setDetalles(List.of(detalleDTO));

        pedido = Pedido.builder()
                .id(1L)
                .usuarioId(1L)
                .usuarioNombre("Juan Perez")
                .estado(Pedido.EstadoPedido.PENDIENTE)
                .total(BigDecimal.valueOf(100))
                .detalles(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Crear pedido exitosamente")
    void crear_exitoso() {

        when(usuarioClient.existeUsuario(1L)).thenReturn(true);
        when(usuarioClient.obtenerNombreUsuario(1L)).thenReturn("Juan Perez");
        when(productoClient.existeProducto(1L)).thenReturn(true);
        when(productoClient.tieneStockSuficiente(1L, 2)).thenReturn(true);
        when(productoClient.obtenerPrecio(1L)).thenReturn(BigDecimal.valueOf(50));
        when(productoClient.obtenerNombre(1L)).thenReturn("Torta de chocolate");
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);


        PedidoResponseDTO response = pedidoService.crear(requestDTO);


        assertNotNull(response);
        assertEquals("Juan Perez", response.getUsuarioNombre());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Crear pedido con usuario inexistente lanza excepción")
    void crear_usuarioInexistente_lanzaExcepcion() {

        when(usuarioClient.existeUsuario(1L)).thenReturn(false);


        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.crear(requestDTO));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Crear pedido con producto inexistente lanza excepción")
    void crear_productoInexistente_lanzaExcepcion() {

        when(usuarioClient.existeUsuario(1L)).thenReturn(true);
        when(usuarioClient.obtenerNombreUsuario(1L)).thenReturn("Juan Perez");
        when(productoClient.existeProducto(1L)).thenReturn(false);


        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.crear(requestDTO));
    }

    @Test
    @DisplayName("Crear pedido con stock insuficiente lanza excepción")
    void crear_stockInsuficiente_lanzaExcepcion() {

        when(usuarioClient.existeUsuario(1L)).thenReturn(true);
        when(usuarioClient.obtenerNombreUsuario(1L)).thenReturn("Juan Perez");
        when(productoClient.existeProducto(1L)).thenReturn(true);
        when(productoClient.tieneStockSuficiente(1L, 2)).thenReturn(false);


        assertThrows(StockInsuficienteException.class,
                () -> pedidoService.crear(requestDTO));
    }

    @Test
    @DisplayName("Listar todos los pedidos")
    void listarTodos_exitoso() {

        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));


        List<PedidoResponseDTO> response = pedidoService.listarTodos();


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Buscar pedido por ID exitosamente")
    void buscarPorId_exitoso() {

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));


        PedidoResponseDTO response = pedidoService.buscarPorId(1L);


        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Buscar pedido por ID no encontrado lanza excepción")
    void buscarPorId_noEncontrado_lanzaExcepcion() {

        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());


        assertThrows(RecursoNoEncontradoException.class,
                () -> pedidoService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Listar pedidos por usuario")
    void listarPorUsuario_exitoso() {

        when(pedidoRepository.findByUsuarioId(1L)).thenReturn(List.of(pedido));


        List<PedidoResponseDTO> response = pedidoService.listarPorUsuario(1L);


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Cambiar estado de pedido exitosamente")
    void cambiarEstado_exitoso() {

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);


        PedidoResponseDTO response = pedidoService.cambiarEstado(1L, "CONFIRMADO");


        assertNotNull(response);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Cambiar estado de pedido ya entregado lanza excepción")
    void cambiarEstado_pedidoEntregado_lanzaExcepcion() {

        pedido.setEstado(Pedido.EstadoPedido.ENTREGADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));


        assertThrows(RuntimeException.class,
                () -> pedidoService.cambiarEstado(1L, "CONFIRMADO"));
    }

    @Test
    @DisplayName("Cancelar pedido exitosamente")
    void cancelar_exitoso() {

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);


        PedidoResponseDTO response = pedidoService.cancelar(1L);


        assertNotNull(response);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Cancelar pedido ya entregado lanza excepción")
    void cancelar_pedidoEntregado_lanzaExcepcion() {

        pedido.setEstado(Pedido.EstadoPedido.ENTREGADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));


        assertThrows(RuntimeException.class,
                () -> pedidoService.cancelar(1L));
    }

}
