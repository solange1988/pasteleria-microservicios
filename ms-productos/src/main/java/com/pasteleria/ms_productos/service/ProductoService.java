package com.pasteleria.ms_productos.service;

import com.pasteleria.ms_productos.dto.*;
import com.pasteleria.ms_productos.exception.*;
import com.pasteleria.ms_productos.model.Producto;
import com.pasteleria.ms_productos.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;



@Service
@Transactional

public class ProductoService {

    private static final Logger log =
            LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;
    private final com.pasteleria.ms_productos.client.CategoriaClient categoriaClient;

    public ProductoService(ProductoRepository productoRepository,
                           com.pasteleria.ms_productos.client.CategoriaClient categoriaClient) {
        this.productoRepository = productoRepository;
        this.categoriaClient = categoriaClient;
    }


    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        log.info("Creando producto: {}", dto.getNombre());

        // Regla de negocio: validar que la categoría existe en ms-categorias
        if (!categoriaClient.existeCategoria(dto.getCategoriaId())) {
            log.warn("Categoría no encontrada — ID: {}", dto.getCategoriaId());
            throw new RecursoNoEncontradoException(
                    "La categoría con ID " + dto.getCategoriaId() + " no existe"
            );
        }


        if (productoRepository.existsByNombre(dto.getNombre())) {
            log.warn("Nombre de producto duplicado: {}", dto.getNombre());
            throw new NombreDuplicadoException(
                    "El producto '" + dto.getNombre() + "' ya existe"
            );
        }


        String nombreCategoria = categoriaClient
                .obtenerNombreCategoria(dto.getCategoriaId());

        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .categoriaId(dto.getCategoriaId())
                .categoriaNombre(nombreCategoria)
                .disponible(true)
                .build();

        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado OK — ID: {}", guardado.getId());
        return mapearADTO(guardado);
    }


    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarTodos() {
        log.info("Listando todos los productos");
        return productoRepository.findAll()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarDisponibles() {
        log.info("Listando productos disponibles");
        return productoRepository.findByDisponibleTrue()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO buscarPorId(Long id) {
        log.info("Buscando producto por ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Producto no encontrado con ID: " + id
                    );
                });
        return mapearADTO(producto);
    }


    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorCategoria(Long categoriaId) {
        log.info("Listando productos por categoría ID: {}", categoriaId);
        return productoRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarPorNombre(String nombre) {
        log.info("Buscando productos por nombre: {}", nombre);
        return productoRepository.buscarPorNombre(nombre)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarPorRangoPrecio(
            BigDecimal min, BigDecimal max) {
        log.info("Buscando productos entre $ {} y $ {}", min, max);
        return productoRepository.buscarPorRangoPrecio(min, max)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> productosConStockBajo(Integer limite) {
        log.info("Buscando productos con stock <= {}", limite);
        return productoRepository.buscarConStockBajo(limite)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
        log.info("Actualizando producto ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado para actualizar — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Producto no encontrado con ID: " + id
                    );
                });

        if (!producto.getCategoriaId().equals(dto.getCategoriaId())) {
            if (!categoriaClient.existeCategoria(dto.getCategoriaId())) {
                log.warn("Categoría no encontrada — ID: {}", dto.getCategoriaId());
                throw new RecursoNoEncontradoException(
                        "La categoría con ID " + dto.getCategoriaId() + " no existe"
                );
            }
        }

        if (!producto.getNombre().equals(dto.getNombre()) &&
                productoRepository.existsByNombre(dto.getNombre())) {
            log.warn("Nombre duplicado en actualización: {}", dto.getNombre());
            throw new NombreDuplicadoException(
                    "El producto '" + dto.getNombre() + "' ya existe"
            );
        }

        String nombreCategoria = categoriaClient
                .obtenerNombreCategoria(dto.getCategoriaId());

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoriaId(dto.getCategoriaId());
        producto.setCategoriaNombre(nombreCategoria);

        Producto actualizado = productoRepository.save(producto);
        log.info("Producto actualizado OK — ID: {}", actualizado.getId());
        return mapearADTO(actualizado);
    }


    public ProductoResponseDTO actualizarStock(Long id, Integer nuevoStock) {
        log.info("Actualizando stock producto ID: {} → {}", id, nuevoStock);

        if (nuevoStock < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con ID: " + id
                ));

        producto.setStock(nuevoStock);
        producto.setDisponible(nuevoStock > 0);

        Producto actualizado = productoRepository.save(producto);
        log.info("Stock actualizado OK — ID: {}, stock: {}", id, nuevoStock);
        return mapearADTO(actualizado);
    }


    public void desactivar(Long id) {
        log.info("Desactivando producto ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con ID: " + id
                ));
        producto.setDisponible(false);
        productoRepository.save(producto);
        log.info("Producto desactivado OK — ID: {}", id);
    }


    public void eliminar(Long id) {
        log.info("Eliminando producto ID: {}", id);
        if (!productoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    "Producto no encontrado con ID: " + id
            );
        }
        productoRepository.deleteById(id);
        log.info("Producto eliminado OK — ID: {}", id);
    }


    private ProductoResponseDTO mapearADTO(Producto p) {
        return ProductoResponseDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .stock(p.getStock())
                .categoriaId(p.getCategoriaId())
                .categoriaNombre(p.getCategoriaNombre())
                .disponible(p.isDisponible())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
