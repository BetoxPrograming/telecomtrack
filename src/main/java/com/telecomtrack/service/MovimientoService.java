package com.telecomtrack.service;

import com.telecomtrack.domain.Material;
import com.telecomtrack.domain.Movimiento;
import com.telecomtrack.domain.Proveedor;
import com.telecomtrack.domain.Usuario;
import com.telecomtrack.repository.MaterialRepository;
import com.telecomtrack.repository.MovimientoRepository;
import com.telecomtrack.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final MaterialRepository materialRepository;
    private final ProveedorRepository proveedorRepository;
    private final NotificacionCorreoService notificacionCorreoService;

    public MovimientoService(MovimientoRepository movimientoRepository,
                              MaterialRepository materialRepository,
                              ProveedorRepository proveedorRepository,
                              NotificacionCorreoService notificacionCorreoService) {
        this.movimientoRepository = movimientoRepository;
        this.materialRepository = materialRepository;
        this.proveedorRepository = proveedorRepository;
        this.notificacionCorreoService = notificacionCorreoService;
    }

    public Movimiento registrarEntrada(Long idMaterial, Integer cantidad,
                                       String observacion, String responsable, Long idProveedor) {
        Material material = materialRepository.findById(idMaterial)
                .orElseThrow(() -> new IllegalArgumentException("Material no encontrado: " + idMaterial));

        Proveedor proveedor = proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado: " + idProveedor));

        material.setStockActual(material.getStockActual() + cantidad);
        materialRepository.save(material);

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(Movimiento.TIPO_ENTRADA);
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservacion(observacion);
        movimiento.setResponsable(responsable);
        movimiento.setMaterial(material);
        movimiento.setProveedor(proveedor);

        return movimientoRepository.save(movimiento);
    }

    public Movimiento registrarSalida(Material material, Integer cantidad,
                                       String observacion, String responsable,
                                       Usuario tecnico) {

        if (cantidad == null || cantidad < 1 || material.getStockActual() < cantidad) {
            throw new IllegalStateException("solicitud.error.stockInsuficiente");
        }

        material.setStockActual(material.getStockActual() - cantidad);
        materialRepository.save(material);

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(Movimiento.TIPO_SALIDA);
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservacion(observacion);
        movimiento.setResponsable(responsable);
        movimiento.setMaterial(material);
        movimiento.setTecnico(tecnico);
        movimiento.setProveedor(null);

        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
        notificacionCorreoService.notificarStockMinimo(material);
        return movimientoGuardado;
    }

    @Transactional(readOnly = true)
    public List<Movimiento> listarPorMaterial(Long idMaterial) {
        return movimientoRepository.findByMaterialIdMaterialOrderByFechaDesc(idMaterial);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> getSalidasPorTecnico(Integer idTecnico) {
        return movimientoRepository
                .findByTecnicoIdUsuarioAndTipoOrderByFechaDesc(
                        idTecnico,
                        Movimiento.TIPO_SALIDA);
    }
}
