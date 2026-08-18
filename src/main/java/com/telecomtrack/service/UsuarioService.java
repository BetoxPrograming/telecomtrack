package com.telecomtrack.service;

import com.telecomtrack.domain.Rol;
import com.telecomtrack.domain.Usuario;
import com.telecomtrack.repository.RolRepository;
import com.telecomtrack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final String ROL_TECNICO = "Técnico";
    private static final String ROL_SUPERVISOR = "Supervisor";
    private static final String ROL_BODEGUERO = "Bodeguero";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activos) {
        if (activos) {
            return usuarioRepository.findByActivoTrue();
        }

        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> getTecnicosActivos() {
        return usuarioRepository
                .findByActivoTrueAndRolOrderByNombreAsc(ROL_TECNICO);
    }

    @Transactional(readOnly = true)
    public List<Usuario> getSupervisoresActivos() {
        return usuarioRepository
                .findByActivoTrueAndRolOrderByNombreAsc(ROL_SUPERVISOR);
    }

    @Transactional(readOnly = true)
    public List<Usuario> getBodeguerosActivos() {
        return usuarioRepository
                .findByActivoTrueAndRolOrderByNombreAsc(ROL_BODEGUERO);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional
    public void save(Usuario usuario) {
        sincronizarRol(usuario);
        usuarioRepository.save(usuario);
    }

    private void sincronizarRol(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            return;
        }

        Optional<Rol> rol = rolRepository.findByRol(usuario.getRol());

        if (rol.isPresent()) {
            usuario.getRoles().clear();
            usuario.getRoles().add(rol.get());
        }
    }

    @Transactional
    public void desactivar(Integer idUsuario) {

        var usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new IllegalArgumentException("El usuario no existe")
                );

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}
