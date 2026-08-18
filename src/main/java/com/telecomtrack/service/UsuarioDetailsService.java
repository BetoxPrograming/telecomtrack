package com.telecomtrack.service;

import com.telecomtrack.domain.Usuario;
import com.telecomtrack.repository.UsuarioRepository;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /*
     * Spring Security envía en "username" el valor escrito en el formulario.
     * En TelecomTrack se utiliza el correo como identificador de inicio de sesión.
     * La consulta incluye activo=true para impedir el acceso de usuarios desactivados.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: " + username)
                );

        var roles = usuario.getRoles().stream()
                .map(rol ->
                        new SimpleGrantedAuthority("ROLE_" + rol.getRol()))
                .collect(Collectors.toSet());

        return new User(
                usuario.getCorreo(),
                usuario.getPassword(),
                roles
        );
    }
}
