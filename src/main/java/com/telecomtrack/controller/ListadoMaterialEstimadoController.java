package com.telecomtrack.controller;

import com.telecomtrack.domain.ListadoMaterialEstimado;
import com.telecomtrack.domain.Usuario;
import com.telecomtrack.service.ListadoMaterialEstimadoService;
import com.telecomtrack.service.MaterialService;
import com.telecomtrack.service.ProyectoService;
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
@RequestMapping("/listado-material-estimado")
public class ListadoMaterialEstimadoController {

    private static final String ROL_ADMINISTRADOR = "Administrador";

    private final ListadoMaterialEstimadoService listadoService;
    private final ProyectoService proyectoService;
    private final MaterialService materialService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public ListadoMaterialEstimadoController(ListadoMaterialEstimadoService listadoService,
                                              ProyectoService proyectoService,
                                              MaterialService materialService,
                                              UsuarioService usuarioService,
                                              MessageSource messageSource) {
        this.listadoService = listadoService;
        this.proyectoService = proyectoService;
        this.materialService = materialService;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    private String msg(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }

    private Usuario getUsuarioAutenticado(Principal principal) {
        return usuarioService.getUsuarioPorCorreoActivo(principal.getName());
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam Integer proyectoId, Model model) {

        var proyecto = proyectoService.getProyecto(proyectoId);

        if (proyecto.isEmpty()) {
            return "redirect:/proyecto/listado";
        }

        model.addAttribute("proyecto", proyecto.get());
        model.addAttribute("materiales", materialService.listarTodos());

        return "listadoMaterialEstimado/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer proyectoId,
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
            listadoService.crear(proyectoId, materialCantidades);
            flash.addFlashAttribute("exitoo", msg("listadoMaterialEstimado.mensaje.creado"));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            flash.addFlashAttribute("error", msg(exception.getMessage()));
            return "redirect:/listado-material-estimado/nuevo?proyectoId=" + proyectoId;
        }

        return "redirect:/proyecto/consultar/" + proyectoId;
    }

    @GetMapping("/pendientes")
    public String pendientes(Principal principal, Model model) {

        Usuario supervisor = getUsuarioAutenticado(principal);
        model.addAttribute("supervisor", supervisor);

        if (ROL_ADMINISTRADOR.equals(supervisor.getRol())) {
            model.addAttribute("pendientes", listadoService.getPendientes());
        } else {
            model.addAttribute("pendientes",
                    listadoService.getPendientesPorSupervisor(supervisor.getIdUsuario()));
        }

        return "listadoMaterialEstimado/pendientes";
    }

    @GetMapping("/consultar/{idListado}")
    public String consultar(@PathVariable Integer idListado,
                             Principal principal,
                             Model model) {

        var listadoOpt = listadoService.getListado(idListado);

        if (listadoOpt.isEmpty()) {
            return "redirect:/listado-material-estimado/pendientes";
        }

        Usuario supervisor = getUsuarioAutenticado(principal);
        ListadoMaterialEstimado listado = listadoOpt.get();

        boolean esAdministrador = ROL_ADMINISTRADOR.equals(supervisor.getRol());
        boolean esSupervisorDelProyecto = listado.getProyecto().getSupervisor() != null
                && listado.getProyecto().getSupervisor().getIdUsuario()
                .equals(supervisor.getIdUsuario());

        if (!esAdministrador && !esSupervisorDelProyecto) {
            return "redirect:/acceso_denegado";
        }

        model.addAttribute("listado", listado);
        model.addAttribute("supervisor", supervisor);

        return "listadoMaterialEstimado/consulta";
    }

    @PostMapping("/decidir/{idListado}")
    public String decidir(@PathVariable Integer idListado,
                           @RequestParam String nuevoEstado,
                           @RequestParam(required = false) String comentario,
                           Principal principal,
                           RedirectAttributes flash) {

        Usuario supervisor = getUsuarioAutenticado(principal);

        try {
            listadoService.decidir(
                    idListado,
                    supervisor.getIdUsuario(),
                    nuevoEstado,
                    comentario);
            flash.addFlashAttribute("exitoo", msg("listadoMaterialEstimado.mensaje.decidido"));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            flash.addFlashAttribute("error", msg(exception.getMessage()));
        }

        return "redirect:/listado-material-estimado/pendientes";
    }
}
