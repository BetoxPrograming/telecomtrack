package com.telecomtrack.service;

import com.telecomtrack.domain.Rol;
import com.telecomtrack.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Transactional(readOnly = true)
    public List<Rol> getRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rol> getRol(Integer idRol) {
        return rolRepository.findById(idRol);
    }

    @Transactional(readOnly = true)
    public Optional<Rol> getRolPorNombre(String rol) {
        return rolRepository.findByRol(rol);
    }
}
