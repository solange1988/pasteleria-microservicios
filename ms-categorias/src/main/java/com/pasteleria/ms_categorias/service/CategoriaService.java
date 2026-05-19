package com.pasteleria.ms_categorias.service;


import com.pasteleria.ms_categorias.repository.CategoriaRepository;
import com.pasteleria.ms_categorias.dto.CategoriaRequestDTO;
import com.pasteleria.ms_categorias.dto.CategoriaResponseDTO;
import com.pasteleria.ms_categorias.exception.NombreDuplicadoException;
import com.pasteleria.ms_categorias.exception.RecursoNoEncontradoException;

import org.springframework.stereotype.Service;

import com.pasteleria.ms_categorias.exception.*;
import com.pasteleria.ms_categorias.model.Categoria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class CategoriaService {

    private static final Logger log =
            LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }


    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        log.info("Creando categoría: {}", dto.getNombre());


        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            log.warn("Nombre de categoría duplicado: {}", dto.getNombre());
            throw new NombreDuplicadoException(
                    "La categoría '" + dto.getNombre() + "' ya existe"
            );
        }

        Categoria categoria = Categoria.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .activa(true)
                .build();

        Categoria guardada = categoriaRepository.save(categoria);
        log.info("Categoría creada OK — ID: {}", guardada.getId());
        return mapearADTO(guardada);
    }


    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        log.info("Listando todas las categorías");
        return categoriaRepository.findAll()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarActivas() {
        log.info("Listando categorías activas");
        return categoriaRepository.findByActivaTrue()
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        log.info("Buscando categoría por ID: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Categoría no encontrada con ID: " + id
                    );
                });
        return mapearADTO(categoria);
    }


    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> buscarPorNombre(String nombre) {
        log.info("Buscando categorías por nombre: {}", nombre);
        return categoriaRepository.buscarPorNombre(nombre)
                .stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }


    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        log.info("Actualizando categoría ID: {}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada para actualizar — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Categoría no encontrada con ID: " + id
                    );
                });

        if (!categoria.getNombre().equals(dto.getNombre()) &&
                categoriaRepository.existsByNombre(dto.getNombre())) {
            log.warn("Nombre duplicado en actualización: {}", dto.getNombre());
            throw new NombreDuplicadoException(
                    "La categoría '" + dto.getNombre() + "' ya existe"
            );
        }

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        Categoria actualizada = categoriaRepository.save(categoria);
        log.info("Categoría actualizada OK — ID: {}", actualizada.getId());
        return mapearADTO(actualizada);
    }


    public void desactivar(Long id) {
        log.info("Desactivando categoría ID: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada para desactivar — ID: {}", id);
                    return new RecursoNoEncontradoException(
                            "Categoría no encontrada con ID: " + id
                    );
                });
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
        log.info("Categoría desactivada OK — ID: {}", id);
    }

    public void eliminar(Long id) {
        log.info("Eliminando categoría ID: {}", id);
        if (!categoriaRepository.existsById(id)) {
            log.warn("Categoría no encontrada para eliminar — ID: {}", id);
            throw new RecursoNoEncontradoException(
                    "Categoría no encontrada con ID: " + id
            );
        }
        categoriaRepository.deleteById(id);
        log.info("Categoría eliminada OK — ID: {}", id);
    }


    private CategoriaResponseDTO mapearADTO(Categoria c) {
        return CategoriaResponseDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .activa(c.isActiva())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
