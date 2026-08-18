package com.telecomtrack.service;

import com.telecomtrack.domain.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Objects;

@Service
public class UsuarioActualService {

    private final UsuarioService usuarioService;

    public UsuarioActualService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario getUsuarioAutenticado(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            return null;
        }

        try {
            return usuarioService.getUsuarioPorCorreoActivo(principal.getName());
        } catch (IllegalArgumentException exception) {
            return null;
        }
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
