package com.telecomtrack.service;

import com.telecomtrack.domain.AsignacionHerramienta;
import com.telecomtrack.domain.Herramienta;
import com.telecomtrack.domain.Proyecto;
import com.telecomtrack.domain.Usuario;
import com.telecomtrack.repository.AsignacionHerramientaRepository;
import com.telecomtrack.repository.HerramientaRepository;
import com.telecomtrack.repository.ProyectoRepository;
import com.telecomtrack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AsignacionHerramientaService {

    private static final String ESTADO_DISPONIBLE = "Disponible";
    private static final String ESTADO_ASIGNADA = "Asignada";

    private final AsignacionHerramientaRepository asignacionRepository;
    private final HerramientaRepository herramientaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;

    public AsignacionHerramientaService(
            AsignacionHerramientaRepository asignacionRepository,
            HerramientaRepository herramientaRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository) {

        this.asignacionRepository = asignacionRepository;
        this.herramientaRepository = herramientaRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
    }

    @Transactional(readOnly = true)
    public List<AsignacionHerramienta> getAsignacionesActivas() {
        return asignacionRepository
                .findByActivaTrueOrderByFechaAsignacionDesc();
    }

    @Transactional(readOnly = true)
    public Optional<AsignacionHerramienta> getAsignacionActiva(
            Integer idAsignacion) {

        return asignacionRepository
                .findByIdAsignacionAndActivaTrue(idAsignacion);
    }

    @Transactional
    public AsignacionHerramienta save(
            AsignacionHerramienta asignacion) {

        Integer idHerramienta =
                asignacion.getHerramienta().getIdHerramienta();

        Integer idTecnico =
                asignacion.getTecnico().getIdUsuario();

        Integer idProyecto =
                asignacion.getProyecto().getIdProyecto();

        Herramienta herramienta = herramientaRepository
                .findById(idHerramienta)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "asignacion.error.herramienta.noExiste"));

        Usuario tecnico = usuarioRepository
                .findById(idTecnico)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "asignacion.error.tecnico.noExiste"));

        Proyecto proyecto = proyectoRepository
                .findById(idProyecto)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "asignacion.error.proyecto.noExiste"));

        if (!ESTADO_DISPONIBLE.equals(herramienta.getEstado())) {
            throw new IllegalStateException(
                    "asignacion.error.herramienta.noDisponible");
        }

        if (asignacionRepository
                .existsByHerramientaIdHerramientaAndActivaTrue(
                        idHerramienta)) {

            throw new IllegalStateException(
                    "asignacion.error.herramienta.asignada");
        }

        if (!tecnico.isActivo()) {
            throw new IllegalStateException(
                    "asignacion.error.tecnico.inactivo");
        }

        if (asignacion.getFechaAsignacion() == null) {
            asignacion.setFechaAsignacion(LocalDate.now());
        }

        asignacion.setHerramienta(herramienta);
        asignacion.setTecnico(tecnico);
        asignacion.setProyecto(proyecto);
        asignacion.setActiva(true);

        herramienta.setEstado(ESTADO_ASIGNADA);
        herramientaRepository.save(herramienta);

        return asignacionRepository.save(asignacion);
    }

    @Transactional(readOnly = true)
    public List<Herramienta> getHerramientasDisponibles() {
        return herramientaRepository.findAll()
                .stream()
                .filter(herramienta ->
                        ESTADO_DISPONIBLE.equals(herramienta.getEstado()))
                .sorted((primera, segunda) ->
                        primera.getNombre()
                                .compareToIgnoreCase(segunda.getNombre()))
                .toList();
    }
}