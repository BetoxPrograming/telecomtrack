package com.telecomtrack.service;

import com.telecomtrack.domain.AsignacionHerramienta;
import com.telecomtrack.domain.DevolucionHerramienta;
import com.telecomtrack.domain.FotoDevolucionHerramienta;
import com.telecomtrack.domain.Herramienta;
import com.telecomtrack.repository.AsignacionHerramientaRepository;
import com.telecomtrack.repository.DevolucionHerramientaRepository;
import com.telecomtrack.repository.FotoDevolucionHerramientaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

        validarFotos(fotos);

        DevolucionHerramienta devolucion = new DevolucionHerramienta();
        devolucion.setAsignacionHerramienta(asignacion);
        devolucion.setFechaDevolucion(LocalDate.now());
        devolucion.setEstadoDevolucion(estadoDevolucion);
        devolucion = devolucionHerramientaRepository.save(devolucion);

        guardarFotos(devolucion, fotos);
        cerrarAsignacionYActualizarHerramienta(asignacion, estadoDevolucion, devolucion);

        return devolucion;
    }

    @Transactional
    public boolean enviarAMantenimiento(Integer idHerramienta, LocalDate fechaRetornoEstimada) {
        return herramientaService.enviarAMantenimiento(idHerramienta, fechaRetornoEstimada);
    }

    @Transactional(readOnly = true)
    public List<AsignacionHerramienta> getAsignacionesActivasDeTecnico(Integer idTecnico) {
        return asignacionHerramientaRepository
                .findByTecnicoIdUsuarioAndActivaTrueOrderByFechaAsignacionDesc(idTecnico);
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

        List<MultipartFile> fotosValidas = new ArrayList<>();
        for (MultipartFile foto : fotos) {
            if (foto != null && !foto.isEmpty()) {
                fotosValidas.add(foto);
            }
        }

        for (MultipartFile foto : fotosValidas) {
            String contentType = foto.getContentType();
            if (!esTipoValido(contentType)) {
                throw new IllegalStateException("devolucion.error.foto.tipo");
            }

            if (foto.getSize() > 5L * 1024 * 1024) {
                throw new IllegalStateException("devolucion.error.foto.tamano");
            }
        }

        int indice = 0;
        for (MultipartFile foto : fotosValidas) {
            String ruta = archivoImagenService.guardarImagen(foto, "devolucion-" + devolucion.getIdDevolucion());

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
            DevolucionHerramienta devolucion) {

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
            herramienta.setFechaRetornoEstimada(LocalDate.now().plusDays(7));
            herramienta.setFechaBaja(null);
            herramienta.setJustificacionBaja(null);
        }

        if (ESTADO_PERDIDA.equals(estadoDevolucion)) {
            herramienta.setEstado(HerramientaService.ESTADO_BAJA);
            herramienta.setFechaBaja(LocalDate.now());
            herramienta.setJustificacionBaja("Baja por pérdida reportada en devolución.");
            herramienta.setFechaRetornoEstimada(null);
        }

        herramientaService.guardar(herramienta);
    }

    private void validarFotos(List<MultipartFile> fotos) {
        if (fotos == null) {
            return;
        }

        long cantidad = fotos.stream()
                .filter(foto -> foto != null && !foto.isEmpty())
                .count();

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
