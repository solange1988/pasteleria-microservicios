package com.pasteleria.ms_inventario.service;


import com.pasteleria.ms_inventario.dto.*;
import com.pasteleria.ms_inventario.exception.*;
import com.pasteleria.ms_inventario.model.Ingrediente;
import com.pasteleria.ms_inventario.repository.IngredienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional

public class InventarioService {

    private static final Logger log =
            LoggerFactory.getLogger(InventarioService.class);

    private final IngredienteRepository ingredienteRepository;

    public InventarioService(
            IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }


    public IngredienteResponseDTO crear(IngredienteRequestDTO dto) {
        log.info("Creando ingrediente: {}", dto.getNombre());


        if (ingredienteRepository.existsByNombre(dto.getNombre())) {
            log.warn("Nombre duplicado: {}", dto.getNombre());
            throw new RuntimeException(
                    "El ingrediente '" + dto.getNombre() + "' ya existe"
            );
        }


        if (dto.getStockMinimo() > dto.getStockActual()) {
            log.warn("Stock mínimo mayor al actual — nombre: {}",
                    dto.getNombre());
            throw new RuntimeException(
                    "El stock mínimo no puede ser mayor al stock actual"
            );
        }

        Ingrediente ingrediente = Ingrediente.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .stockActual(dto.getStockActual())
                .stockMinimo(dto.getStockMinimo())
                .unidadMedida(dto.getUnidadMedida())
                .activo(true)
                .build();

        Ingrediente guardado = ingredienteRepository.save(ingrediente);
        log.info("Ingrediente creado OK — ID: {}, stock: {} {}",
                guardado.getId(), guardado.getStockActual(),
                guardado.getUnidadMedida());
        return mapearADTO(guardado);
    }


    @Transactional(readOnly = true)
    public List<IngredienteResponseDTO> listarTodos() {
        log.info("Listando todos los ingredientes");
        return ingredienteRepository.findAll()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<IngredienteResponseDTO> listarActivos() {
        log.info("Listando ingredientes activos");
        return ingredienteRepository.findByActivoTrue()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public IngredienteResponseDTO buscarPorId(Long id) {
        log.info("Buscando ingrediente ID: {}", id);
        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ingrediente no encontrado — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Ingrediente no encontrado con ID: " + id
                    );
                });
        return mapearADTO(ingrediente);
    }


    @Transactional(readOnly = true)
    public List<IngredienteResponseDTO> buscarPorNombre(String nombre) {
        log.info("Buscando ingredientes por nombre: {}", nombre);
        return ingredienteRepository.buscarPorNombre(nombre)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<IngredienteResponseDTO> listarConStockBajo() {
        log.warn("Consultando ingredientes con stock bajo");
        return ingredienteRepository.buscarConStockBajo()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<IngredienteResponseDTO> listarSinStock() {
        log.warn("Consultando ingredientes sin stock");
        return ingredienteRepository.buscarSinStock()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    public IngredienteResponseDTO actualizar(
            Long id, IngredienteRequestDTO dto) {
        log.info("Actualizando ingrediente ID: {}", id);

        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ingrediente no encontrado para actualizar — ID: {}",
                            id);
                    return new RecursoNoEncontradoException(
                            "Ingrediente no encontrado con ID: " + id
                    );
                });


        if (!ingrediente.getNombre().equals(dto.getNombre()) &&
                ingredienteRepository.existsByNombre(dto.getNombre())) {
            log.warn("Nombre duplicado en actualización: {}",
                    dto.getNombre());
            throw new RuntimeException(
                    "El ingrediente '" + dto.getNombre() + "' ya existe"
            );
        }

        ingrediente.setNombre(dto.getNombre());
        ingrediente.setDescripcion(dto.getDescripcion());
        ingrediente.setStockActual(dto.getStockActual());
        ingrediente.setStockMinimo(dto.getStockMinimo());
        ingrediente.setUnidadMedida(dto.getUnidadMedida());

        Ingrediente actualizado = ingredienteRepository.save(ingrediente);
        log.info("Ingrediente actualizado OK — ID: {}", actualizado.getId());
        return mapearADTO(actualizado);
    }


    public IngredienteResponseDTO agregarStock(
            Long id, Double cantidad) {
        log.info("Agregando stock — ID: {}, cantidad: {}",
                id, cantidad);

        if (cantidad <= 0) {
            log.error("Cantidad inválida para agregar stock: {}",
                    cantidad);
            throw new RuntimeException(
                    "La cantidad a agregar debe ser mayor a 0"
            );
        }

        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ingrediente no encontrado con ID: " + id
                ));

        double stockAnterior = ingrediente.getStockActual();
        ingrediente.setStockActual(stockAnterior + cantidad);

        Ingrediente actualizado = ingredienteRepository.save(ingrediente);
        log.info("Stock agregado OK — ID: {}, anterior: {}, nuevo: {}",
                id, stockAnterior, actualizado.getStockActual());
        return mapearADTO(actualizado);
    }


    public IngredienteResponseDTO reducirStock(
            Long id, Double cantidad) {
        log.info("Reduciendo stock — ID: {}, cantidad: {}",
                id, cantidad);

        if (cantidad <= 0) {
            log.error("Cantidad inválida para reducir stock: {}",
                    cantidad);
            throw new RuntimeException(
                    "La cantidad a reducir debe ser mayor a 0"
            );
        }

        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ingrediente no encontrado con ID: " + id
                ));


        if (ingrediente.getStockActual() < cantidad) {
            log.warn("Stock insuficiente — ID: {}, disponible: {}, requerido: {}",
                    id, ingrediente.getStockActual(), cantidad);
            throw new StockInsuficienteException(
                    "Stock insuficiente. Disponible: "
                            + ingrediente.getStockActual()
                            + " " + ingrediente.getUnidadMedida()
            );
        }

        double stockAnterior = ingrediente.getStockActual();
        ingrediente.setStockActual(stockAnterior - cantidad);

        if (ingrediente.getStockActual() <= ingrediente.getStockMinimo()) {
            log.warn("ALERTA: Stock bajo para {} — actual: {}, mínimo: {}",
                    ingrediente.getNombre(),
                    ingrediente.getStockActual(),
                    ingrediente.getStockMinimo());
        }

        Ingrediente actualizado = ingredienteRepository.save(ingrediente);
        log.info("Stock reducido OK — ID: {}, anterior: {}, nuevo: {}",
                id, stockAnterior, actualizado.getStockActual());
        return mapearADTO(actualizado);
    }


    public void desactivar(Long id) {
        log.info("Desactivando ingrediente ID: {}", id);
        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ingrediente no encontrado con ID: " + id
                ));
        ingrediente.setActivo(false);
        ingredienteRepository.save(ingrediente);
        log.info("Ingrediente desactivado OK — ID: {}", id);
    }


    public void eliminar(Long id) {
        log.info("Eliminando ingrediente ID: {}", id);
        if (!ingredienteRepository.existsById(id)) {
            log.warn("Ingrediente no encontrado para eliminar — ID: {}",
                    id);
            throw new RecursoNoEncontradoException(
                    "Ingrediente no encontrado con ID: " + id
            );
        }
        ingredienteRepository.deleteById(id);
        log.info("Ingrediente eliminado OK — ID: {}", id);
    }


    private IngredienteResponseDTO mapearADTO(Ingrediente i) {
        boolean stockBajo = i.getStockActual() <= i.getStockMinimo();
        return IngredienteResponseDTO.builder()
                .id(i.getId())
                .nombre(i.getNombre())
                .descripcion(i.getDescripcion())
                .stockActual(i.getStockActual())
                .stockMinimo(i.getStockMinimo())
                .unidadMedida(i.getUnidadMedida())
                .activo(i.isActivo())
                .stockBajo(stockBajo)
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
