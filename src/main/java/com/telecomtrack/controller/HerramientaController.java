package com.telecomtrack.controller;

import com.telecomtrack.domain.Herramienta;
import com.telecomtrack.service.HerramientaService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequestMapping("/herramienta")
public class HerramientaController {

    private final HerramientaService herramientaService;
    private final MessageSource messageSource;

    public HerramientaController(HerramientaService herramientaService, MessageSource messageSource) {
        this.herramientaService = herramientaService;
        this.messageSource = messageSource;
    }

    private void agregarMensajeExito(RedirectAttributes redirectAttributes, String mensajeCodigo, Locale locale) {
        redirectAttributes.addFlashAttribute("mensaje", messageSource.getMessage(mensajeCodigo, null, locale));
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        var herramientas = herramientaService.getHerramientas();

        model.addAttribute("herramientas", herramientas);

        return "herramienta/listado";
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model) {

        var herramientas = herramientaService.getHerramientas();

        model.addAttribute("herramientas", herramientas);

        return "herramienta/catalogo";
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
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (herramientaService.codigoDuplicado(herramienta)) {
            bindingResult.rejectValue("codigo", "validacion.herramienta.codigo.duplicado");
        }

        if (bindingResult.hasErrors()) {
            return "herramienta/modifica";
        }

        herramientaService.guardar(herramienta);

        agregarMensajeExito(redirectAttributes, "herramienta.mensaje.guardada", locale);

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
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

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

        agregarMensajeExito(redirectAttributes, "herramienta.mensaje.baja", locale);

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
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (herramienta.getFechaRetornoEstimada() == null) {
            bindingResult.rejectValue("fechaRetornoEstimada", "validacion.herramienta.fechaRetornoEstimada.requerido");
        }

        if (bindingResult.hasErrors()) {
            return "herramienta/mantenimiento";
        }

        herramientaService.mantenimiento(
                herramienta.getIdHerramienta(),
                herramienta.getFechaRetornoEstimada());

        agregarMensajeExito(redirectAttributes, "herramienta.mensaje.mantenimiento", locale);

        return "redirect:/herramienta/listado";
    }

    @PostMapping("/disponible")
    public String volverDisponible(
            @RequestParam Integer idHerramienta,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        herramientaService.volverDisponible(idHerramienta);

        agregarMensajeExito(redirectAttributes, "herramienta.mensaje.disponible", locale);

        return "redirect:/herramienta/listado";
    }
}
