package com.pasteleria.ms_productos;

import com.pasteleria.ms_productos.client.CategoriaClient;
import com.pasteleria.ms_productos.dto.ProductoRequestDTO;
import com.pasteleria.ms_productos.dto.ProductoResponseDTO;
import com.pasteleria.ms_productos.exception.NombreDuplicadoException;
import com.pasteleria.ms_productos.exception.RecursoNoEncontradoException;
import com.pasteleria.ms_productos.model.Producto;
import com.pasteleria.ms_productos.repository.ProductoRepository;
import com.pasteleria.ms_productos.service.ProductoService;
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
public class ProductoServiceTest {
    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaClient categoriaClient;

    @InjectMocks
    private ProductoService productoService;

    private ProductoRequestDTO requestDTO;
    private Producto producto;

    @BeforeEach
    void setUp() {
        requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Torta de chocolate");
        requestDTO.setDescripcion("Torta de chocolate con relleno");
        requestDTO.setPrecio(BigDecimal.valueOf(15000));
        requestDTO.setStock(10);
        requestDTO.setCategoriaId(1L);

        producto = Producto.builder()
                .id(1L)
                .nombre("Torta de chocolate")
                .descripcion("Torta de chocolate con relleno")
                .precio(BigDecimal.valueOf(15000))
                .stock(10)
                .categoriaId(1L)
                .categoriaNombre("Tortas")
                .disponible(true)
                .build();
    }

    @Test
    @DisplayName("Crear producto exitosamente")
    void crear_exitoso() {

        when(categoriaClient.existeCategoria(1L)).thenReturn(true);
        when(productoRepository.existsByNombre("Torta de chocolate")).thenReturn(false);
        when(categoriaClient.obtenerNombreCategoria(1L)).thenReturn("Tortas");
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);


        ProductoResponseDTO response = productoService.crear(requestDTO);


        assertNotNull(response);
        assertEquals("Torta de chocolate", response.getNombre());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Crear producto con categoría inexistente lanza excepción")
    void crear_categoriaInexistente_lanzaExcepcion() {

        when(categoriaClient.existeCategoria(1L)).thenReturn(false);


        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.crear(requestDTO));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Crear producto con nombre duplicado lanza excepción")
    void crear_nombreDuplicado_lanzaExcepcion() {

        when(categoriaClient.existeCategoria(1L)).thenReturn(true);
        when(productoRepository.existsByNombre("Torta de chocolate")).thenReturn(true);


        assertThrows(NombreDuplicadoException.class,
                () -> productoService.crear(requestDTO));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Listar todos los productos")
    void listarTodos_exitoso() {

        when(productoRepository.findAll()).thenReturn(List.of(producto));


        List<ProductoResponseDTO> response = productoService.listarTodos();


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Listar productos disponibles")
    void listarDisponibles_exitoso() {

        when(productoRepository.findByDisponibleTrue()).thenReturn(List.of(producto));


        List<ProductoResponseDTO> response = productoService.listarDisponibles();


        assertNotNull(response);
        assertEquals(1, response.size());
        assertTrue(response.get(0).isDisponible());
    }

    @Test
    @DisplayName("Buscar producto por ID exitosamente")
    void buscarPorId_exitoso() {

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));


        ProductoResponseDTO response = productoService.buscarPorId(1L);


        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("Buscar producto por ID no encontrado lanza excepción")
    void buscarPorId_noEncontrado_lanzaExcepcion() {

        when(productoRepository.findById(99L)).thenReturn(Optional.empty());


        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Listar productos por categoría")
    void listarPorCategoria_exitoso() {

        when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(producto));


        List<ProductoResponseDTO> response = productoService.listarPorCategoria(1L);


        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("Actualizar stock exitosamente")
    void actualizarStock_exitoso() {

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);


        ProductoResponseDTO response = productoService.actualizarStock(1L, 20);


        assertNotNull(response);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Actualizar stock con valor negativo lanza excepción")
    void actualizarStock_negativo_lanzaExcepcion() {

        assertThrows(RuntimeException.class,
                () -> productoService.actualizarStock(1L, -5));
    }

    @Test
    @DisplayName("Desactivar producto exitosamente")
    void desactivar_exitoso() {

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);


        productoService.desactivar(1L);


        verify(productoRepository, times(1)).save(any(Producto.class));
        assertFalse(producto.isDisponible());
    }

    @Test
    @DisplayName("Eliminar producto exitosamente")
    void eliminar_exitoso() {

        when(productoRepository.existsById(1L)).thenReturn(true);


        productoService.eliminar(1L);


        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar producto no encontrado lanza excepción")
    void eliminar_noEncontrado_lanzaExcepcion() {

        when(productoRepository.existsById(99L)).thenReturn(false);


        assertThrows(RecursoNoEncontradoException.class,
                () -> productoService.eliminar(99L));
    }

}
