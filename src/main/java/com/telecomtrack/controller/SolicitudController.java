package com.telecomtrack.controller;

import com.telecomtrack.domain.Solicitud;
import com.telecomtrack.domain.Usuario;
import com.telecomtrack.service.HerramientaService;
import com.telecomtrack.service.MaterialService;
import com.telecomtrack.service.ProyectoService;
import com.telecomtrack.service.SolicitudService;
import com.telecomtrack.service.UbicacionService;
import com.telecomtrack.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/solicitud")
public class SolicitudController {

    private static final String ROL_ADMINISTRADOR = "Administrador";
    private static final String ROL_BODEGUERO = "Bodeguero";
    private static final String ROL_TECNICO = "Técnico";

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

    private Usuario getUsuarioAutenticado(Principal principal) {
        return usuarioService.getUsuarioPorCorreoActivo(principal.getName());
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) Integer ubicacionId,
                        Principal principal,
                        Model model) {

        model.addAttribute("tecnico", getUsuarioAutenticado(principal));
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
    public String guardar(@RequestParam Integer proyectoId,
                           @RequestParam Integer ubicacionId,
                           @RequestParam(required = false) List<Integer> herramientaIds,
                           @RequestParam(required = false) List<Integer> materialIds,
                           HttpServletRequest request,
                           Principal principal,
                           RedirectAttributes flash) {

        Usuario tecnico = getUsuarioAutenticado(principal);
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
            solicitudService.crear(
                    tecnico.getIdUsuario(),
                    proyectoId,
                    ubicacionId,
                    herramientaIds,
                    materialCantidades);

            flash.addFlashAttribute("exitoo", msg("solicitud.mensaje.creada"));
            return "redirect:/solicitud/mis-solicitudes";

        } catch (IllegalArgumentException | IllegalStateException exception) {
            flash.addFlashAttribute("error", msg(exception.getMessage()));
            return "redirect:/solicitud/nueva?ubicacionId=" + ubicacionId;
        }
    }

    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(Principal principal, Model model) {

        Usuario tecnico = getUsuarioAutenticado(principal);
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("solicitudes", solicitudService.getMisSolicitudes(tecnico.getIdUsuario()));

        return "solicitud/mis-solicitudes";
    }

    @GetMapping("/pendientes")
    public String pendientes(Model model) {

        model.addAttribute("pendientes", solicitudService.getPendientes());
        return "solicitud/pendientes";
    }

    @PostMapping("/aprobar/{idSolicitud}")
    public String aprobar(@PathVariable Integer idSolicitud,
                           Principal principal,
                           RedirectAttributes flash) {

        Usuario bodeguero = getUsuarioAutenticado(principal);

        try {
            solicitudService.aprobar(idSolicitud, bodeguero.getIdUsuario());
            flash.addFlashAttribute("exitoo", msg("solicitud.mensaje.aprobada"));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            flash.addFlashAttribute("error", msg(exception.getMessage()));
        }

        return "redirect:/solicitud/pendientes";
    }

    @GetMapping("/rechazar/{idSolicitud}")
    public String formularioRechazo(@PathVariable Integer idSolicitud, Model model) {

        var solicitud = solicitudService.getSolicitud(idSolicitud);

        if (solicitud.isEmpty()) {
            return "redirect:/solicitud/pendientes";
        }

        model.addAttribute("solicitud", solicitud.get());
        return "solicitud/rechazar";
    }

    @PostMapping("/rechazar/{idSolicitud}")
    public String rechazar(@PathVariable Integer idSolicitud,
                            @RequestParam String motivo,
                            Principal principal,
                            RedirectAttributes flash) {

        Usuario bodeguero = getUsuarioAutenticado(principal);

        try {
            solicitudService.rechazar(
                    idSolicitud,
                    bodeguero.getIdUsuario(),
                    motivo);
            flash.addFlashAttribute("exitoo", msg("solicitud.mensaje.rechazada"));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            flash.addFlashAttribute("error", msg(exception.getMessage()));
        }

        return "redirect:/solicitud/pendientes";
    }

    @GetMapping("/consultar/{idSolicitud}")
    public String consultar(@PathVariable Integer idSolicitud,
                             Principal principal,
                             Model model) {

        var solicitudOpt = solicitudService.getSolicitud(idSolicitud);

        if (solicitudOpt.isEmpty()) {
            return "redirect:/";
        }

        Usuario usuario = getUsuarioAutenticado(principal);
        Solicitud solicitud = solicitudOpt.get();

        boolean esAdministrador = ROL_ADMINISTRADOR.equals(usuario.getRol());
        boolean esBodeguero = ROL_BODEGUERO.equals(usuario.getRol());
        boolean esTecnicoPropietario = ROL_TECNICO.equals(usuario.getRol())
                && solicitud.getTecnico().getIdUsuario().equals(usuario.getIdUsuario());

        if (!esAdministrador && !esBodeguero && !esTecnicoPropietario) {
            return "redirect:/acceso_denegado";
        }

        model.addAttribute("solicitud", solicitud);
        return "solicitud/consulta";
    }
}
