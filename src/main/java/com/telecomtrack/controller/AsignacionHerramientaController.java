package com.telecomtrack.controller;

import com.telecomtrack.domain.AsignacionHerramienta;
import com.telecomtrack.domain.Herramienta;
import com.telecomtrack.domain.Proyecto;
import com.telecomtrack.domain.Usuario;
import com.telecomtrack.service.AsignacionHerramientaService;
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
    private final UsuarioService usuarioService;
    private final ProyectoService proyectoService;
    private final MessageSource messageSource;

    public AsignacionHerramientaController(
            AsignacionHerramientaService asignacionHerramientaService,
            UsuarioService usuarioService,
            ProyectoService proyectoService,
            MessageSource messageSource) {

        this.asignacionHerramientaService = asignacionHerramientaService;
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

        asignacionHerramienta.setHerramienta(new Herramienta());
        asignacionHerramienta.setTecnico(new Usuario());
        asignacionHerramienta.setProyecto(new Proyecto());
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

        validarSelecciones(
                asignacionHerramienta,
                bindingResult
        );

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

            bindingResult.reject(exception.getMessage());
            cargarDatosFormulario(model);

            return "asignacion/modifica";
        }
    }

    private void validarSelecciones(
            AsignacionHerramienta asignacion,
            BindingResult bindingResult) {

        if (asignacion.getHerramienta() == null
                || asignacion.getHerramienta()
                .getIdHerramienta() == null) {

            bindingResult.rejectValue(
                    "herramienta",
                    "validacion.asignacion.herramienta.requerida"
            );
        }

        if (asignacion.getTecnico() == null
                || asignacion.getTecnico()
                .getIdUsuario() == null) {

            bindingResult.rejectValue(
                    "tecnico",
                    "validacion.asignacion.tecnico.requerido"
            );
        }

        if (asignacion.getProyecto() == null
                || asignacion.getProyecto()
                .getIdProyecto() == null) {

            bindingResult.rejectValue(
                    "proyecto",
                    "validacion.asignacion.proyecto.requerido"
            );
        }
    }

    private void cargarDatosFormulario(Model model) {

        model.addAttribute(
                "herramientas",
                asignacionHerramientaService
                        .getHerramientasDisponibles()
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
