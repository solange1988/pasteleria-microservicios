package com.pasteleria.ms_usuarios;

import com.pasteleria.ms_usuarios.dto.UsuarioRequestDTO;
import com.pasteleria.ms_usuarios.dto.UsuarioResponseDTO;
import com.pasteleria.ms_usuarios.exception.EmailDuplicadoException;
import com.pasteleria.ms_usuarios.exception.RecursoNoEncontradoException;
import com.pasteleria.ms_usuarios.model.Usuario;
import com.pasteleria.ms_usuarios.repository.UsuarioRepository;
import com.pasteleria.ms_usuarios.service.UsuarioService;
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
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO requestDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        requestDTO = new UsuarioRequestDTO();
        requestDTO.setNombre("Juan");
        requestDTO.setApellido("Perez");
        requestDTO.setEmail("juan@test.com");
        requestDTO.setTelefono("123456789");
        requestDTO.setDireccion("Calle 123");
        requestDTO.setRol("CLIENTE");

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .telefono("123456789")
                .direccion("Calle 123")
                .rol(Usuario.Rol.CLIENTE)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Crear usuario exitosamente")
    void crear_exitoso() {

        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);


        UsuarioResponseDTO response = usuarioService.crear(requestDTO);


        assertNotNull(response);
        assertEquals("juan@test.com", response.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Crear usuario con email duplicado lanza excepción")
    void crear_emailDuplicado_lanzaExcepcion() {

        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(true);


        assertThrows(EmailDuplicadoException.class,
                () -> usuarioService.crear(requestDTO));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Crear usuario con rol inválido lanza excepción")
    void crear_rolInvalido_lanzaExcepcion() {

        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(false);
        requestDTO.setRol("ROL_INVALIDO");


        assertThrows(RuntimeException.class,
                () -> usuarioService.crear(requestDTO));
    }

    @Test
    @DisplayName("Listar todos los usuarios")
    void listarTodos_exitoso() {

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));


        List<UsuarioResponseDTO> response = usuarioService.listarTodos();


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Listar usuarios activos")
    void listarActivos_exitoso() {

        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of(usuario));


        List<UsuarioResponseDTO> response = usuarioService.listarActivos();


        assertNotNull(response);
        assertEquals(1, response.size());
        assertTrue(response.get(0).isActivo());
    }

    @Test
    @DisplayName("Buscar usuario por ID exitosamente")
    void buscarPorId_exitoso() {

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));


        UsuarioResponseDTO response = usuarioService.buscarPorId(1L);


        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Buscar usuario por ID no encontrado lanza excepción")
    void buscarPorId_noEncontrado_lanzaExcepcion() {

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());


        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Buscar usuario por email exitosamente")
    void buscarPorEmail_exitoso() {

        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));


        UsuarioResponseDTO response = usuarioService.buscarPorEmail("juan@test.com");


        assertNotNull(response);
        assertEquals("juan@test.com", response.getEmail());
    }

    @Test
    @DisplayName("Buscar usuario por email no encontrado lanza excepción")
    void buscarPorEmail_noEncontrado_lanzaExcepcion() {

        when(usuarioRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());


        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.buscarPorEmail("noexiste@test.com"));
    }

    @Test
    @DisplayName("Desactivar usuario exitosamente")
    void desactivar_exitoso() {

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);


        usuarioService.desactivar(1L);


        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        assertFalse(usuario.isActivo());
    }

    @Test
    @DisplayName("Eliminar usuario exitosamente")
    void eliminar_exitoso() {

        when(usuarioRepository.existsById(1L)).thenReturn(true);


        usuarioService.eliminar(1L);


        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar usuario no encontrado lanza excepción")
    void eliminar_noEncontrado_lanzaExcepcion() {

        when(usuarioRepository.existsById(99L)).thenReturn(false);


        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.eliminar(99L));
    }

}
