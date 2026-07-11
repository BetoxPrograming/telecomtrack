package com.telecomtrack.service;

import com.telecomtrack.domain.Proyecto;
import com.telecomtrack.repository.ProyectoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProyectoService {

    private static final String ESTADO_ACTIVO = "Activo";

    private final ProyectoRepository proyectoRepository;

    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    @Transactional(readOnly = true)
    public List<Proyecto> getProyectos() {
        return proyectoRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Proyecto> getProyectosActivos() {
        return proyectoRepository.findByEstadoOrderByNombreAsc(ESTADO_ACTIVO);
    }

    @Transactional(readOnly = true)
    public Optional<Proyecto> getProyecto(Integer idProyecto) {
        return proyectoRepository.findById(idProyecto);
    }
}