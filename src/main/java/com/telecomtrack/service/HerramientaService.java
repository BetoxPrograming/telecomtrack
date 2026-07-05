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
    public void save(Herramienta herramienta) {

        if (!"Mantenimiento".equals(herramienta.getEstado())) {
            herramienta.setFechaRetornoEstimada(null);
        }

        if (!"Baja".equals(herramienta.getEstado())) {
            herramienta.setFechaBajaDefinitiva(null);
            herramienta.setJustificacionBajaDefinitiva(null);
        }

        herramientaRepository.save(herramienta);
    }

    @Transactional
    public void bajaDefinitiva(Integer idHerramienta, String justificacionBajaDefinitiva) {
        var herramienta = herramientaRepository.findById(idHerramienta)
                .orElseThrow(() -> new IllegalArgumentException("La herramienta no existe"));

        herramienta.setEstado("Baja");
        herramienta.setFechaRetornoEstimada(null);
        herramienta.setFechaBajaDefinitiva(LocalDate.now());
        herramienta.setJustificacionBajaDefinitiva(justificacionBajaDefinitiva);

        herramientaRepository.save(herramienta);
    }

    @Transactional
    public void mantenimiento(Integer idHerramienta, LocalDate fechaRetornoEstimada) {
        var herramienta = herramientaRepository.findById(idHerramienta)
                .orElseThrow(() -> new IllegalArgumentException("La herramienta no existe"));

        herramienta.setEstado("Mantenimiento");
        herramienta.setFechaBajaDefinitiva(null);
        herramienta.setJustificacionBajaDefinitiva(null);
        herramienta.setFechaRetornoEstimada(fechaRetornoEstimada);

        herramientaRepository.save(herramienta);
    }

    @Transactional
    public void volverDisponible(Integer idHerramienta) {
        var herramienta = herramientaRepository.findById(idHerramienta)
                .orElseThrow(() -> new IllegalArgumentException("La herramienta no existe"));

        herramienta.setEstado("Disponible");
        herramienta.setFechaRetornoEstimada(null);

        herramientaRepository.save(herramienta);
    }
}
