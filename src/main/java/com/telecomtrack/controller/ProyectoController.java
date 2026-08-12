package com.telecomtrack.controller;

import com.telecomtrack.domain.Proyecto;
import com.telecomtrack.service.ListadoMaterialEstimadoService;
import com.telecomtrack.service.ProyectoService;
import com.telecomtrack.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/proyecto")
public class ProyectoController {

    private final ProyectoService proyectoService;
    private final UsuarioService usuarioService;
    private final ListadoMaterialEstimadoService listadoMaterialEstimadoService;

    public ProyectoController(ProyectoService proyectoService, UsuarioService usuarioService,
                               ListadoMaterialEstimadoService listadoMaterialEstimadoService) {
        this.proyectoService = proyectoService;
        this.usuarioService = usuarioService;
        this.listadoMaterialEstimadoService = listadoMaterialEstimadoService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        var proyectos = proyectoService.getProyectos();

        model.addAttribute("proyectos", proyectos);

        return "proyecto/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("proyecto", new Proyecto());
        model.addAttribute("supervisores", usuarioService.getSupervisoresActivos());

        return "proyecto/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Proyecto proyecto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("supervisores", usuarioService.getSupervisoresActivos());
            return "proyecto/modifica";
        }

        proyectoService.save(proyecto);

        return "redirect:/proyecto/listado";
    }

    @GetMapping("/modificar/{idProyecto}")
    public String modificar(
            @PathVariable Integer idProyecto,
            Model model) {

        var proyecto = proyectoService.getProyecto(idProyecto);

        if (proyecto.isEmpty()) {
            return "redirect:/proyecto/listado";
        }

        model.addAttribute("proyecto", proyecto.get());
        model.addAttribute("supervisores", usuarioService.getSupervisoresActivos());

        return "proyecto/modifica";
    }

    @GetMapping("/consultar/{idProyecto}")
    public String consultar(
            @PathVariable Integer idProyecto,
            Model model) {

        var proyecto = proyectoService.getProyecto(idProyecto);

        if (proyecto.isEmpty()) {
            return "redirect:/proyecto/listado";
        }

        model.addAttribute("proyecto", proyecto.get());
        model.addAttribute("listadosEstimados",
                listadoMaterialEstimadoService.getPorProyecto(idProyecto));

        return "proyecto/consulta";
    }
}
