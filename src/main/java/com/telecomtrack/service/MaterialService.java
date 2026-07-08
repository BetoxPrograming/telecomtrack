package com.telecomtrack.service;

import com.telecomtrack.domain.Material;
import com.telecomtrack.repository.MaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Transactional(readOnly = true)
    public List<Material> listarTodos() {
        return materialRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Material> listarConStockBajo() {
        return materialRepository.findMaterialesConStockBajo();
    }

    @Transactional(readOnly = true)
    public Optional<Material> buscarPorId(Long id) {
        return materialRepository.findById(id);
    }

    public Material guardar(Material material) {
        return materialRepository.save(material);
    }

    @Transactional(readOnly = true)
    public boolean existeCodigoUnico(String codigo) {
        return materialRepository.existsByCodigoUnico(codigo);
    }

    @Transactional(readOnly = true)
    public boolean existeCodigoUnicoExcluyendo(String codigo, Long id) {
        return materialRepository.existsByCodigoUnicoAndIdMaterialNot(codigo, id);
    }
}
