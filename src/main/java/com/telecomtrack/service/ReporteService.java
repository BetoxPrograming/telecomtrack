package com.telecomtrack.service;

import com.telecomtrack.domain.AsignacionHerramienta;
import com.telecomtrack.domain.Herramienta;
import com.telecomtrack.domain.ListadoMaterialEstimado;
import com.telecomtrack.domain.Material;
import com.telecomtrack.domain.Proyecto;
import com.telecomtrack.domain.Solicitud;
import com.telecomtrack.dto.ReporteConsumoProyectoFila;
import com.telecomtrack.dto.ReporteInventarioFila;
import com.telecomtrack.dto.ReporteTecnicoFila;
import com.telecomtrack.repository.AsignacionHerramientaRepository;
import com.telecomtrack.repository.DevolucionHerramientaRepository;
import com.telecomtrack.repository.HerramientaRepository;
import com.telecomtrack.repository.ListadoMaterialEstimadoRepository;
import com.telecomtrack.repository.MaterialRepository;
import com.telecomtrack.repository.ProyectoRepository;
import com.telecomtrack.repository.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteService {

    private static final String ESTADO_TECNICO_ACTIVA = "ACTIVA";
    private static final String ESTADO_TECNICO_DEVUELTA = "DEVUELTA";

    private final ProyectoRepository proyectoRepository;
    private final ListadoMaterialEstimadoRepository listadoMaterialEstimadoRepository;
    private final SolicitudRepository solicitudRepository;
    private final MaterialRepository materialRepository;
    private final HerramientaRepository herramientaRepository;
    private final AsignacionHerramientaRepository asignacionHerramientaRepository;
    private final DevolucionHerramientaRepository devolucionHerramientaRepository;

    public ReporteService(ProyectoRepository proyectoRepository,
                           ListadoMaterialEstimadoRepository listadoMaterialEstimadoRepository,
                           SolicitudRepository solicitudRepository,
                           MaterialRepository materialRepository,
                           HerramientaRepository herramientaRepository,
                           AsignacionHerramientaRepository asignacionHerramientaRepository,
                           DevolucionHerramientaRepository devolucionHerramientaRepository) {
        this.proyectoRepository = proyectoRepository;
        this.listadoMaterialEstimadoRepository = listadoMaterialEstimadoRepository;
        this.solicitudRepository = solicitudRepository;
        this.materialRepository = materialRepository;
        this.herramientaRepository = herramientaRepository;
        this.asignacionHerramientaRepository = asignacionHerramientaRepository;
        this.devolucionHerramientaRepository = devolucionHerramientaRepository;
    }

    @Transactional(readOnly = true)
    public List<ReporteConsumoProyectoFila> getConsumoPorProyecto(Integer idProyecto) {

        List<Proyecto> proyectos = idProyecto != null
                ? proyectoRepository.findById(idProyecto).map(List::of).orElse(List.of())
                : proyectoRepository.findAllByOrderByNombreAsc();

        return proyectos.stream()
                .map(this::calcularConsumo)
                .toList();
    }

    private ReporteConsumoProyectoFila calcularConsumo(Proyecto proyecto) {

        int cantidadEstimada = listadoMaterialEstimadoRepository
                .findByProyectoIdProyectoOrderByFechaCreacionDesc(proyecto.getIdProyecto())
                .stream()
                .filter(listado -> ListadoMaterialEstimado.ESTADO_APROBADO.equals(listado.getEstado()))
                .flatMap(listado -> listado.getDetalles().stream())
                .mapToInt(detalle -> detalle.getCantidadEstimada() != null ? detalle.getCantidadEstimada() : 0)
                .sum();

        int cantidadReal = solicitudRepository
                .findByProyectoIdProyectoAndEstado(proyecto.getIdProyecto(), Solicitud.ESTADO_APROBADA)
                .stream()
                .flatMap(solicitud -> solicitud.getDetalles().stream())
                .filter(detalle -> detalle.getMaterial() != null)
                .mapToInt(detalle -> detalle.getCantidad() != null ? detalle.getCantidad() : 0)
                .sum();

        return ReporteConsumoProyectoFila.builder()
                .proyecto(proyecto)
                .cantidadEstimada(cantidadEstimada)
                .cantidadReal(cantidadReal)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReporteInventarioFila> getInventarioGeneral(String tipoElemento, String categoria, String estado,
                                                              String ubicacion, LocalDate fechaDesde, LocalDate fechaHasta) {

        List<ReporteInventarioFila> filas = new ArrayList<>();

        if (!"MATERIAL".equalsIgnoreCase(tipoElemento)) {
            filas.addAll(herramientaRepository.findAll().stream()
                    .map(this::aFilaInventario)
                    .toList());
        }

        if (!"HERRAMIENTA".equalsIgnoreCase(tipoElemento)) {
            filas.addAll(materialRepository.findAll().stream()
                    .map(this::aFilaInventario)
                    .toList());
        }

        return filas.stream()
                .filter(fila -> categoria == null || categoria.isBlank()
                        || categoria.equalsIgnoreCase(fila.getCategoria()))
                .filter(fila -> estado == null || estado.isBlank()
                        || estado.equalsIgnoreCase(fila.getEstado()))
                .filter(fila -> ubicacion == null || ubicacion.isBlank()
                        || ubicacion.equalsIgnoreCase(fila.getUbicacion()))
                .filter(fila -> dentroDeRango(fila.getFechaReferencia(), fechaDesde, fechaHasta))
                .toList();
    }

    private boolean dentroDeRango(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        if (desde == null && hasta == null) {
            return true;
        }
        if (fecha == null) {
            return false;
        }
        if (desde != null && fecha.isBefore(desde)) {
            return false;
        }
        return hasta == null || !fecha.isAfter(hasta);
    }

    private ReporteInventarioFila aFilaInventario(Herramienta herramienta) {

        LocalDate fechaReferencia = herramienta.getFechaBaja() != null
                ? herramienta.getFechaBaja()
                : herramienta.getFechaRetornoEstimada();

        return ReporteInventarioFila.builder()
                .tipoElemento("HERRAMIENTA")
                .codigo(herramienta.getCodigo())
                .nombre(herramienta.getNombre())
                .categoria(herramienta.getCategoria())
                .estado(herramienta.getEstado())
                .ubicacion(herramienta.getUbicacion() != null ? herramienta.getUbicacion().getNombre() : null)
                .fechaReferencia(fechaReferencia)
                .cantidad(1)
                .valorUnitario(herramienta.getValorUnitario())
                .valorTotal(herramienta.getValorUnitario() != null
                        ? herramienta.getValorUnitario()
                        : BigDecimal.ZERO)
                .build();
    }

    private ReporteInventarioFila aFilaInventario(Material material) {

        return ReporteInventarioFila.builder()
                .tipoElemento("MATERIAL")
                .codigo(material.getCodigoUnico())
                .nombre(material.getNombre())
                .categoria(material.getCategoria() != null ? material.getCategoria().getNombre() : null)
                .estado(material.isStockBajo() ? "Stock bajo" : "Stock normal")
                .ubicacion(material.getUbicacion() != null ? material.getUbicacion().getNombre() : null)
                .fechaReferencia(null)
                .cantidad(material.getStockActual() != null ? material.getStockActual() : 0)
                .valorUnitario(material.getValorUnitario())
                .valorTotal(material.getValorTotal())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReporteTecnicoFila> getActivosPorTecnico(Integer idTecnico, Integer idProyecto, String estado) {

        return asignacionHerramientaRepository.findAll().stream()
                .filter(asignacion -> idTecnico == null || idTecnico.equals(asignacion.getTecnico().getIdUsuario()))
                .filter(asignacion -> idProyecto == null || idProyecto.equals(asignacion.getProyecto().getIdProyecto()))
                .map(this::aFilaTecnico)
                .filter(fila -> estado == null || estado.isBlank() || estado.equalsIgnoreCase(fila.getEstadoActual()))
                .toList();
    }

    private ReporteTecnicoFila aFilaTecnico(AsignacionHerramienta asignacion) {

        boolean devuelta = devolucionHerramientaRepository
                .existsByAsignacionHerramientaIdAsignacion(asignacion.getIdAsignacion());

        return ReporteTecnicoFila.builder()
                .tecnico(asignacion.getTecnico())
                .herramientaCodigo(asignacion.getHerramienta().getCodigo())
                .herramientaNombre(asignacion.getHerramienta().getNombre())
                .proyecto(asignacion.getProyecto())
                .fechaAsignacion(asignacion.getFechaAsignacion())
                .estadoActual(devuelta ? ESTADO_TECNICO_DEVUELTA : ESTADO_TECNICO_ACTIVA)
                .build();
    }
}
