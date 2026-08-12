package com.telecomtrack.controller;

import com.telecomtrack.domain.DevolucionHerramienta;
import com.telecomtrack.service.DevolucionHerramientaService;
import com.telecomtrack.service.ProyectoService;
import com.telecomtrack.service.UsuarioActualService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/devolucion")
public class DevolucionHerramientaController {

    private final DevolucionHerramientaService devolucionHerramientaService;
    private final UsuarioActualService usuarioActualService;
    private final ProyectoService proyectoService;
    private final MessageSource messageSource;

    public DevolucionHerramientaController(
            DevolucionHerramientaService devolucionHerramientaService,
            UsuarioActualService usuarioActualService,
            ProyectoService proyectoService,
            MessageSource messageSource) {

        this.devolucionHerramientaService = devolucionHerramientaService;
        this.usuarioActualService = usuarioActualService;
        this.proyectoService = proyectoService;
        this.messageSource = messageSource;
    }

    @GetMapping("/nueva")
    public String nueva(
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        var tecnico = usuarioActualService.getUsuarioAutenticado(request);

        if (tecnico == null) {
            agregarMensaje(redirectAttributes, "devolucion.error.autenticacion", "danger", locale);
            return "redirect:/";
        }

        model.addAttribute("tecnico", tecnico);
        model.addAttribute("asignaciones", devolucionHerramientaService.getAsignacionesActivasDeTecnico(tecnico.getIdUsuario()));
        model.addAttribute("estadosDevolucion", estadosDevolucion());
        model.addAttribute("idiomaRuta", "/devolucion/nueva");
        return "devolucion/nueva";
    }

    @PostMapping("/guardar")
    public String guardar(
            HttpServletRequest request,
            @RequestParam Integer idAsignacion,
            @RequestParam String estadoDevolucion,
            @RequestParam(required = false) List<MultipartFile> fotos,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        var tecnico = usuarioActualService.getUsuarioAutenticado(request);

        if (tecnico == null) {
            agregarMensaje(redirectAttributes, "devolucion.error.autenticacion", "danger", locale);
            return "redirect:/";
        }

        try {
            DevolucionHerramienta devolucion = devolucionHerramientaService.registrarDevolucion(
                    idAsignacion,
                    tecnico.getIdUsuario(),
                    estadoDevolucion,
                    fotos == null ? new ArrayList<>() : fotos);

            agregarMensaje(redirectAttributes, "devolucion.mensaje.guardada", "success", locale);
            return "redirect:/devolucion/historial";

        } catch (IllegalStateException exception) {
            model.addAttribute("tecnico", tecnico);
            model.addAttribute("asignaciones", devolucionHerramientaService.getAsignacionesActivasDeTecnico(tecnico.getIdUsuario()));
            model.addAttribute("estadosDevolucion", estadosDevolucion());
            model.addAttribute("errorGeneral", messageSource.getMessage(exception.getMessage(), null, locale));
            model.addAttribute("idiomaRuta", "/devolucion/nueva");
            return "devolucion/nueva";
        }
    }

    @GetMapping("/historial")
    public String historial(
            HttpServletRequest request,
            @RequestParam(required = false) Integer idProyecto,
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        var tecnico = usuarioActualService.getUsuarioAutenticado(request);

        if (tecnico == null) {
            agregarMensaje(redirectAttributes, "devolucion.error.autenticacion", "danger", locale);
            return "redirect:/";
        }

        model.addAttribute("proyectos", proyectoService.getProyectosActivos());
        model.addAttribute("devoluciones", devolucionHerramientaService.getHistorialTecnico(tecnico.getIdUsuario(), idProyecto, fechaInicio, fechaFin));
        model.addAttribute("idProyecto", idProyecto);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("idiomaRuta", "/devolucion/historial");
        return "devolucion/historial";
    }

    private List<String> estadosDevolucion() {
        List<String> estados = new ArrayList<>();
        estados.add(DevolucionHerramientaService.ESTADO_BUENA);
        estados.add(DevolucionHerramientaService.ESTADO_DANADA);
        estados.add(DevolucionHerramientaService.ESTADO_PERDIDA);
        return estados;
    }

    private void agregarMensaje(
            RedirectAttributes redirectAttributes,
            String codigo,
            String tipo,
            Locale locale) {

        redirectAttributes.addFlashAttribute("mensaje", messageSource.getMessage(codigo, null, locale));
        redirectAttributes.addFlashAttribute("tipoMensaje", tipo);
    }
}
