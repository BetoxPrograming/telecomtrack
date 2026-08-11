package com.telecomtrack.controller;

import com.telecomtrack.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/solicitud")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final UsuarioService usuarioService;
    private final ProyectoService proyectoService;
    private final UbicacionService ubicacionService;
    private final HerramientaService herramientaService;
    private final MaterialService materialService;
    private final MessageSource messageSource;

    public SolicitudController(SolicitudService solicitudService,
                                UsuarioService usuarioService,
                                ProyectoService proyectoService,
                                UbicacionService ubicacionService,
                                HerramientaService herramientaService,
                                MaterialService materialService,
                                MessageSource messageSource) {
        this.solicitudService = solicitudService;
        this.usuarioService = usuarioService;
        this.proyectoService = proyectoService;
        this.ubicacionService = ubicacionService;
        this.herramientaService = herramientaService;
        this.materialService = materialService;
        this.messageSource = messageSource;
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) Integer ubicacionId, Model model) {

        model.addAttribute("tecnicos", usuarioService.getTecnicosActivos());
        model.addAttribute("proyectos", proyectoService.getProyectosActivos());
        model.addAttribute("ubicaciones", ubicacionService.getUbicaciones());
        model.addAttribute("ubicacionId", ubicacionId);

        if (ubicacionId != null) {
            model.addAttribute("herramientas", herramientaService.getHerramientasDisponibles(ubicacionId, null));
            model.addAttribute("materiales", materialService.buscarPorUbicacion(ubicacionId, null));
        }

        return "solicitud/nueva";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer tecnicoId,
                           @RequestParam Integer proyectoId,
                           @RequestParam Integer ubicacionId,
                           @RequestParam(required = false) List<Integer> herramientaIds,
                           @RequestParam(required = false) List<Integer> materialIds,
                           HttpServletRequest request,
                           RedirectAttributes flash) {

        Map<Integer, Integer> materialCantidades = new HashMap<>();

        if (materialIds != null) {
            for (Integer idMaterial : materialIds) {
                String cantidadParam = request.getParameter("cantidadMaterial_" + idMaterial);
                Integer cantidad = null;
                if (cantidadParam != null && !cantidadParam.isBlank()) {
                    try {
                        cantidad = Integer.valueOf(cantidadParam.trim());
                    } catch (NumberFormatException ignored) {
                        cantidad = null;
                    }
                }
                materialCantidades.put(idMaterial, cantidad);
            }
        }

        try {
            solicitudService.crear(tecnicoId, proyectoId, ubicacionId, herramientaIds, materialCantidades);

            flash.addFlashAttribute("exitoo", msg("solicitud.mensaje.creada"));

            return "redirect:/solicitud/mis-solicitudes?tecnicoId=" + tecnicoId;

        } catch (IllegalArgumentException | IllegalStateException exception) {

            flash.addFlashAttribute("error", msg(exception.getMessage()));

            return "redirect:/solicitud/nueva?ubicacionId=" + ubicacionId;
        }
    }

    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(@RequestParam(required = false) Integer tecnicoId, Model model) {

        model.addAttribute("tecnicos", usuarioService.getTecnicosActivos());
        model.addAttribute("tecnicoId", tecnicoId);

        if (tecnicoId != null) {
            model.addAttribute("solicitudes", solicitudService.getMisSolicitudes(tecnicoId));
        }

        return "solicitud/mis-solicitudes";
    }

    @GetMapping("/pendientes")
    public String pendientes(@RequestParam(required = false) Integer bodegueroId, Model model) {

        model.addAttribute("bodegueros", usuarioService.getBodeguerosActivos());
        model.addAttribute("bodegueroId", bodegueroId);
        model.addAttribute("pendientes", solicitudService.getPendientes());

        return "solicitud/pendientes";
    }

    @PostMapping("/aprobar/{idSolicitud}")
    public String aprobar(@PathVariable Integer idSolicitud,
                           @RequestParam Integer bodegueroId,
                           RedirectAttributes flash) {

        try {
            solicitudService.aprobar(idSolicitud, bodegueroId);
            flash.addFlashAttribute("exitoo", msg("solicitud.mensaje.aprobada"));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            flash.addFlashAttribute("error", msg(exception.getMessage()));
        }

        return "redirect:/solicitud/pendientes?bodegueroId=" + bodegueroId;
    }

    @GetMapping("/rechazar/{idSolicitud}")
    public String formularioRechazo(@PathVariable Integer idSolicitud,
                                     @RequestParam Integer bodegueroId,
                                     Model model) {

        var solicitud = solicitudService.getSolicitud(idSolicitud);

        if (solicitud.isEmpty()) {
            return "redirect:/solicitud/pendientes?bodegueroId=" + bodegueroId;
        }

        model.addAttribute("solicitud", solicitud.get());
        model.addAttribute("bodegueroId", bodegueroId);

        return "solicitud/rechazar";
    }

    @PostMapping("/rechazar/{idSolicitud}")
    public String rechazar(@PathVariable Integer idSolicitud,
                            @RequestParam Integer bodegueroId,
                            @RequestParam String motivo,
                            RedirectAttributes flash) {

        try {
            solicitudService.rechazar(idSolicitud, bodegueroId, motivo);
            flash.addFlashAttribute("exitoo", msg("solicitud.mensaje.rechazada"));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            flash.addFlashAttribute("error", msg(exception.getMessage()));
        }

        return "redirect:/solicitud/pendientes?bodegueroId=" + bodegueroId;
    }

    @GetMapping("/consultar/{idSolicitud}")
    public String consultar(@PathVariable Integer idSolicitud, Model model) {

        var solicitud = solicitudService.getSolicitud(idSolicitud);

        if (solicitud.isEmpty()) {
            return "redirect:/solicitud/mis-solicitudes";
        }

        model.addAttribute("solicitud", solicitud.get());

        return "solicitud/consulta";
    }
}
