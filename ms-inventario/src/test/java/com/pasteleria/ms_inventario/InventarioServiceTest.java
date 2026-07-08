package com.pasteleria.ms_inventario;

import com.pasteleria.ms_inventario.dto.IngredienteRequestDTO;
import com.pasteleria.ms_inventario.dto.IngredienteResponseDTO;
import com.pasteleria.ms_inventario.exception.RecursoNoEncontradoException;
import com.pasteleria.ms_inventario.exception.StockInsuficienteException;
import com.pasteleria.ms_inventario.model.Ingrediente;
import com.pasteleria.ms_inventario.repository.IngredienteRepository;
import com.pasteleria.ms_inventario.service.InventarioService;
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
public class InventarioServiceTest {
    @Mock
    private IngredienteRepository ingredienteRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private IngredienteRequestDTO requestDTO;
    private Ingrediente ingrediente;

    @BeforeEach
    void setUp() {
        requestDTO = new IngredienteRequestDTO();
        requestDTO.setNombre("Harina");
        requestDTO.setDescripcion("Harina de trigo");
        requestDTO.setStockActual(100.0);
        requestDTO.setStockMinimo(10.0);
        requestDTO.setUnidadMedida("kg");

        ingrediente = Ingrediente.builder()
                .id(1L)
                .nombre("Harina")
                .descripcion("Harina de trigo")
                .stockActual(100.0)
                .stockMinimo(10.0)
                .unidadMedida("kg")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Crear ingrediente exitosamente")
    void crear_exitoso() {

        when(ingredienteRepository.existsByNombre("Harina")).thenReturn(false);
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);


        IngredienteResponseDTO response = inventarioService.crear(requestDTO);


        assertNotNull(response);
        assertEquals("Harina", response.getNombre());
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Crear ingrediente con nombre duplicado lanza excepción")
    void crear_nombreDuplicado_lanzaExcepcion() {

        when(ingredienteRepository.existsByNombre("Harina")).thenReturn(true);


        assertThrows(RuntimeException.class,
                () -> inventarioService.crear(requestDTO));
        verify(ingredienteRepository, never()).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Crear ingrediente con stock mínimo mayor al actual lanza excepción")
    void crear_stockMinimoMayorActual_lanzaExcepcion() {

        when(ingredienteRepository.existsByNombre("Harina")).thenReturn(false);
        requestDTO.setStockMinimo(200.0);


        assertThrows(RuntimeException.class,
                () -> inventarioService.crear(requestDTO));
    }

    @Test
    @DisplayName("Listar todos los ingredientes")
    void listarTodos_exitoso() {

        when(ingredienteRepository.findAll()).thenReturn(List.of(ingrediente));


        List<IngredienteResponseDTO> response = inventarioService.listarTodos();


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Buscar ingrediente por ID exitosamente")
    void buscarPorId_exitoso() {

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));


        IngredienteResponseDTO response = inventarioService.buscarPorId(1L);


        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Buscar ingrediente por ID no encontrado lanza excepción")
    void buscarPorId_noEncontrado_lanzaExcepcion() {
        // Given
        when(ingredienteRepository.findById(99L)).thenReturn(Optional.empty());


        assertThrows(RecursoNoEncontradoException.class,
                () -> inventarioService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Agregar stock exitosamente")
    void agregarStock_exitoso() {

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);


        IngredienteResponseDTO response = inventarioService.agregarStock(1L, 50.0);


        assertNotNull(response);
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Agregar stock con cantidad inválida lanza excepción")
    void agregarStock_cantidadInvalida_lanzaExcepcion() {

        assertThrows(RuntimeException.class,
                () -> inventarioService.agregarStock(1L, -10.0));
    }

    @Test
    @DisplayName("Reducir stock exitosamente")
    void reducirStock_exitoso() {

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);


        IngredienteResponseDTO response = inventarioService.reducirStock(1L, 20.0);


        assertNotNull(response);
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Reducir stock insuficiente lanza excepción")
    void reducirStock_insuficiente_lanzaExcepcion() {

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));


        assertThrows(StockInsuficienteException.class,
                () -> inventarioService.reducirStock(1L, 200.0));
    }

    @Test
    @DisplayName("Desactivar ingrediente exitosamente")
    void desactivar_exitoso() {

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);


        inventarioService.desactivar(1L);


        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
        assertFalse(ingrediente.isActivo());
    }

    @Test
    @DisplayName("Eliminar ingrediente exitosamente")
    void eliminar_exitoso() {

        when(ingredienteRepository.existsById(1L)).thenReturn(true);


        inventarioService.eliminar(1L);


        verify(ingredienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar ingrediente no encontrado lanza excepción")
    void eliminar_noEncontrado_lanzaExcepcion() {
        // Given
        when(ingredienteRepository.existsById(99L)).thenReturn(false);


        assertThrows(RecursoNoEncontradoException.class,
                () -> inventarioService.eliminar(99L));
    }

}
