package com.telecomtrack.service;

import com.telecomtrack.domain.ConfiguracionNotificacion;
import com.telecomtrack.repository.ConfiguracionNotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionNotificacionService {

    private final ConfiguracionNotificacionRepository configuracionNotificacionRepository;

    public ConfiguracionNotificacionService(
            ConfiguracionNotificacionRepository configuracionNotificacionRepository) {
        this.configuracionNotificacionRepository = configuracionNotificacionRepository;
    }

    @Transactional
    public ConfiguracionNotificacion getConfiguracion() {
        return configuracionNotificacionRepository
                .findFirstByOrderByIdConfiguracionAsc()
                .orElseGet(() -> {
                    var configuracion = new ConfiguracionNotificacion();
                    configuracion.setNotificarStockMinimo(true);
                    configuracion.setNotificarSolicitudesPendientes(true);
                    return configuracionNotificacionRepository.save(configuracion);
                });
    }

    @Transactional
    public void save(ConfiguracionNotificacion configuracion) {
        configuracionNotificacionRepository.save(configuracion);
    }

    @Transactional(readOnly = true)
    public boolean notificarStockMinimo() {
        return configuracionNotificacionRepository
                .findFirstByOrderByIdConfiguracionAsc()
                .map(ConfiguracionNotificacion::isNotificarStockMinimo)
                .orElse(true);
    }

    @Transactional(readOnly = true)
    public boolean notificarSolicitudesPendientes() {
        return configuracionNotificacionRepository
                .findFirstByOrderByIdConfiguracionAsc()
                .map(ConfiguracionNotificacion::isNotificarSolicitudesPendientes)
                .orElse(true);
    }
}
