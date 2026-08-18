package com.telecomtrack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SeguridadController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("idiomaRuta", "/login");
        return "seguridad/login";
    }

    @GetMapping("/acceso_denegado")
    public String accesoDenegado(Model model) {
        model.addAttribute("idiomaRuta", "/acceso_denegado");
        return "seguridad/acceso-denegado";
    }
}
