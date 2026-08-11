package com.telecomtrack.service;

import com.telecomtrack.domain.*;
import com.telecomtrack.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final DetalleSolicitudRepository detalleSolicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final HerramientaRepository herramientaRepository;
    private final MaterialRepository materialRepository;
    private final MovimientoService movimientoService;
    private final AsignacionHerramientaService asignacionHerramientaService;

    public SolicitudService(SolicitudRepository solicitudRepository,
                             DetalleSolicitudRepository detalleSolicitudRepository,
                             UsuarioRepository usuarioRepository,
                             ProyectoRepository proyectoRepository,
                             UbicacionRepository ubicacionRepository,
                             HerramientaRepository herramientaRepository,
                             MaterialRepository materialRepository,
                             MovimientoService movimientoService,
                             AsignacionHerramientaService asignacionHerramientaService) {
        this.solicitudRepository = solicitudRepository;
        this.detalleSolicitudRepository = detalleSolicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.herramientaRepository = herramientaRepository;
        this.materialRepository = materialRepository;
        this.movimientoService = movimientoService;
        this.asignacionHerramientaService = asignacionHerramientaService;
    }

    @Transactional
    public Solicitud crear(Integer idTecnico, Integer idProyecto, Integer idUbicacion,
                            List<Integer> herramientaIds, Map<Integer, Integer> materialCantidades) {

        Usuario tecnico = usuarioRepository.findById(idTecnico)
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.tecnico.noExiste"));

        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.proyecto.noExiste"));

        Ubicacion ubicacion = ubicacionRepository.findById(idUbicacion)
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.ubicacion.noExiste"));

        boolean sinItems = (herramientaIds == null || herramientaIds.isEmpty())
                && (materialCantidades == null || materialCantidades.isEmpty());

        if (sinItems) {
            throw new IllegalArgumentException("solicitud.error.sinItems");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setTecnico(tecnico);
        solicitud.setProyecto(proyecto);
        solicitud.setUbicacion(ubicacion);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstado(Solicitud.ESTADO_PENDIENTE);
        solicitud = solicitudRepository.save(solicitud);

        if (herramientaIds != null) {
            for (Integer idHerramienta : herramientaIds) {

                Herramienta herramienta = herramientaRepository.findById(idHerramienta)
                        .orElseThrow(() -> new IllegalArgumentException("solicitud.error.herramienta.noExiste"));

                DetalleSolicitud detalle = new DetalleSolicitud();
                detalle.setSolicitud(solicitud);
                detalle.setHerramienta(herramienta);

                detalleSolicitudRepository.save(detalle);
            }
        }

        if (materialCantidades != null) {
            for (Map.Entry<Integer, Integer> entry : materialCantidades.entrySet()) {

                if (entry.getValue() == null || entry.getValue() < 1) {
                    continue;
                }

                Material material = materialRepository.findById(entry.getKey().longValue())
                        .orElseThrow(() -> new IllegalArgumentException("solicitud.error.material.noExiste"));

                DetalleSolicitud detalle = new DetalleSolicitud();
                detalle.setSolicitud(solicitud);
                detalle.setMaterial(material);
                detalle.setCantidad(entry.getValue());

                detalleSolicitudRepository.save(detalle);
            }
        }

        return solicitud;
    }

    @Transactional(readOnly = true)
    public List<Solicitud> getMisSolicitudes(Integer idTecnico) {
        return solicitudRepository.findByTecnicoIdUsuarioOrderByFechaSolicitudDesc(idTecnico);
    }

    @Transactional(readOnly = true)
    public List<Solicitud> getPendientes() {
        return solicitudRepository.findByEstadoOrderByFechaSolicitudAsc(Solicitud.ESTADO_PENDIENTE);
    }

    @Transactional(readOnly = true)
    public Optional<Solicitud> getSolicitud(Integer idSolicitud) {
        return solicitudRepository.findById(idSolicitud);
    }

    @Transactional
    public void aprobar(Integer idSolicitud, Integer idUsuarioDecision) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.noExiste"));

        if (!Solicitud.ESTADO_PENDIENTE.equals(solicitud.getEstado())) {
            throw new IllegalStateException("solicitud.error.yaProcesada");
        }

        Usuario bodeguero = usuarioRepository.findById(idUsuarioDecision)
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.usuarioDecision.noExiste"));

        if (!solicitud.getTecnico().isActivo()) {
            throw new IllegalStateException("asignacion.error.tecnico.inactivo");
        }

        // Pasada 1: validar todos los detalles, sin escribir nada todavia.
        for (DetalleSolicitud detalle : solicitud.getDetalles()) {

            if (detalle.getMaterial() != null) {

                if (detalle.getCantidad() == null || detalle.getCantidad() < 1
                        || detalle.getMaterial().getStockActual() < detalle.getCantidad()) {
                    throw new IllegalStateException("solicitud.error.stockInsuficiente");
                }

            } else if (detalle.getHerramienta() != null
                    && !HerramientaService.ESTADO_DISPONIBLE.equals(detalle.getHerramienta().getEstado())) {
                throw new IllegalStateException("solicitud.error.herramientaNoDisponible");
            }
        }

        // Pasada 2: pasada 1 completa sin errores, ahora si se aplican los cambios.
        for (DetalleSolicitud detalle : solicitud.getDetalles()) {

            if (detalle.getMaterial() != null) {

                String observacion = "Salida por solicitud #" + solicitud.getIdSolicitud()
                        + " - Proyecto: " + solicitud.getProyecto().getNombre();

                movimientoService.registrarSalida(
                        detalle.getMaterial(),
                        detalle.getCantidad(),
                        observacion,
                        bodeguero.getNombre() + " " + bodeguero.getApellido());

            } else if (detalle.getHerramienta() != null) {

                AsignacionHerramienta asignacion = new AsignacionHerramienta();
                asignacion.setHerramienta(detalle.getHerramienta());
                asignacion.setTecnico(solicitud.getTecnico());
                asignacion.setProyecto(solicitud.getProyecto());
                asignacion.setFechaAsignacion(LocalDate.now());

                asignacionHerramientaService.save(asignacion);
            }
        }

        solicitud.setEstado(Solicitud.ESTADO_APROBADA);
        solicitud.setUsuarioDecision(bodeguero);
        solicitud.setFechaDecision(LocalDateTime.now());

        solicitudRepository.save(solicitud);
    }

    @Transactional
    public void rechazar(Integer idSolicitud, Integer idUsuarioDecision, String motivo) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.noExiste"));

        if (!Solicitud.ESTADO_PENDIENTE.equals(solicitud.getEstado())) {
            throw new IllegalStateException("solicitud.error.yaProcesada");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("solicitud.error.motivoRequerido");
        }

        Usuario bodeguero = usuarioRepository.findById(idUsuarioDecision)
                .orElseThrow(() -> new IllegalArgumentException("solicitud.error.usuarioDecision.noExiste"));

        solicitud.setEstado(Solicitud.ESTADO_RECHAZADA);
        solicitud.setMotivoRechazo(motivo.trim());
        solicitud.setUsuarioDecision(bodeguero);
        solicitud.setFechaDecision(LocalDateTime.now());

        solicitudRepository.save(solicitud);
    }
}
