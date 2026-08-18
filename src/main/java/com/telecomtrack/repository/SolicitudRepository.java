package com.telecomtrack.repository;

import com.telecomtrack.domain.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {

    List<Solicitud> findByTecnicoIdUsuarioOrderByFechaSolicitudDesc(Integer idTecnico);

    List<Solicitud> findByEstadoOrderByFechaSolicitudAsc(String estado);

    List<Solicitud> findAllByOrderByFechaSolicitudDesc();

    long countByEstado(String estado);

    List<Solicitud> findTop10ByProyectoSupervisorIdUsuarioOrderByFechaSolicitudDesc(Integer idSupervisor);

    List<Solicitud> findByProyectoIdProyectoAndEstado(Integer idProyecto, String estado);
}
