package com.telecomtrack.controller;

import com.telecomtrack.service.DashboardService;
import com.telecomtrack.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Restriccion por rol (Admin/Supervisor) queda pendiente de Spring Security (Issue #14);
// por ahora estas rutas quedan abiertas igual que el resto del proyecto.
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
    public String supervisor(@RequestParam(required = false) Integer supervisorId, Model model) {

        model.addAttribute("supervisores", usuarioService.getSupervisoresActivos());
        model.addAttribute("supervisorId", supervisorId);

        if (supervisorId != null) {
            model.addAttribute("resumen", dashboardService.getResumenSupervisor(supervisorId));
        }

        return "dashboard/supervisor";
    }
}
