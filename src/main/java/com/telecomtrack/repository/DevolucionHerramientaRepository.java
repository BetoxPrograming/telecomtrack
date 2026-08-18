package com.telecomtrack.repository;

import com.telecomtrack.domain.DevolucionHerramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DevolucionHerramientaRepository
        extends JpaRepository<DevolucionHerramienta, Integer> {

    boolean existsByAsignacionHerramientaIdAsignacion(Integer idAsignacion);

    List<DevolucionHerramienta> findAllByOrderByFechaDevolucionDesc();

    List<DevolucionHerramienta>
    findByAsignacionHerramientaProyectoIdProyectoOrderByFechaDevolucionDesc(Integer idProyecto);

    List<DevolucionHerramienta>
    findByFechaDevolucionBetweenOrderByFechaDevolucionDesc(LocalDate fechaInicio, LocalDate fechaFin);

    List<DevolucionHerramienta>
    findByAsignacionHerramientaProyectoIdProyectoAndFechaDevolucionBetweenOrderByFechaDevolucionDesc(
            Integer idProyecto,
            LocalDate fechaInicio,
            LocalDate fechaFin);

    List<DevolucionHerramienta>
    findByAsignacionHerramientaTecnicoIdUsuarioOrderByFechaDevolucionDesc(Integer idTecnico);

    List<DevolucionHerramienta>
    findTop5ByAsignacionHerramientaHerramientaIdHerramientaOrderByFechaDevolucionDesc(Integer idHerramienta);

    List<DevolucionHerramienta>
    findByAsignacionHerramientaTecnicoIdUsuarioAndAsignacionHerramientaProyectoIdProyectoOrderByFechaDevolucionDesc(
            Integer idTecnico,
            Integer idProyecto);

    List<DevolucionHerramienta>
    findByAsignacionHerramientaTecnicoIdUsuarioAndFechaDevolucionBetweenOrderByFechaDevolucionDesc(
            Integer idTecnico,
            LocalDate fechaInicio,
            LocalDate fechaFin);

    List<DevolucionHerramienta>
    findByAsignacionHerramientaTecnicoIdUsuarioAndAsignacionHerramientaProyectoIdProyectoAndFechaDevolucionBetweenOrderByFechaDevolucionDesc(
            Integer idTecnico,
            Integer idProyecto,
            LocalDate fechaInicio,
            LocalDate fechaFin);
}
