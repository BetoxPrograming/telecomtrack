package com.telecomtrack.repository;

import com.telecomtrack.domain.DetalleSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleSolicitudRepository extends JpaRepository<DetalleSolicitud, Integer> {
}
