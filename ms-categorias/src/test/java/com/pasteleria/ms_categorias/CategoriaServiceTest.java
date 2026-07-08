package com.pasteleria.ms_categorias;

import com.pasteleria.ms_categorias.dto.CategoriaRequestDTO;
import com.pasteleria.ms_categorias.dto.CategoriaResponseDTO;
import com.pasteleria.ms_categorias.exception.NombreDuplicadoException;
import com.pasteleria.ms_categorias.exception.RecursoNoEncontradoException;
import com.pasteleria.ms_categorias.model.Categoria;
import com.pasteleria.ms_categorias.repository.CategoriaRepository;
import com.pasteleria.ms_categorias.service.CategoriaService;
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
public class CategoriaServiceTest {
    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private CategoriaRequestDTO requestDTO;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        requestDTO = new CategoriaRequestDTO();
        requestDTO.setNombre("Tortas");
        requestDTO.setDescripcion("Categoría de tortas");

        categoria = Categoria.builder()
                .id(1L)
                .nombre("Tortas")
                .descripcion("Categoría de tortas")
                .activa(true)
                .build();
    }

    @Test
    @DisplayName("Crear categoría exitosamente")
    void crear_exitoso() {

        when(categoriaRepository.existsByNombre("Tortas")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);


        CategoriaResponseDTO response = categoriaService.crear(requestDTO);


        assertNotNull(response);
        assertEquals("Tortas", response.getNombre());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Crear categoría con nombre duplicado lanza excepción")
    void crear_nombreDuplicado_lanzaExcepcion() {

        when(categoriaRepository.existsByNombre("Tortas")).thenReturn(true);


        assertThrows(NombreDuplicadoException.class,
                () -> categoriaService.crear(requestDTO));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Listar todas las categorías")
    void listarTodas_exitoso() {

        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));


        List<CategoriaResponseDTO> response = categoriaService.listarTodas();


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Listar categorías activas")
    void listarActivas_exitoso() {

        when(categoriaRepository.findByActivaTrue()).thenReturn(List.of(categoria));


        List<CategoriaResponseDTO> response = categoriaService.listarActivas();


        assertNotNull(response);
        assertEquals(1, response.size());
        assertTrue(response.get(0).isActiva());
    }

    @Test
    @DisplayName("Buscar categoría por ID exitosamente")
    void buscarPorId_exitoso() {

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));


        CategoriaResponseDTO response = categoriaService.buscarPorId(1L);


        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Buscar categoría por ID no encontrada lanza excepción")
    void buscarPorId_noEncontrada_lanzaExcepcion() {
        // Given
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RecursoNoEncontradoException.class,
                () -> categoriaService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Actualizar categoría exitosamente")
    void actualizar_exitoso() {
        // Given
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNombre("Tortas Nuevas")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        requestDTO.setNombre("Tortas Nuevas");


        CategoriaResponseDTO response = categoriaService.actualizar(1L, requestDTO);


        assertNotNull(response);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Desactivar categoría exitosamente")
    void desactivar_exitoso() {

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);


        categoriaService.desactivar(1L);


        verify(categoriaRepository, times(1)).save(any(Categoria.class));
        assertFalse(categoria.isActiva());
    }

    @Test
    @DisplayName("Eliminar categoría exitosamente")
    void eliminar_exitoso() {

        when(categoriaRepository.existsById(1L)).thenReturn(true);


        categoriaService.eliminar(1L);


        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar categoría no encontrada lanza excepción")
    void eliminar_noEncontrada_lanzaExcepcion() {
        // Given
        when(categoriaRepository.existsById(99L)).thenReturn(false);


        assertThrows(RecursoNoEncontradoException.class,
                () -> categoriaService.eliminar(99L));
    }

}
