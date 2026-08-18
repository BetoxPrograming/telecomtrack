package com.telecomtrack.repository;

import com.telecomtrack.domain.ConfiguracionNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracionNotificacionRepository
        extends JpaRepository<ConfiguracionNotificacion, Integer> {

    Optional<ConfiguracionNotificacion> findFirstByOrderByIdConfiguracionAsc();
}
