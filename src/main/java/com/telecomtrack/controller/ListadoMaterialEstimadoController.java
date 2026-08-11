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
@RequestMapping("/listado-material-estimado")
public class ListadoMaterialEstimadoController {

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
    public String pendientes(@RequestParam(required = false) Integer supervisorId, Model model) {

        model.addAttribute("supervisores", usuarioService.getSupervisoresActivos());
        model.addAttribute("supervisorId", supervisorId);
        model.addAttribute("pendientes", listadoService.getPendientes());

        return "listadoMaterialEstimado/pendientes";
    }

    @GetMapping("/consultar/{idListado}")
    public String consultar(@PathVariable Integer idListado,
                             @RequestParam(required = false) Integer supervisorId,
                             Model model) {

        var listado = listadoService.getListado(idListado);

        if (listado.isEmpty()) {
            return "redirect:/listado-material-estimado/pendientes";
        }

        model.addAttribute("listado", listado.get());
        model.addAttribute("supervisorId", supervisorId);
        model.addAttribute("supervisores", usuarioService.getSupervisoresActivos());

        return "listadoMaterialEstimado/consulta";
    }

    @PostMapping("/decidir/{idListado}")
    public String decidir(@PathVariable Integer idListado,
                           @RequestParam Integer supervisorId,
                           @RequestParam String nuevoEstado,
                           @RequestParam(required = false) String comentario,
                           RedirectAttributes flash) {

        try {
            listadoService.decidir(idListado, supervisorId, nuevoEstado, comentario);
            flash.addFlashAttribute("exitoo", msg("listadoMaterialEstimado.mensaje.decidido"));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            flash.addFlashAttribute("error", msg(exception.getMessage()));
        }

        return "redirect:/listado-material-estimado/pendientes?supervisorId=" + supervisorId;
    }
}
