package com.telecomtrack.controller;

import com.telecomtrack.domain.Usuario;
import com.telecomtrack.service.DashboardService;
import com.telecomtrack.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UsuarioService usuarioService;

    public DashboardController(DashboardService dashboardService, UsuarioService usuarioService) {
        this.dashboardService = dashboardService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String administrador(Model model) {

        model.addAttribute("resumen", dashboardService.getResumenAdministrador());
        return "dashboard/administrador";
    }

    @GetMapping("/supervisor")
    public String supervisor(Principal principal, Model model) {

        Usuario supervisor = usuarioService.getUsuarioPorCorreoActivo(principal.getName());

        model.addAttribute("supervisor", supervisor);
        model.addAttribute("resumen",
                dashboardService.getResumenSupervisor(supervisor.getIdUsuario()));

        return "dashboard/supervisor";
    }
}
