package com.telecomtrack.repository;

import com.telecomtrack.domain.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByMaterialIdMaterialOrderByFechaDesc(Long idMaterial);

    List<Movimiento> findByResponsableOrderByFechaDesc(String responsable);

    List<Movimiento> findByResponsableAndTipoOrderByFechaDesc(String responsable, String tipo);
}