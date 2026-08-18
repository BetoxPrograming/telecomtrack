package com.telecomtrack.controller;

import com.telecomtrack.domain.Usuario;
import com.telecomtrack.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    private void agregarRoles(Model model) {
        String[] roles = {
                "{rol.administrador}",
                "{rol.bodeguero}",
                "{rol.tecnico}",
                "{rol.supervisor}",
                "{rol.visitante}"
        };

        model.addAttribute("roles", roles);
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("usuarios", usuarioService.getUsuarios(false));
        model.addAttribute("idiomaRuta", "/usuario/listado");
        return "usuario/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Usuario usuario, Model model) {
        usuario.setActivo(true);
        agregarRoles(model);
        model.addAttribute("idiomaRuta", "/usuario/nuevo");
        return "usuario/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Usuario usuario,
            BindingResult bindingResult,
            Model model) {

        if (usuario.getIdUsuario() == null
                && (usuario.getPassword() == null
                || usuario.getPassword().isBlank())) {
            bindingResult.rejectValue(
                    "password",
                    "validacion.usuario.password.requerida");
        }

        if (bindingResult.hasErrors()) {
            agregarRoles(model);
            model.addAttribute("idiomaRuta", usuario.getIdUsuario() == null
                    ? "/usuario/nuevo"
                    : "/usuario/modificar/" + usuario.getIdUsuario());
            return "usuario/modifica";
        }

        usuarioService.save(usuario);
        return "redirect:/usuario/listado";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(
            @PathVariable Integer idUsuario,
            Model model) {

        var usuario = usuarioService.getUsuario(idUsuario);

        if (usuario.isEmpty()) {
            return "redirect:/usuario/listado";
        }

        var usuarioActual = usuario.get();
        usuarioActual.setPassword(null);

        model.addAttribute("usuario", usuarioActual);
        model.addAttribute("idiomaRuta", "/usuario/modificar/" + idUsuario);
        agregarRoles(model);
        return "usuario/modifica";
    }

    @PostMapping("/desactivar")
    public String desactivar(@RequestParam Integer idUsuario) {
        usuarioService.desactivar(idUsuario);
        return "redirect:/usuario/listado";
    }
}
