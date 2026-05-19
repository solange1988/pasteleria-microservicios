package com.pasteleria.ms_auth.service;


import com.pasteleria.ms_auth.dto.AuthResponseDTO;
import com.pasteleria.ms_auth.dto.LoginRequestDTO;
import com.pasteleria.ms_auth.dto.RegisterRequestDTO;
import com.pasteleria.ms_auth.exception.CredencialesInvalidasException;
import com.pasteleria.ms_auth.exception.EmailDuplicadoException;
import jakarta.transaction.Transactional;
import com.pasteleria.ms_auth.model.Rol;
import com.pasteleria.ms_auth.model.Usuario;
import org.springframework.stereotype.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pasteleria.ms_auth.repository.UsuarioRepository;


@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private UsuarioRepository usuarioRepository = null;

    public AuthService(UsuarioRepository usuarioRepositoryusuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public AuthResponseDTO registrar(RegisterRequestDTO dto) {
        log.info("Iniciando registro para email: {}", dto.getEmail());

       
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Registro rechazado — email ya existe: {}", dto.getEmail());
            throw new EmailDuplicadoException(
                    "El email '" + dto.getEmail() + "' ya está registrado en el sistema"
            );
        }


        Rol rol;
        try {
            rol = Rol.valueOf(dto.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Rol inválido recibido: {}", dto.getRol());
            throw new RuntimeException(
                    "Rol inválido: '" + dto.getRol() + "'. Use: ADMINISTRADOR, CLIENTE o PASTELERO"
            );
        }

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .password(dto.getPassword()) // Sin encriptación por simplicidad
                .rol(rol)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado OK — ID: {}, email: {}, rol: {}",
                guardado.getId(), guardado.getEmail(), guardado.getRol());

        return AuthResponseDTO.builder()
                .id(guardado.getId())
                .nombre(guardado.getNombre())
                .email(guardado.getEmail())
                .rol(guardado.getRol().name())
                .activo(guardado.isActivo())
                .mensaje("Registro exitoso")
                .build();
    }

    @Transactional()
    public AuthResponseDTO login(LoginRequestDTO dto) {
        log.info("Intento de login para: {}", dto.getEmail());


        Usuario usuario = usuarioRepository
                .findByEmailAndActivoTrue(dto.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login fallido — usuario no encontrado: {}", dto.getEmail());
                    return new CredencialesInvalidasException("Credenciales inválidas");
                });


        if (!dto.getPassword().equals(usuario.getPassword())) {
            log.warn("Login fallido — contraseña incorrecta para: {}", dto.getEmail());
            throw new CredencialesInvalidasException("Credenciales inválidas");
        }

        log.info("Login exitoso — usuario: {}, rol: {}",
                usuario.getEmail(), usuario.getRol());

        return AuthResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .activo(usuario.isActivo())
                .mensaje("Login exitoso")
                .build();
    }
}

