package com.pasteleria.ms_usuarios.repository;

import com.pasteleria.ms_usuarios.model.Usuario;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{


    Optional<Usuario> findByEmail(String email);


    boolean existsByEmail(String email);


    List<Usuario> findByActivoTrue();

    List<Usuario> findByRol(Usuario.Rol rol);


    List<Usuario> findByRolAndActivoTrue(Usuario.Rol rol);


    @Query("SELECT u FROM Usuario u WHERE " +
            "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
            "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Usuario> buscarPorNombre(String nombre);
}
