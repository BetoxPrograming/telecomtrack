package com.telecomtrack.repository;

import com.telecomtrack.domain.DetalleMaterialEstimado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleMaterialEstimadoRepository extends JpaRepository<DetalleMaterialEstimado, Integer> {
}
