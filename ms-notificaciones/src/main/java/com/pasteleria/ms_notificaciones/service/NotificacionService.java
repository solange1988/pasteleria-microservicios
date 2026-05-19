package com.pasteleria.ms_notificaciones.service;

import com.pasteleria.ms_notificaciones.cliente.UsuarioClient;
import com.pasteleria.ms_notificaciones.dto.*;
import com.pasteleria.ms_notificaciones.exception.*;
import com.pasteleria.ms_notificaciones.model.Notificacion;
import com.pasteleria.ms_notificaciones.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional


public class NotificacionService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificacionService.class);

    private final NotificacionRepository notificacionRepository;
    private final UsuarioClient usuarioClient;

    public NotificacionService(
            NotificacionRepository notificacionRepository,
            UsuarioClient usuarioClient) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioClient = usuarioClient;
    }


    public NotificacionResponseDTO crear(NotificacionRequestDTO dto) {
        log.info("Creando notificación — usuario ID: {}, tipo: {}",
                dto.getUsuarioId(), dto.getTipo());


        if (!usuarioClient.existeUsuario(dto.getUsuarioId())) {
            log.warn("Notificación rechazada — usuario no encontrado ID: {}",
                    dto.getUsuarioId());
            throw new RecursoNoEncontradoException(
                    "El usuario con ID " + dto.getUsuarioId()
                            + " no existe"
            );
        }


        Notificacion.TipoNotificacion tipo;
        try {
            tipo = Notificacion.TipoNotificacion.valueOf(
                    dto.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Tipo de notificación inválido: {}", dto.getTipo());
            throw new RuntimeException(
                    "Tipo inválido: " + dto.getTipo()
            );
        }


        String nombreUsuario = usuarioClient
                .obtenerNombre(dto.getUsuarioId());
        String emailUsuario = usuarioClient
                .obtenerEmail(dto.getUsuarioId());

        log.debug("Preparando notificación para: {} — {}",
                nombreUsuario, emailUsuario);

        Notificacion notificacion = Notificacion.builder()
                .usuarioId(dto.getUsuarioId())
                .usuarioNombre(nombreUsuario)
                .usuarioEmail(emailUsuario)
                .pedidoId(dto.getPedidoId())
                .titulo(dto.getTitulo())
                .mensaje(dto.getMensaje())
                .tipo(tipo)
                .estado(Notificacion.EstadoNotificacion.PENDIENTE)
                .build();

        Notificacion guardada = notificacionRepository
                .save(notificacion);
        log.info("Notificación creada OK — ID: {}", guardada.getId());


        NotificacionResponseDTO response = enviarNotificacion(guardada);
        return response;
    }


    private NotificacionResponseDTO enviarNotificacion(
            Notificacion notificacion) {
        try {
            log.info("Enviando notificación ID: {} a {} — {}",
                    notificacion.getId(),
                    notificacion.getUsuarioNombre(),
                    notificacion.getUsuarioEmail());


            Thread.sleep(100);

            notificacion.setEstado(
                    Notificacion.EstadoNotificacion.ENVIADA);
            notificacion.setFechaEnvio(LocalDateTime.now());

            Notificacion enviada = notificacionRepository
                    .save(notificacion);

            log.info("Notificación enviada OK — ID: {}, tipo: {}, usuario: {}",
                    enviada.getId(),
                    enviada.getTipo(),
                    enviada.getUsuarioEmail());

            return mapearADTO(enviada);

        } catch (Exception e) {
            log.error("Error al enviar notificación ID: {} — {}",
                    notificacion.getId(), e.getMessage());

            notificacion.setEstado(
                    Notificacion.EstadoNotificacion.FALLIDA);
            notificacionRepository.save(notificacion);

            return mapearADTO(notificacion);
        }
    }


    public NotificacionResponseDTO reenviar(Long id) {
        log.info("Reenviando notificación ID: {}", id);

        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Notificación no encontrada — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Notificación no encontrada con ID: " + id
                    );
                });


        if (notificacion.getEstado() !=
                Notificacion.EstadoNotificacion.FALLIDA) {
            log.warn("Reenvío rechazado — notificación ID {} " +
                    "no está en estado FALLIDA", id);
            throw new RuntimeException(
                    "Solo se pueden reenviar notificaciones FALLIDAS"
            );
        }

        notificacion.setEstado(
                Notificacion.EstadoNotificacion.PENDIENTE);
        return enviarNotificacion(notificacion);
    }


    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarTodas() {
        log.info("Listando todas las notificaciones");
        return notificacionRepository.findAll()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public NotificacionResponseDTO buscarPorId(Long id) {
        log.info("Buscando notificación ID: {}", id);
        Notificacion notificacion = notificacionRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn("Notificación no encontrada — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Notificación no encontrada con ID: " + id
                    );
                });
        return mapearADTO(notificacion);
    }


    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPorUsuario(
            Long usuarioId) {
        log.info("Listando notificaciones usuario ID: {}", usuarioId);
        return notificacionRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPorPedido(
            Long pedidoId) {
        log.info("Listando notificaciones pedido ID: {}", pedidoId);
        return notificacionRepository.findByPedidoId(pedidoId)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPorEstado(
            String estado) {
        log.info("Listando notificaciones estado: {}", estado);
        try {
            Notificacion.EstadoNotificacion estadoEnum =
                    Notificacion.EstadoNotificacion.valueOf(
                            estado.toUpperCase());
            return notificacionRepository.findByEstado(estadoEnum)
                    .stream()
                    .map(this::mapearADTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("Estado inválido: {}", estado);
            throw new RuntimeException(
                    "Estado inválido: " + estado
                            + ". Válidos: PENDIENTE, ENVIADA, FALLIDA"
            );
        }
    }


    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPendientes() {
        log.info("Listando notificaciones pendientes");
        return notificacionRepository.buscarPendientes()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> buscarPorFecha(
            LocalDateTime inicio, LocalDateTime fin) {
        log.info("Buscando notificaciones entre {} y {}",
                inicio, fin);
        return notificacionRepository
                .buscarPorRangoFecha(inicio, fin)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    private NotificacionResponseDTO mapearADTO(Notificacion n) {
        return NotificacionResponseDTO.builder()
                .id(n.getId())
                .usuarioId(n.getUsuarioId())
                .usuarioNombre(n.getUsuarioNombre())
                .usuarioEmail(n.getUsuarioEmail())
                .pedidoId(n.getPedidoId())
                .titulo(n.getTitulo())
                .mensaje(n.getMensaje())
                .tipo(n.getTipo().name())
                .estado(n.getEstado().name())
                .fechaEnvio(n.getFechaEnvio())
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .build();
    }

}
