package com.telecomtrack.controller;

import com.telecomtrack.domain.Ubicacion;
import com.telecomtrack.service.UbicacionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ubicacion")
public class UbicacionController {

    private final UbicacionService ubicacionService;

    public UbicacionController(UbicacionService ubicacionService) {
        this.ubicacionService = ubicacionService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("ubicaciones", ubicacionService.getUbicaciones());
        model.addAttribute("idiomaRuta", "/ubicacion/listado");
        return "ubicacion/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("ubicacion", new Ubicacion());
        model.addAttribute("idiomaRuta", "/ubicacion/nuevo");
        return "ubicacion/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Ubicacion ubicacion,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("idiomaRuta", ubicacion.getIdUbicacion() == null
                    ? "/ubicacion/nuevo"
                    : "/ubicacion/modificar/" + ubicacion.getIdUbicacion());
            return "ubicacion/modifica";
        }

        ubicacionService.save(ubicacion);
        return "redirect:/ubicacion/listado";
    }

    @GetMapping("/modificar/{idUbicacion}")
    public String modificar(
            @PathVariable Integer idUbicacion,
            Model model) {

        var ubicacion = ubicacionService.getUbicacion(idUbicacion);

        if (ubicacion.isEmpty()) {
            return "redirect:/ubicacion/listado";
        }

        model.addAttribute("ubicacion", ubicacion.get());
        model.addAttribute("idiomaRuta", "/ubicacion/modificar/" + idUbicacion);
        return "ubicacion/modifica";
    }

    @GetMapping("/consultar/{idUbicacion}")
    public String consultar(
            @PathVariable Integer idUbicacion,
            Model model) {

        var ubicacion = ubicacionService.getUbicacion(idUbicacion);

        if (ubicacion.isEmpty()) {
            return "redirect:/ubicacion/listado";
        }

        model.addAttribute("ubicacion", ubicacion.get());
        model.addAttribute("idiomaRuta", "/ubicacion/consultar/" + idUbicacion);
        return "ubicacion/consulta";
    }
}
