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

    long countByActivaTrue();

    List<AsignacionHerramienta>
    findByActivaTrueAndProyectoSupervisorIdUsuarioOrderByFechaAsignacionDesc(Integer idSupervisor);

    List<AsignacionHerramienta>
    findByTecnicoIdUsuarioOrderByFechaAsignacionDesc(Integer idTecnico);

    List<AsignacionHerramienta>
    findByProyectoIdProyectoOrderByFechaAsignacionDesc(Integer idProyecto);

    Optional<AsignacionHerramienta>
    findByIdAsignacionAndActivaTrue(Integer idAsignacion);

    boolean existsByHerramientaIdHerramientaAndActivaTrue(
            Integer idHerramienta);
}