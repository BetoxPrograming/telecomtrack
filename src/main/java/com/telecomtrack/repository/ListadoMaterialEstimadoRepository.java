package com.telecomtrack.repository;

import com.telecomtrack.domain.ListadoMaterialEstimado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListadoMaterialEstimadoRepository extends JpaRepository<ListadoMaterialEstimado, Integer> {

    List<ListadoMaterialEstimado> findByProyectoIdProyectoOrderByFechaCreacionDesc(Integer idProyecto);

    List<ListadoMaterialEstimado> findByEstadoOrderByFechaCreacionAsc(String estado);

    List<ListadoMaterialEstimado> findByEstadoAndProyectoSupervisorIdUsuarioOrderByFechaCreacionAsc(
            String estado, Integer idSupervisor);
}
