package com.pasteleria.ms_productos.repository;


import com.pasteleria.ms_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository

public interface ProductoRepository extends JpaRepository<Producto, Long>{


    List<Producto> findByDisponibleTrue();


    List<Producto> findByCategoriaId(Long categoriaId);


    List<Producto> findByCategoriaIdAndDisponibleTrue(Long categoriaId);


    boolean existsByNombre(String nombre);


    @Query("SELECT p FROM Producto p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Producto> buscarPorNombre(String nombre);


    @Query("SELECT p FROM Producto p WHERE " +
            "p.precio BETWEEN :min AND :max AND p.disponible = true")
    List<Producto> buscarPorRangoPrecio(BigDecimal min, BigDecimal max);


    @Query("SELECT p FROM Producto p WHERE p.stock <= :limite AND p.disponible = true")
    List<Producto> buscarConStockBajo(Integer limite);
}
