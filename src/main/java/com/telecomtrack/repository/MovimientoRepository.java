package com.telecomtrack.repository;

import com.telecomtrack.domain.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByMaterialIdMaterialOrderByFechaDesc(Long idMaterial);

    List<Movimiento> findTop10ByOrderByFechaDesc();

    List<Movimiento> findByFechaBetweenOrderByFechaDesc(LocalDateTime desde, LocalDateTime hasta);
}