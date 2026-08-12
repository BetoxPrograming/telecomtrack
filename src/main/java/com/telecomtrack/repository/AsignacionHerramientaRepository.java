package com.telecomtrack.repository;

import com.telecomtrack.domain.AsignacionHerramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionHerramientaRepository
        extends JpaRepository<AsignacionHerramienta, Integer> {

    List<AsignacionHerramienta>
    findByActivaTrueOrderByFechaAsignacionDesc();

    List<AsignacionHerramienta>
    findByTecnicoIdUsuarioAndActivaTrueOrderByFechaAsignacionDesc(Integer idTecnico);

    Optional<AsignacionHerramienta>
    findByIdAsignacionAndActivaTrue(Integer idAsignacion);

    boolean existsByHerramientaIdHerramientaAndActivaTrue(
            Integer idHerramienta);

    List<AsignacionHerramienta>
    findByTecnicoIdUsuarioOrderByFechaAsignacionDesc(Integer idTecnico);

    Optional<AsignacionHerramienta>
    findTopByHerramientaIdHerramientaOrderByFechaAsignacionDesc(Integer idHerramienta);
}