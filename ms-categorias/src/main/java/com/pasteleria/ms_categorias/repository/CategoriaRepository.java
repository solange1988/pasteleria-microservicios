package com.pasteleria.ms_categorias.repository;





import com.pasteleria.ms_categorias.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{


    boolean existsByNombre(String nombre);


    Optional<Categoria> findByNombre(String nombre);


    List<Categoria> findByActivaTrue();


    @Query("SELECT c FROM Categoria c WHERE " +
            "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Categoria> buscarPorNombre(String nombre);
}
