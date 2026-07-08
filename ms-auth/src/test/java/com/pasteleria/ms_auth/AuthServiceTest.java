package com.pasteleria.ms_auth;

import com.pasteleria.ms_auth.dto.AuthResponseDTO;
import com.pasteleria.ms_auth.dto.LoginRequestDTO;
import com.pasteleria.ms_auth.dto.RegisterRequestDTO;
import com.pasteleria.ms_auth.exception.CredencialesInvalidasException;
import com.pasteleria.ms_auth.exception.EmailDuplicadoException;
import com.pasteleria.ms_auth.model.Rol;
import com.pasteleria.ms_auth.model.Usuario;
import com.pasteleria.ms_auth.repository.UsuarioRepository;
import com.pasteleria.ms_auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterRequestDTO();
        registerDTO.setNombre("Juan Perez");
        registerDTO.setEmail("juan@test.com");
        registerDTO.setPassword("123456");
        registerDTO.setRol("CLIENTE");

        loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("juan@test.com");
        loginDTO.setPassword("123456");

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Perez")
                .email("juan@test.com")
                .password("123456")
                .rol(Rol.CLIENTE)
                .activo(true)
                .build();
    }



    @Test
    @DisplayName("Registrar usuario exitosamente")
    void registrar_exitoso() {

        when(usuarioRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);


        AuthResponseDTO response = authService.registrar(registerDTO);


        assertNotNull(response);
        assertEquals("juan@test.com", response.getEmail());
        assertEquals("Registro exitoso", response.getMensaje());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registrar usuario con email duplicado lanza excepción")
    void registrar_emailDuplicado_lanzaExcepcion() {

        when(usuarioRepository.existsByEmail(registerDTO.getEmail())).thenReturn(true);


        assertThrows(EmailDuplicadoException.class,
                () -> authService.registrar(registerDTO));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registrar usuario con rol inválido lanza excepción")
    void registrar_rolInvalido_lanzaExcepcion() {

        when(usuarioRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
        registerDTO.setRol("ROL_INVALIDO");


        assertThrows(RuntimeException.class,
                () -> authService.registrar(registerDTO));
    }



    @Test
    @DisplayName("Login exitoso con credenciales correctas")
    void login_exitoso() {

        when(usuarioRepository.findByEmailAndActivoTrue(loginDTO.getEmail()))
                .thenReturn(Optional.of(usuario));


        AuthResponseDTO response = authService.login(loginDTO);


        assertNotNull(response);
        assertEquals("juan@test.com", response.getEmail());
        assertEquals("Login exitoso", response.getMensaje());
    }

    @Test
    @DisplayName("Login con usuario no encontrado lanza excepción")
    void login_usuarioNoEncontrado_lanzaExcepcion() {

        when(usuarioRepository.findByEmailAndActivoTrue(loginDTO.getEmail()))
                .thenReturn(Optional.empty());


        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login(loginDTO));
    }

    @Test
    @DisplayName("Login con contraseña incorrecta lanza excepción")
    void login_passwordIncorrecta_lanzaExcepcion() {

        when(usuarioRepository.findByEmailAndActivoTrue(loginDTO.getEmail()))
                .thenReturn(Optional.of(usuario));
        loginDTO.setPassword("passwordIncorrecta");


        assertThrows(CredencialesInvalidasException.class,
                () -> authService.login(loginDTO));
    }

}

