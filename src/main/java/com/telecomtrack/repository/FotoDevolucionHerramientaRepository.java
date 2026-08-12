package com.telecomtrack.repository;

import com.telecomtrack.domain.FotoDevolucionHerramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoDevolucionHerramientaRepository
        extends JpaRepository<FotoDevolucionHerramienta, Integer> {

    List<FotoDevolucionHerramienta>
    findByDevolucionHerramientaIdDevolucionOrderByFechaCargaAsc(Integer idDevolucion);
}
