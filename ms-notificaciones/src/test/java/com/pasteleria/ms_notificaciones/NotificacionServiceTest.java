package com.pasteleria.ms_notificaciones;
import com.pasteleria.ms_notificaciones.cliente.UsuarioClient;
import com.pasteleria.ms_notificaciones.dto.NotificacionRequestDTO;
import com.pasteleria.ms_notificaciones.dto.NotificacionResponseDTO;
import com.pasteleria.ms_notificaciones.exception.RecursoNoEncontradoException;
import com.pasteleria.ms_notificaciones.model.Notificacion;
import com.pasteleria.ms_notificaciones.repository.NotificacionRepository;
import com.pasteleria.ms_notificaciones.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {
    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private NotificacionService notificacionService;

    private NotificacionRequestDTO requestDTO;
    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
        requestDTO = new NotificacionRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setPedidoId(1L);
        requestDTO.setTitulo("Pedido confirmado");
        requestDTO.setMensaje("Tu pedido ha sido confirmado");
        requestDTO.setTipo("CONFIRMACION_PEDIDO");

        notificacion = Notificacion.builder()
                .id(1L)
                .usuarioId(1L)
                .usuarioNombre("Juan Perez")
                .usuarioEmail("juan@test.com")
                .pedidoId(1L)
                .titulo("Pedido confirmado")
                .mensaje("Tu pedido ha sido confirmado")
                .tipo(Notificacion.TipoNotificacion.CONFIRMACION_PEDIDO)
                .estado(Notificacion.EstadoNotificacion.PENDIENTE)
                .build();
    }

    @Test
    @DisplayName("Crear notificación exitosamente")
    void crear_exitoso() {

        when(usuarioClient.existeUsuario(1L)).thenReturn(true);
        when(usuarioClient.obtenerNombre(1L)).thenReturn("Juan Perez");
        when(usuarioClient.obtenerEmail(1L)).thenReturn("juan@test.com");
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);


        NotificacionResponseDTO response = notificacionService.crear(requestDTO);


        assertNotNull(response);
        assertEquals("Juan Perez", response.getUsuarioNombre());
        verify(notificacionRepository, atLeastOnce()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Crear notificación con usuario inexistente lanza excepción")
    void crear_usuarioInexistente_lanzaExcepcion() {

        when(usuarioClient.existeUsuario(1L)).thenReturn(false);


        assertThrows(RecursoNoEncontradoException.class,
                () -> notificacionService.crear(requestDTO));
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Crear notificación con tipo inválido lanza excepción")
    void crear_tipoInvalido_lanzaExcepcion() {

        when(usuarioClient.existeUsuario(1L)).thenReturn(true);
        requestDTO.setTipo("TIPO_INVALIDO");


        assertThrows(RuntimeException.class,
                () -> notificacionService.crear(requestDTO));
    }

    @Test
    @DisplayName("Reenviar notificación fallida exitosamente")
    void reenviar_exitoso() {

        notificacion.setEstado(Notificacion.EstadoNotificacion.FALLIDA);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);


        NotificacionResponseDTO response = notificacionService.reenviar(1L);


        assertNotNull(response);
        verify(notificacionRepository, atLeastOnce()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("Reenviar notificación no fallida lanza excepción")
    void reenviar_noFallida_lanzaExcepcion() {

        notificacion.setEstado(Notificacion.EstadoNotificacion.ENVIADA);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));


        assertThrows(RuntimeException.class,
                () -> notificacionService.reenviar(1L));
    }

    @Test
    @DisplayName("Listar todas las notificaciones")
    void listarTodas_exitoso() {

        when(notificacionRepository.findAll()).thenReturn(List.of(notificacion));


        List<NotificacionResponseDTO> response = notificacionService.listarTodas();


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Buscar notificación por ID exitosamente")
    void buscarPorId_exitoso() {

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));


        NotificacionResponseDTO response = notificacionService.buscarPorId(1L);


        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Buscar notificación por ID no encontrada lanza excepción")
    void buscarPorId_noEncontrada_lanzaExcepcion() {
        // Given
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());


        assertThrows(RecursoNoEncontradoException.class,
                () -> notificacionService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Listar notificaciones por usuario")
    void listarPorUsuario_exitoso() {

        when(notificacionRepository.findByUsuarioId(1L)).thenReturn(List.of(notificacion));


        List<NotificacionResponseDTO> response = notificacionService.listarPorUsuario(1L);


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Listar notificaciones pendientes")
    void listarPendientes_exitoso() {

        when(notificacionRepository.buscarPendientes()).thenReturn(List.of(notificacion));


        List<NotificacionResponseDTO> response = notificacionService.listarPendientes();


        assertNotNull(response);
        assertEquals(1, response.size());
    }

}
