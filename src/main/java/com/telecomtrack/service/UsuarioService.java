package com.telecomtrack.service;

import com.telecomtrack.domain.Rol;
import com.telecomtrack.domain.Usuario;
import com.telecomtrack.repository.RolRepository;
import com.telecomtrack.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
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

        if (usuario.getIdUsuario() == null) {

            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException(
                        "La contraseña es obligatoria para usuarios nuevos.");
            }

            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        } else {

            Usuario usuarioExistente = usuarioRepository
                    .findById(usuario.getIdUsuario())
                    .orElseThrow(() ->
                            new IllegalArgumentException("El usuario no existe")
                    );

            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword(usuarioExistente.getPassword());
            } else {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        }

        sincronizarRol(usuario);
        usuarioRepository.save(usuario);
    }

    private void sincronizarRol(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            return;
        }

        usuario.getRoles().clear();

        /*
         * El Administrador recibe todos los roles para conservar el patrón
         * trabajado en Tienda: un mismo usuario puede poseer varios roles.
         * Así puede acceder a todos los módulos sin crear una jerarquía nueva.
         */
        if ("Administrador".equals(usuario.getRol())) {
            usuario.getRoles().addAll(rolRepository.findAll());
            return;
        }

        Optional<Rol> rol = rolRepository.findByRol(usuario.getRol());
        rol.ifPresent(value -> usuario.getRoles().add(value));
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
