package com.telecomtrack.service;

import com.telecomtrack.domain.Usuario;
import com.telecomtrack.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Objects;

@Service
public class UsuarioActualService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioActualService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioAutenticado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            Object idUsuario = session.getAttribute("usuarioAutenticadoId");
            if (idUsuario instanceof Integer usuarioId) {
                return usuarioRepository.findById(usuarioId).orElse(null);
            }

            Object correo = session.getAttribute("usuarioAutenticadoCorreo");
            if (correo instanceof String correoUsuario && !correoUsuario.isBlank()) {
                return usuarioRepository.findByActivoTrue().stream()
                        .filter(usuario -> correoUsuario.equalsIgnoreCase(usuario.getCorreo()))
                        .findFirst()
                        .orElse(null);
            }
        }

        Principal principal = request.getUserPrincipal();
        if (principal != null && principal.getName() != null) {
            String nombreUsuario = principal.getName();

            return usuarioRepository.findByActivoTrue().stream()
                    .filter(usuario -> nombreUsuario.equalsIgnoreCase(usuario.getCorreo()))
                    .findFirst()
                    .orElse(null);
        }

        return usuarioRepository.findByActivoTrue().stream()
                .filter(usuario -> "Técnico".equals(usuario.getRol()))
                .findFirst()
                .orElse(null);
    }

    public boolean esRol(Usuario usuario, String rol) {
        return usuario != null && Objects.equals(usuario.getRol(), rol);
    }

    public boolean esTecnico(Usuario usuario) {
        return esRol(usuario, "Técnico");
    }

    public boolean esBodeguero(Usuario usuario) {
        return esRol(usuario, "Bodeguero");
    }

    public boolean esAdministrador(Usuario usuario) {
        return esRol(usuario, "Administrador");
    }
}
