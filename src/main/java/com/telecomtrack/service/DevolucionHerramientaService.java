package com.telecomtrack.service;

import com.telecomtrack.domain.AsignacionHerramienta;
import com.telecomtrack.domain.DevolucionHerramienta;
import com.telecomtrack.domain.FotoDevolucionHerramienta;
import com.telecomtrack.domain.Herramienta;
import com.telecomtrack.domain.Proyecto;
import com.telecomtrack.repository.AsignacionHerramientaRepository;
import com.telecomtrack.repository.DevolucionHerramientaRepository;
import com.telecomtrack.repository.FotoDevolucionHerramientaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DevolucionHerramientaService {

    public static final String ESTADO_BUENA = "Buena";
    public static final String ESTADO_DANADA = "Dañada";
    public static final String ESTADO_PERDIDA = "Perdida";

    private final DevolucionHerramientaRepository devolucionHerramientaRepository;
    private final AsignacionHerramientaRepository asignacionHerramientaRepository;
    private final FotoDevolucionHerramientaRepository fotoDevolucionHerramientaRepository;
    private final HerramientaService herramientaService;
    private final ArchivoImagenService archivoImagenService;

    public DevolucionHerramientaService(
            DevolucionHerramientaRepository devolucionHerramientaRepository,
            AsignacionHerramientaRepository asignacionHerramientaRepository,
            FotoDevolucionHerramientaRepository fotoDevolucionHerramientaRepository,
            HerramientaService herramientaService,
            ArchivoImagenService archivoImagenService) {

        this.devolucionHerramientaRepository = devolucionHerramientaRepository;
        this.asignacionHerramientaRepository = asignacionHerramientaRepository;
        this.fotoDevolucionHerramientaRepository = fotoDevolucionHerramientaRepository;
        this.herramientaService = herramientaService;
        this.archivoImagenService = archivoImagenService;
    }

    @Transactional
    public DevolucionHerramienta registrarDevolucion(
            Integer idAsignacion,
            Integer idTecnico,
            String estadoDevolucion,
            LocalDate fechaRetornoEstimada,
            String justificacionBaja,
            List<MultipartFile> fotos) {

        AsignacionHerramienta asignacion = asignacionHerramientaRepository
                .findByIdAsignacionAndActivaTrue(idAsignacion)
                .orElseThrow(() -> new IllegalStateException("devolucion.error.asignacion.noExiste"));

        if (devolucionHerramientaRepository.existsByAsignacionHerramientaIdAsignacion(idAsignacion)) {
            throw new IllegalStateException("devolucion.error.duplicada");
        }

        if (!asignacion.getTecnico().getIdUsuario().equals(idTecnico)) {
            throw new IllegalStateException("devolucion.error.propiedad");
        }

        if (!esEstadoValido(estadoDevolucion)) {
            throw new IllegalStateException("devolucion.error.estado.invalido");
        }

        validarDatosEstado(estadoDevolucion, fechaRetornoEstimada, justificacionBaja);
        validarFotos(fotos);

        DevolucionHerramienta devolucion = new DevolucionHerramienta();
        devolucion.setAsignacionHerramienta(asignacion);
        devolucion.setFechaDevolucion(LocalDate.now());
        devolucion.setEstadoDevolucion(estadoDevolucion);
        devolucion = devolucionHerramientaRepository.save(devolucion);

        guardarFotos(devolucion, fotos);
        cerrarAsignacionYActualizarHerramienta(
                asignacion,
                estadoDevolucion,
                fechaRetornoEstimada,
                justificacionBaja);

        return devolucion;
    }

    @Transactional(readOnly = true)
    public List<AsignacionHerramienta> getAsignacionesActivasDeTecnico(Integer idTecnico) {
        return asignacionHerramientaRepository
                .findByTecnicoIdUsuarioAndActivaTrueOrderByFechaAsignacionDesc(idTecnico);
    }

    @Transactional(readOnly = true)
    public List<Proyecto> getProyectosTecnico(Integer idTecnico) {
        List<AsignacionHerramienta> asignaciones = asignacionHerramientaRepository
                .findByTecnicoIdUsuarioOrderByFechaAsignacionDesc(idTecnico);

        Map<Integer, Proyecto> proyectos = new LinkedHashMap<>();

        for (AsignacionHerramienta asignacion : asignaciones) {
            if (asignacion.getProyecto() != null) {
                proyectos.putIfAbsent(
                        asignacion.getProyecto().getIdProyecto(),
                        asignacion.getProyecto());
            }
        }

        return new ArrayList<>(proyectos.values());
    }

    @Transactional(readOnly = true)
    public List<DevolucionHerramienta> getHistorialTecnico(
            Integer idTecnico,
            Integer idProyecto,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        if (idProyecto != null && fechaInicio != null && fechaFin != null) {
            return devolucionHerramientaRepository
                    .findByAsignacionHerramientaTecnicoIdUsuarioAndAsignacionHerramientaProyectoIdProyectoAndFechaDevolucionBetweenOrderByFechaDevolucionDesc(
                            idTecnico,
                            idProyecto,
                            fechaInicio,
                            fechaFin);
        }

        if (idProyecto != null) {
            return devolucionHerramientaRepository
                    .findByAsignacionHerramientaTecnicoIdUsuarioAndAsignacionHerramientaProyectoIdProyectoOrderByFechaDevolucionDesc(
                            idTecnico,
                            idProyecto);
        }

        if (fechaInicio != null && fechaFin != null) {
            return devolucionHerramientaRepository
                    .findByAsignacionHerramientaTecnicoIdUsuarioAndFechaDevolucionBetweenOrderByFechaDevolucionDesc(
                            idTecnico,
                            fechaInicio,
                            fechaFin);
        }

        return devolucionHerramientaRepository
                .findByAsignacionHerramientaTecnicoIdUsuarioOrderByFechaDevolucionDesc(idTecnico);
    }

    @Transactional(readOnly = true)
    public List<FotoDevolucionHerramienta> getFotos(Integer idDevolucion) {
        return fotoDevolucionHerramientaRepository
                .findByDevolucionHerramientaIdDevolucionOrderByFechaCargaAsc(idDevolucion);
    }

    @Transactional(readOnly = true)
    public AsignacionHerramienta getUltimaAsignacionHerramienta(Integer idHerramienta) {
        return asignacionHerramientaRepository
                .findTopByHerramientaIdHerramientaOrderByFechaAsignacionDesc(idHerramienta)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<DevolucionHerramienta> getHistorialRecienteHerramienta(Integer idHerramienta) {
        return devolucionHerramientaRepository
                .findTop5ByAsignacionHerramientaHerramientaIdHerramientaOrderByFechaDevolucionDesc(idHerramienta);
    }

    private void guardarFotos(DevolucionHerramienta devolucion, List<MultipartFile> fotos) {
        if (fotos == null) {
            return;
        }

        int indice = 0;

        for (MultipartFile foto : fotos) {
            if (foto == null || foto.isEmpty()) {
                continue;
            }

            String ruta = archivoImagenService.guardarImagen(
                    foto,
                    "devolucion-" + devolucion.getIdDevolucion());

            if (indice == 0) {
                devolucion.setRutaFoto(ruta);
                devolucionHerramientaRepository.save(devolucion);
            }

            FotoDevolucionHerramienta fotoEntidad = new FotoDevolucionHerramienta();
            fotoEntidad.setDevolucionHerramienta(devolucion);
            fotoEntidad.setRutaImagen(ruta);
            fotoEntidad.setNombreArchivo(foto.getOriginalFilename());
            fotoEntidad.setFechaCarga(LocalDateTime.now());
            fotoDevolucionHerramientaRepository.save(fotoEntidad);
            indice++;
        }
    }

    private void cerrarAsignacionYActualizarHerramienta(
            AsignacionHerramienta asignacion,
            String estadoDevolucion,
            LocalDate fechaRetornoEstimada,
            String justificacionBaja) {

        asignacion.setActiva(false);
        asignacionHerramientaRepository.save(asignacion);

        Herramienta herramienta = asignacion.getHerramienta();

        if (ESTADO_BUENA.equals(estadoDevolucion)) {
            herramienta.setEstado(HerramientaService.ESTADO_DISPONIBLE);
            herramienta.setFechaRetornoEstimada(null);
            herramienta.setJustificacionBaja(null);
            herramienta.setFechaBaja(null);
        }

        if (ESTADO_DANADA.equals(estadoDevolucion)) {
            herramienta.setEstado(HerramientaService.ESTADO_MANTENIMIENTO);
            herramienta.setFechaRetornoEstimada(fechaRetornoEstimada);
            herramienta.setFechaBaja(null);
            herramienta.setJustificacionBaja(null);
        }

        if (ESTADO_PERDIDA.equals(estadoDevolucion)) {
            herramienta.setEstado(HerramientaService.ESTADO_BAJA);
            herramienta.setFechaBaja(LocalDate.now());
            herramienta.setJustificacionBaja(justificacionBaja.trim());
            herramienta.setFechaRetornoEstimada(null);
        }

        herramientaService.guardar(herramienta);
    }

    private void validarDatosEstado(
            String estadoDevolucion,
            LocalDate fechaRetornoEstimada,
            String justificacionBaja) {

        if (ESTADO_DANADA.equals(estadoDevolucion)) {
            if (fechaRetornoEstimada == null) {
                throw new IllegalStateException("devolucion.error.fechaRetorno.requerida");
            }

            if (fechaRetornoEstimada.isBefore(LocalDate.now())) {
                throw new IllegalStateException("devolucion.error.fechaRetorno.invalida");
            }
        }

        if (ESTADO_PERDIDA.equals(estadoDevolucion)
                && (justificacionBaja == null || justificacionBaja.isBlank())) {
            throw new IllegalStateException("devolucion.error.justificacion.requerida");
        }
    }

    private void validarFotos(List<MultipartFile> fotos) {
        if (fotos == null) {
            return;
        }

        int cantidad = 0;

        for (MultipartFile foto : fotos) {
            if (foto == null || foto.isEmpty()) {
                continue;
            }

            cantidad++;

            if (!esTipoValido(foto.getContentType())) {
                throw new IllegalStateException("devolucion.error.foto.tipo");
            }

            if (foto.getSize() > 5L * 1024 * 1024) {
                throw new IllegalStateException("devolucion.error.foto.tamano");
            }
        }

        if (cantidad > 3) {
            throw new IllegalStateException("devolucion.error.foto.maximo");
        }
    }

    private boolean esEstadoValido(String estado) {
        return ESTADO_BUENA.equals(estado)
                || ESTADO_DANADA.equals(estado)
                || ESTADO_PERDIDA.equals(estado);
    }

    private boolean esTipoValido(String contentType) {
        return "image/jpeg".equals(contentType)
                || "image/png".equals(contentType);
    }
}
