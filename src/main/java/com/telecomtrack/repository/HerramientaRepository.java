package com.telecomtrack.repository;

import com.telecomtrack.domain.Herramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HerramientaRepository extends JpaRepository<Herramienta, Integer> {

    Optional<Herramienta> findByCodigo(String codigo);
}
