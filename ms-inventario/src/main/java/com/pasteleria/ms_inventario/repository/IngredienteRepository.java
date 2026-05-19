package com.pasteleria.ms_inventario.repository;

import com.pasteleria.ms_inventario.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {


    Optional<Ingrediente> findByNombre(String nombre);


    boolean existsByNombre(String nombre);


    List<Ingrediente> findByActivoTrue();


    @Query("SELECT i FROM Ingrediente i WHERE " +
            "LOWER(i.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Ingrediente> buscarPorNombre(String nombre);


    @Query("SELECT i FROM Ingrediente i WHERE " +
            "i.stockActual <= i.stockMinimo AND i.activo = true")
    List<Ingrediente> buscarConStockBajo();


    @Query("SELECT i FROM Ingrediente i WHERE " +
            "i.stockActual = 0 AND i.activo = true")
    List<Ingrediente> buscarSinStock();


}
