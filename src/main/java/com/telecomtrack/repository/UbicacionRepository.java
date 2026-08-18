package com.telecomtrack.repository;

import com.telecomtrack.domain.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    Optional<Ubicacion> findFirstByNombreIgnoreCase(String nombre);
}
