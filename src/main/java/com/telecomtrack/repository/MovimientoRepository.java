package com.telecomtrack.repository;

import com.telecomtrack.domain.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByMaterialIdMaterialOrderByFechaDesc(Long idMaterial);
}