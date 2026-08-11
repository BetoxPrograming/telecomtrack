package com.telecomtrack.service;

import com.telecomtrack.domain.DetalleMaterialEstimado;
import com.telecomtrack.domain.ListadoMaterialEstimado;
import com.telecomtrack.domain.Material;
import com.telecomtrack.domain.Proyecto;
import com.telecomtrack.domain.Usuario;
import com.telecomtrack.repository.DetalleMaterialEstimadoRepository;
import com.telecomtrack.repository.ListadoMaterialEstimadoRepository;
import com.telecomtrack.repository.MaterialRepository;
import com.telecomtrack.repository.ProyectoRepository;
import com.telecomtrack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ListadoMaterialEstimadoService {

    private final ListadoMaterialEstimadoRepository listadoRepository;
    private final DetalleMaterialEstimadoRepository detalleRepository;
    private final ProyectoRepository proyectoRepository;
    private final MaterialRepository materialRepository;
    private final UsuarioRepository usuarioRepository;

    public ListadoMaterialEstimadoService(ListadoMaterialEstimadoRepository listadoRepository,
                                           DetalleMaterialEstimadoRepository detalleRepository,
                                           ProyectoRepository proyectoRepository,
                                           MaterialRepository materialRepository,
                                           UsuarioRepository usuarioRepository) {
        this.listadoRepository = listadoRepository;
        this.detalleRepository = detalleRepository;
        this.proyectoRepository = proyectoRepository;
        this.materialRepository = materialRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ListadoMaterialEstimado crear(Integer idProyecto, Map<Integer, Integer> materialCantidades) {

        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new IllegalArgumentException("listadoMaterialEstimado.error.proyecto.noExiste"));

        boolean sinItems = materialCantidades == null || materialCantidades.isEmpty();

        if (sinItems) {
            throw new IllegalArgumentException("listadoMaterialEstimado.error.sinItems");
        }

        ListadoMaterialEstimado listado = new ListadoMaterialEstimado();
        listado.setProyecto(proyecto);
        listado.setFechaCreacion(LocalDate.now());
        listado.setEstado(ListadoMaterialEstimado.ESTADO_PENDIENTE);
        listado = listadoRepository.save(listado);

        for (Map.Entry<Integer, Integer> entry : materialCantidades.entrySet()) {

            if (entry.getValue() == null || entry.getValue() < 1) {
                continue;
            }

            Material material = materialRepository.findById(entry.getKey().longValue())
                    .orElseThrow(() -> new IllegalArgumentException("listadoMaterialEstimado.error.material.noExiste"));

            DetalleMaterialEstimado detalle = new DetalleMaterialEstimado();
            detalle.setListado(listado);
            detalle.setMaterial(material);
            detalle.setCantidadEstimada(entry.getValue());

            detalleRepository.save(detalle);
        }

        return listado;
    }

    @Transactional(readOnly = true)
    public List<ListadoMaterialEstimado> getPorProyecto(Integer idProyecto) {
        return listadoRepository.findByProyectoIdProyectoOrderByFechaCreacionDesc(idProyecto);
    }

    @Transactional(readOnly = true)
    public List<ListadoMaterialEstimado> getPendientes() {
        return listadoRepository.findByEstadoOrderByFechaCreacionAsc(ListadoMaterialEstimado.ESTADO_PENDIENTE);
    }

    @Transactional(readOnly = true)
    public Optional<ListadoMaterialEstimado> getListado(Integer idListado) {
        return listadoRepository.findById(idListado);
    }

    @Transactional
    public void decidir(Integer idListado, Integer idSupervisor, String nuevoEstado, String comentario) {

        ListadoMaterialEstimado listado = listadoRepository.findById(idListado)
                .orElseThrow(() -> new IllegalArgumentException("listadoMaterialEstimado.error.noExiste"));

        if (!ListadoMaterialEstimado.ESTADO_PENDIENTE.equals(listado.getEstado())) {
            throw new IllegalStateException("listadoMaterialEstimado.error.yaProcesado");
        }

        boolean estadoValido = ListadoMaterialEstimado.ESTADO_APROBADO.equals(nuevoEstado)
                || ListadoMaterialEstimado.ESTADO_RECHAZADO.equals(nuevoEstado)
                || ListadoMaterialEstimado.ESTADO_MODIFICACION_SOLICITADA.equals(nuevoEstado);

        if (!estadoValido) {
            throw new IllegalArgumentException("listadoMaterialEstimado.error.estadoInvalido");
        }

        boolean comentarioRequerido = !ListadoMaterialEstimado.ESTADO_APROBADO.equals(nuevoEstado);

        if (comentarioRequerido && (comentario == null || comentario.isBlank())) {
            throw new IllegalArgumentException("listadoMaterialEstimado.error.comentarioRequerido");
        }

        Usuario supervisor = usuarioRepository.findById(idSupervisor)
                .orElseThrow(() -> new IllegalArgumentException("listadoMaterialEstimado.error.supervisor.noExiste"));

        listado.setEstado(nuevoEstado);
        listado.setComentarioSupervisor(comentario == null ? null : comentario.trim());
        listado.setUsuarioDecision(supervisor);
        listado.setFechaDecision(LocalDate.now());

        listadoRepository.save(listado);
    }
}
