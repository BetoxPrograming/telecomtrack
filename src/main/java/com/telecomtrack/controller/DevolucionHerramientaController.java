package com.telecomtrack.controller;

import com.telecomtrack.domain.FotoDevolucionHerramienta;
import com.telecomtrack.domain.Usuario;
import com.telecomtrack.service.DevolucionHerramientaService;
import com.telecomtrack.service.UsuarioService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/devolucion")
public class DevolucionHerramientaController {

    private final DevolucionHerramientaService devolucionHerramientaService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public DevolucionHerramientaController(
            DevolucionHerramientaService devolucionHerramientaService,
            UsuarioService usuarioService,
            MessageSource messageSource) {

        this.devolucionHerramientaService = devolucionHerramientaService;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    private Usuario getUsuarioAutenticado(Principal principal) {
        return usuarioService.getUsuarioPorCorreoActivo(principal.getName());
    }

    @GetMapping("/nueva")
    public String nueva(Principal principal, Model model) {
        Usuario tecnico = getUsuarioAutenticado(principal);
        cargarFormulario(model, tecnico);
        return "devolucion/nueva";
    }

    @PostMapping("/guardar")
    public String guardar(
            Principal principal,
            @RequestParam Integer idAsignacion,
            @RequestParam String estadoDevolucion,
            @RequestParam(required = false) LocalDate fechaRetornoEstimada,
            @RequestParam(required = false) String justificacionBaja,
            @RequestParam(required = false) List<MultipartFile> fotos,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Usuario tecnico = getUsuarioAutenticado(principal);

        try {
            devolucionHerramientaService.registrarDevolucion(
                    idAsignacion,
                    tecnico.getIdUsuario(),
                    estadoDevolucion,
                    fechaRetornoEstimada,
                    justificacionBaja,
                    fotos == null ? new ArrayList<>() : fotos);

            agregarMensaje(
                    redirectAttributes,
                    "devolucion.mensaje.guardada",
                    "success",
                    locale);

            return "redirect:/devolucion/historial";

        } catch (IllegalArgumentException | IllegalStateException exception) {
            cargarFormulario(model, tecnico);
            model.addAttribute(
                    "errorGeneral",
                    messageSource.getMessage(
                            exception.getMessage(),
                            null,
                            exception.getMessage(),
                            locale));
            return "devolucion/nueva";
        }
    }

    @GetMapping("/historial")
    public String historial(
            Principal principal,
            @RequestParam(required = false) Integer idProyecto,
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            Model model) {

        Usuario tecnico = getUsuarioAutenticado(principal);

        var devoluciones = devolucionHerramientaService.getHistorialTecnico(
                tecnico.getIdUsuario(),
                idProyecto,
                fechaInicio,
                fechaFin);

        Map<Integer, List<FotoDevolucionHerramienta>> fotosPorDevolucion = new LinkedHashMap<>();

        for (var devolucion : devoluciones) {
            fotosPorDevolucion.put(
                    devolucion.getIdDevolucion(),
                    devolucionHerramientaService.getFotos(devolucion.getIdDevolucion()));
        }

        model.addAttribute(
                "proyectos",
                devolucionHerramientaService.getProyectosTecnico(tecnico.getIdUsuario()));
        model.addAttribute("devoluciones", devoluciones);
        model.addAttribute("fotosPorDevolucion", fotosPorDevolucion);
        model.addAttribute("idProyecto", idProyecto);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("idiomaRuta", "/devolucion/historial");
        return "devolucion/historial";
    }

    private void cargarFormulario(Model model, Usuario tecnico) {
        model.addAttribute("tecnico", tecnico);
        model.addAttribute(
                "asignaciones",
                devolucionHerramientaService
                        .getAsignacionesActivasDeTecnico(tecnico.getIdUsuario()));
        model.addAttribute("estadosDevolucion", estadosDevolucion());
        model.addAttribute("idiomaRuta", "/devolucion/nueva");
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

        redirectAttributes.addFlashAttribute(
                "mensaje",
                messageSource.getMessage(codigo, null, codigo, locale));
        redirectAttributes.addFlashAttribute("tipoMensaje", tipo);
    }
}
