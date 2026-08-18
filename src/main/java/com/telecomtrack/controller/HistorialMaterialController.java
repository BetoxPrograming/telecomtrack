package com.telecomtrack.controller;

import com.telecomtrack.domain.Usuario;
import com.telecomtrack.service.MovimientoService;
import com.telecomtrack.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/historial-material")
public class HistorialMaterialController {

    private final MovimientoService movimientoService;
    private final UsuarioService usuarioService;

    public HistorialMaterialController(
            MovimientoService movimientoService,
            UsuarioService usuarioService) {
        this.movimientoService = movimientoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String historial(Principal principal, Model model) {
        Usuario tecnico = usuarioService.getUsuarioPorCorreoActivo(principal.getName());

        model.addAttribute(
                "movimientos",
                movimientoService.getSalidasPorTecnico(tecnico.getIdUsuario()));
        model.addAttribute("idiomaRuta", "/historial-material");
        return "materiales/historial-tecnico";
    }
}
