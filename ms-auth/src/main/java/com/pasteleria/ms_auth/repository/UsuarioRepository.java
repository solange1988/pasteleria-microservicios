package com.pasteleria.ms_auth.repository;


import com.pasteleria.ms_auth.model.Usuario;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);


    Optional<Usuario> findByEmailAndActivoTrue(String email);


}
