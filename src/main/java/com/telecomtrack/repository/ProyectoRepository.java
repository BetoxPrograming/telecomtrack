package com.telecomtrack.repository;

import com.telecomtrack.domain.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    List<Proyecto> findAllByOrderByNombreAsc();

    List<Proyecto> findByEstadoOrderByNombreAsc(String estado);

    long countByEstado(String estado);

    List<Proyecto> findBySupervisorIdUsuarioOrderByNombreAsc(Integer idSupervisor);
}