package com.telecomtrack.service;

import com.telecomtrack.domain.ListadoMaterialEstimado;
import com.telecomtrack.domain.Solicitud;
import com.telecomtrack.dto.DashboardResumen;
import com.telecomtrack.dto.DashboardSupervisor;
import com.telecomtrack.repository.AsignacionHerramientaRepository;
import com.telecomtrack.repository.HerramientaRepository;
import com.telecomtrack.repository.MaterialRepository;
import com.telecomtrack.repository.MovimientoRepository;
import com.telecomtrack.repository.ProyectoRepository;
import com.telecomtrack.repository.SolicitudRepository;
import com.telecomtrack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private static final String ESTADO_PROYECTO_ACTIVO = "Activo";

    private final HerramientaRepository herramientaRepository;
    private final MaterialRepository materialRepository;
    private final SolicitudRepository solicitudRepository;
    private final ProyectoRepository proyectoRepository;
    private final MovimientoRepository movimientoRepository;
    private final AsignacionHerramientaRepository asignacionHerramientaRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardService(HerramientaRepository herramientaRepository,
                             MaterialRepository materialRepository,
                             SolicitudRepository solicitudRepository,
                             ProyectoRepository proyectoRepository,
                             MovimientoRepository movimientoRepository,
                             AsignacionHerramientaRepository asignacionHerramientaRepository,
                             UsuarioRepository usuarioRepository) {
        this.herramientaRepository = herramientaRepository;
        this.materialRepository = materialRepository;
        this.solicitudRepository = solicitudRepository;
        this.proyectoRepository = proyectoRepository;
        this.movimientoRepository = movimientoRepository;
        this.asignacionHerramientaRepository = asignacionHerramientaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResumen getResumenAdministrador() {
        return DashboardResumen.builder()
                .herramientasDisponibles(herramientaRepository.countByEstado(HerramientaService.ESTADO_DISPONIBLE))
                .herramientasEnUso(asignacionHerramientaRepository.countByActivaTrue())
                .materialesStockCritico(materialRepository.findMaterialesConStockBajo())
                .solicitudesPendientes(solicitudRepository.countByEstado(Solicitud.ESTADO_PENDIENTE))
                .proyectosActivos(proyectoRepository.countByEstado(ESTADO_PROYECTO_ACTIVO))
                .ultimosMovimientos(movimientoRepository.findTop10ByOrderByFechaDesc())
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardSupervisor getResumenSupervisor(Integer idSupervisor) {

        var supervisor = usuarioRepository.findById(idSupervisor)
                .orElseThrow(() -> new IllegalArgumentException("dashboard.error.supervisor.noExiste"));

        return DashboardSupervisor.builder()
                .supervisor(supervisor)
                .misProyectos(proyectoRepository.findBySupervisorIdUsuarioOrderByNombreAsc(idSupervisor))
                .herramientasDisponibles(herramientaRepository.countDisponiblesPorSupervisor(
                        idSupervisor, HerramientaService.ESTADO_DISPONIBLE))
                .elementosEnUso(asignacionHerramientaRepository
                        .findByActivaTrueAndProyectoSupervisorIdUsuarioOrderByFechaAsignacionDesc(idSupervisor))
                .materialesStockCritico(materialRepository.findMaterialesConStockBajoPorSupervisor(
                        idSupervisor, ListadoMaterialEstimado.ESTADO_APROBADO))
                .actividadReciente(solicitudRepository
                        .findTop10ByProyectoSupervisorIdUsuarioOrderByFechaSolicitudDesc(idSupervisor))
                .build();
    }
}
