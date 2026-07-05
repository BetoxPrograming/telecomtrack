package com.telecomtrack.controller;

import com.telecomtrack.domain.Herramienta;
import com.telecomtrack.service.HerramientaService;
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
@RequestMapping("/herramienta")
public class HerramientaController {

    private final HerramientaService herramientaService;

    public HerramientaController(HerramientaService herramientaService) {
        this.herramientaService = herramientaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        var herramientas = herramientaService.getHerramientas();

        model.addAttribute("herramientas", herramientas);

        return "herramienta/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        var herramienta = new Herramienta();
        herramienta.setEstado("Disponible");

        model.addAttribute("herramienta", herramienta);

        return "herramienta/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Herramienta herramienta,
            BindingResult bindingResult) {

        if (herramientaService.codigoDuplicado(herramienta)) {
            bindingResult.rejectValue("codigo", "validacion.herramienta.codigo.duplicado");
        }

        if (bindingResult.hasErrors()) {
            return "herramienta/modifica";
        }

        herramientaService.save(herramienta);

        return "redirect:/herramienta/listado";
    }

    @GetMapping("/modificar/{idHerramienta}")
    public String modificar(
            @PathVariable Integer idHerramienta,
            Model model) {

        var herramienta = herramientaService.getHerramienta(idHerramienta);

        if (herramienta.isEmpty()) {
            return "redirect:/herramienta/listado";
        }

        model.addAttribute("herramienta", herramienta.get());

        return "herramienta/modifica";
    }

    @GetMapping("/consultar/{idHerramienta}")
    public String consultar(
            @PathVariable Integer idHerramienta,
            Model model) {

        var herramienta = herramientaService.getHerramienta(idHerramienta);

        if (herramienta.isEmpty()) {
            return "redirect:/herramienta/listado";
        }

        model.addAttribute("herramienta", herramienta.get());

        return "herramienta/consulta";
    }

    @GetMapping("/baja-definitiva/{idHerramienta}")
    public String bajaDefinitivaFormulario(
            @PathVariable Integer idHerramienta,
            Model model) {

        var herramienta = herramientaService.getHerramienta(idHerramienta);

        if (herramienta.isEmpty()) {
            return "redirect:/herramienta/listado";
        }

        model.addAttribute("herramienta", herramienta.get());

        return "herramienta/baja-definitiva";
    }

    @PostMapping("/baja-definitiva")
    public String bajaDefinitiva(
            @RequestParam Integer idHerramienta,
            @RequestParam(required = false) String justificacionBajaDefinitiva,
            Model model) {

        if (justificacionBajaDefinitiva == null || justificacionBajaDefinitiva.isBlank()) {
            var herramienta = herramientaService.getHerramienta(idHerramienta);

            if (herramienta.isEmpty()) {
                return "redirect:/herramienta/listado";
            }

            model.addAttribute("herramienta", herramienta.get());
            model.addAttribute("errorJustificacion", true);

            return "herramienta/baja-definitiva";
        }

        herramientaService.bajaDefinitiva(idHerramienta, justificacionBajaDefinitiva.trim());

        return "redirect:/herramienta/listado";
    }

    @GetMapping("/mantenimiento/{idHerramienta}")
    public String mantenimientoFormulario(
            @PathVariable Integer idHerramienta,
            Model model) {

        var herramienta = herramientaService.getHerramienta(idHerramienta);

        if (herramienta.isEmpty()) {
            return "redirect:/herramienta/listado";
        }

        model.addAttribute("herramienta", herramienta.get());

        return "herramienta/mantenimiento";
    }

    @PostMapping("/mantenimiento")
    public String mantenimiento(
            @Valid Herramienta herramienta,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "herramienta/mantenimiento";
        }

        herramientaService.mantenimiento(
                herramienta.getIdHerramienta(),
                herramienta.getFechaRetornoEstimada());

        return "redirect:/herramienta/listado";
    }

    @PostMapping("/disponible")
    public String volverDisponible(
            @RequestParam Integer idHerramienta) {

        herramientaService.volverDisponible(idHerramienta);

        return "redirect:/herramienta/listado";
    }
}
