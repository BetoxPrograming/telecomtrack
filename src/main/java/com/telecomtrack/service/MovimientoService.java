package com.telecomtrack.service;

import com.telecomtrack.domain.Material;
import com.telecomtrack.domain.Movimiento;
import com.telecomtrack.domain.Proveedor;
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

    public MovimientoService(MovimientoRepository movimientoRepository,
                              MaterialRepository materialRepository,
                              ProveedorRepository proveedorRepository) {
        this.movimientoRepository = movimientoRepository;
        this.materialRepository = materialRepository;
        this.proveedorRepository = proveedorRepository;
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
        movimiento.setTipo("ENTRADA");
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservacion(observacion);
        movimiento.setResponsable(responsable);
        movimiento.setMaterial(material);
        movimiento.setProveedor(proveedor);

        return movimientoRepository.save(movimiento);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> listarPorMaterial(Long idMaterial) {
        return movimientoRepository.findByMaterialIdMaterialOrderByFechaDesc(idMaterial);
    }
}
