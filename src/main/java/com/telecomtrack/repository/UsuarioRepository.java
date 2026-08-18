package com.telecomtrack.repository;

import com.telecomtrack.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    List<Usuario> findByActivoTrue();
    List<Usuario> findByActivoTrueAndRolOrderByNombreAsc(String rol);
    Optional<Usuario> findByCorreoAndActivoTrue(String correo);

}