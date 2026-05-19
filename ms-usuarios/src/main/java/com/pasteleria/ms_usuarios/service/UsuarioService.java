package com.pasteleria.ms_usuarios.service;


import com.pasteleria.ms_usuarios.dto.UsuarioRequestDTO;
import com.pasteleria.ms_usuarios.dto.UsuarioResponseDTO;
import com.pasteleria.ms_usuarios.exception.EmailDuplicadoException;
import com.pasteleria.ms_usuarios.exception.RecursoNoEncontradoException;
import com.pasteleria.ms_usuarios.model.Usuario;
import com.pasteleria.ms_usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public class UsuarioService {

    private static final Logger log =
            LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        log.info("Creando usuario con email: {}", dto.getEmail());


        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email duplicado: {}", dto.getEmail());
            throw new EmailDuplicadoException(
                    "El email '" + dto.getEmail() + "' ya está registrado"
            );
        }


        Usuario.Rol rol;
        try {
            rol = Usuario.Rol.valueOf(dto.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Rol inválido: {}", dto.getRol());
            throw new RuntimeException(
                    "Rol inválido: '" + dto.getRol() +
                            "'. Use: ADMINISTRADOR, CLIENTE o PASTELERO"
            );
        }

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .rol(rol)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado OK — ID: {}", guardado.getId());
        return mapearADTO(guardado);
    }


    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        log.info("Listando todos los usuarios");
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarActivos() {
        log.info("Listando usuarios activos");
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        log.info("Buscando usuario por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Usuario no encontrado con ID: " + id
                    );
                });
        return mapearADTO(usuario);
    }


    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorEmail(String email) {
        log.info("Buscando usuario por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado — email: {}", email);
                    return new RecursoNoEncontradoException(
                            "Usuario no encontrado con email: " + email
                    );
                });
        return mapearADTO(usuario);
    }


    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorRol(String rol) {
        log.info("Listando usuarios con rol: {}", rol);
        try {
            Usuario.Rol rolEnum = Usuario.Rol.valueOf(rol.toUpperCase());
            return usuarioRepository.findByRol(rolEnum)
                    .stream()
                    .map(this::mapearADTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.error("Rol inválido en búsqueda: {}", rol);
            throw new RuntimeException("Rol inválido: " + rol);
        }
    }


    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarPorNombre(String nombre) {
        log.info("Buscando usuarios por nombre: {}", nombre);
        return usuarioRepository.buscarPorNombre(nombre)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        log.info("Actualizando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado para actualizar — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Usuario no encontrado con ID: " + id
                    );
                });


        if (!usuario.getEmail().equals(dto.getEmail()) &&
                usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email duplicado en actualización: {}", dto.getEmail());
            throw new EmailDuplicadoException(
                    "El email '" + dto.getEmail() + "' ya está en uso"
            );
        }

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());
        usuario.setRol(Usuario.Rol.valueOf(dto.getRol().toUpperCase()));

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado OK — ID: {}", actualizado.getId());
        return mapearADTO(actualizado);
    }


    public void desactivar(Long id) {
        log.info("Desactivando usuario ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado para desactivar — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Usuario no encontrado con ID: " + id
                    );
                });
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado OK — ID: {}", id);
    }


    public void eliminar(Long id) {
        log.info("Eliminando usuario ID: {}", id);
        if (!usuarioRepository.existsById(id)) {
            log.warn("Usuario no encontrado para eliminar — ID: {}", id);
            throw new RecursoNoEncontradoException(
                    "Usuario no encontrado con ID: " + id
            );
        }
        usuarioRepository.deleteById(id);
        log.info("Usuario eliminado OK — ID: {}", id);
    }


    private UsuarioResponseDTO mapearADTO(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .email(u.getEmail())
                .telefono(u.getTelefono())
                .direccion(u.getDireccion())
                .rol(u.getRol().name())
                .activo(u.isActivo())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}
