package com.telecomtrack.service;

import com.telecomtrack.domain.Herramienta;
import com.telecomtrack.repository.HerramientaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HerramientaService {

    public static final String ESTADO_DISPONIBLE = "Disponible";
    public static final String ESTADO_MANTENIMIENTO = "Mantenimiento";
    public static final String ESTADO_BAJA = "Baja";

    private final HerramientaRepository herramientaRepository;

    public HerramientaService(HerramientaRepository herramientaRepository) {
        this.herramientaRepository = herramientaRepository;
    }

    @Transactional(readOnly = true)
    public List<Herramienta> getHerramientas() {
        return herramientaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Herramienta> getHerramienta(Integer idHerramienta) {
        return herramientaRepository.findById(idHerramienta);
    }

    @Transactional(readOnly = true)
    public boolean codigoDuplicado(Herramienta herramienta) {
        var herramientaEncontrada = herramientaRepository.findByCodigo(herramienta.getCodigo());

        if (herramientaEncontrada.isEmpty()) {
            return false;
        }

        if (herramienta.getIdHerramienta() == null) {
            return true;
        }

        return !herramientaEncontrada.get().getIdHerramienta().equals(herramienta.getIdHerramienta());
    }

    @Transactional
    public Herramienta guardar(Herramienta herramienta) {
        aplicarReglasEstado(herramienta);
        return herramientaRepository.save(herramienta);
    }

    @Transactional
    public boolean enviarAMantenimiento(Integer idHerramienta, LocalDate fechaRetornoEstimada) {
        var herramienta = herramientaRepository.findById(idHerramienta).orElse(null);

        if (herramienta == null || !ESTADO_DISPONIBLE.equals(herramienta.getEstado())) {
            return false;
        }

        herramienta.setEstado(ESTADO_MANTENIMIENTO);
        herramienta.setFechaRetornoEstimada(fechaRetornoEstimada);
        herramienta.setFechaBaja(null);
        herramienta.setJustificacionBaja(null);

        herramientaRepository.save(herramienta);
        return true;
    }

    @Transactional
    public boolean darDeBaja(Integer idHerramienta, String justificacionBaja) {
        var herramienta = herramientaRepository.findById(idHerramienta).orElse(null);

        if (herramienta == null || ESTADO_BAJA.equals(herramienta.getEstado())) {
            return false;
        }

        herramienta.setEstado(ESTADO_BAJA);
        herramienta.setFechaRetornoEstimada(null);
        herramienta.setFechaBaja(LocalDate.now());
        herramienta.setJustificacionBaja(justificacionBaja);

        herramientaRepository.save(herramienta);
        return true;
    }

    @Transactional
    public boolean volverDisponible(Integer idHerramienta) {
        var herramienta = herramientaRepository.findById(idHerramienta).orElse(null);

        if (herramienta == null || !ESTADO_MANTENIMIENTO.equals(herramienta.getEstado())) {
            return false;
        }

        herramienta.setEstado(ESTADO_DISPONIBLE);
        herramienta.setFechaRetornoEstimada(null);
        herramienta.setFechaBaja(null);
        herramienta.setJustificacionBaja(null);

        herramientaRepository.save(herramienta);
        return true;
    }

    private void aplicarReglasEstado(Herramienta herramienta) {
        if (ESTADO_DISPONIBLE.equals(herramienta.getEstado())) {
            herramienta.setFechaRetornoEstimada(null);
            herramienta.setFechaBaja(null);
            herramienta.setJustificacionBaja(null);
            return;
        }

        if (ESTADO_MANTENIMIENTO.equals(herramienta.getEstado())) {
            herramienta.setFechaBaja(null);
            herramienta.setJustificacionBaja(null);
            return;
        }

        if (ESTADO_BAJA.equals(herramienta.getEstado())) {
            herramienta.setFechaRetornoEstimada(null);
        }
    }
}
