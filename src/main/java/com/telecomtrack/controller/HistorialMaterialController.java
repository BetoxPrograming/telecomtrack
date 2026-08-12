package com.telecomtrack.controller;

import com.telecomtrack.service.MovimientoService;
import com.telecomtrack.service.UsuarioActualService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/historial-material")
public class HistorialMaterialController {

    private final MovimientoService movimientoService;
    private final UsuarioActualService usuarioActualService;

    public HistorialMaterialController(MovimientoService movimientoService,
                                       UsuarioActualService usuarioActualService) {
        this.movimientoService = movimientoService;
        this.usuarioActualService = usuarioActualService;
    }

    @GetMapping
    public String historial(HttpServletRequest request, Model model) {
        var usuario = usuarioActualService.getUsuarioAutenticado(request);

        if (usuario == null) {
            return "redirect:/";
        }

        model.addAttribute("movimientos", movimientoService.getMovimientosPorResponsable(usuario.getNombre() + " " + usuario.getApellido()));
        model.addAttribute("idiomaRuta", "/historial-material");
        return "materiales/historial-tecnico";
    }
}
