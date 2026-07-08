package com.telecomtrack.controller;

import com.telecomtrack.domain.AsignacionHerramienta;
import com.telecomtrack.service.AsignacionHerramientaService;
import com.telecomtrack.service.HerramientaService;
import com.telecomtrack.service.ProyectoService;
import com.telecomtrack.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Locale;

@Controller
@RequestMapping("/asignacion")
public class AsignacionHerramientaController {

    private final AsignacionHerramientaService asignacionHerramientaService;
    private final HerramientaService herramientaService;
    private final UsuarioService usuarioService;
    private final ProyectoService proyectoService;
    private final MessageSource messageSource;

    public AsignacionHerramientaController(
            AsignacionHerramientaService asignacionHerramientaService,
            HerramientaService herramientaService,
            UsuarioService usuarioService,
            ProyectoService proyectoService,
            MessageSource messageSource) {

        this.asignacionHerramientaService = asignacionHerramientaService;
        this.herramientaService = herramientaService;
        this.usuarioService = usuarioService;
        this.proyectoService = proyectoService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        model.addAttribute(
                "asignaciones",
                asignacionHerramientaService.getAsignacionesActivas()
        );

        return "asignacion/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        AsignacionHerramienta asignacionHerramienta =
                new AsignacionHerramienta();

        asignacionHerramienta.setFechaAsignacion(LocalDate.now());
        asignacionHerramienta.setActiva(true);

        model.addAttribute(
                "asignacionHerramienta",
                asignacionHerramienta
        );

        cargarDatosFormulario(model);

        return "asignacion/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid
            @ModelAttribute("asignacionHerramienta")
            AsignacionHerramienta asignacionHerramienta,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (bindingResult.hasErrors()) {
            cargarDatosFormulario(model);
            return "asignacion/modifica";
        }

        try {
            asignacionHerramientaService.save(
                    asignacionHerramienta
            );

            String mensaje = messageSource.getMessage(
                    "mensaje.asignacion.guardada",
                    null,
                    "La asignación se registró correctamente.",
                    locale
            );

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    mensaje
            );

            redirectAttributes.addFlashAttribute(
                    "tipoMensaje",
                    "success"
            );

            return "redirect:/asignacion/listado";

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            String mensaje = messageSource.getMessage(
                    exception.getMessage(),
                    null,
                    "No fue posible registrar la asignación.",
                    locale
            );

            bindingResult.reject(
                    "asignacion.error",
                    mensaje
            );

            cargarDatosFormulario(model);

            return "asignacion/modifica";
        }
    }

    private void cargarDatosFormulario(Model model) {

        model.addAttribute(
                "herramientas",
                herramientaService.getHerramientasDisponibles()
        );

        model.addAttribute(
                "tecnicos",
                usuarioService.getTecnicosActivos()
        );

        model.addAttribute(
                "proyectos",
                proyectoService.getProyectosActivos()
        );
    }
}