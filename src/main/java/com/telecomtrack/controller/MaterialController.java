package com.telecomtrack.controller;

import com.telecomtrack.domain.Material;
import com.telecomtrack.service.CategoriaService;
import com.telecomtrack.service.MaterialService;
import com.telecomtrack.service.MovimientoService;
import com.telecomtrack.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/materiales")
public class MaterialController {

    private final MaterialService materialService;
    private final MovimientoService movimientoService;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;
    private final MessageSource messageSource;

    public MaterialController(MaterialService materialService,
                               MovimientoService movimientoService,
                               CategoriaService categoriaService,
                               ProveedorService proveedorService,
                               MessageSource messageSource) {
        this.materialService = materialService;
        this.movimientoService = movimientoService;
        this.categoriaService = categoriaService;
        this.proveedorService = proveedorService;
        this.messageSource = messageSource;
    }

    private String msg(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("materiales", materialService.listarTodos());
        model.addAttribute("alertas", materialService.listarConStockBajo());
        return "materiales/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("material", new Material());
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        return "materiales/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return materialService.buscarPorId(id).map(m -> {
            model.addAttribute("material", m);
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("proveedores", proveedorService.listarTodos());
            return "materiales/formulario";
        }).orElseGet(() -> {
            flash.addFlashAttribute("error", msg("mat.msg.noEncontrado"));
            return "redirect:/materiales";
        });
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("material") Material material,
                          BindingResult result,
                          @RequestParam(required = false) Long categoriaId,
                          @RequestParam(required = false) Long proveedorId,
                          Model model, RedirectAttributes flash) {

        if (categoriaId != null) {
            categoriaService.buscarPorId(categoriaId).ifPresent(material::setCategoria);
        }
        if (proveedorId != null) {
            proveedorService.buscarPorId(proveedorId).ifPresent(material::setProveedor);
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("proveedores", proveedorService.listarTodos());
            return "materiales/formulario";
        }

        boolean esNuevo = material.getIdMaterial() == null;

        boolean duplicado = esNuevo
                ? materialService.existeCodigoUnico(material.getCodigoUnico())
                : materialService.existeCodigoUnicoExcluyendo(material.getCodigoUnico(), material.getIdMaterial());

        if (duplicado) {
            result.rejectValue("codigoUnico", "error.material", msg("mat.msg.codigoDuplicado"));
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("proveedores", proveedorService.listarTodos());
            return "materiales/formulario";
        }

        if (esNuevo) {
            material.setStockActual(0);
        } else {
            materialService.buscarPorId(material.getIdMaterial())
                    .ifPresent(existente -> material.setStockActual(existente.getStockActual()));
        }

        materialService.guardar(material);
        flash.addFlashAttribute("exitoo", msg(esNuevo ? "mat.msg.creado" : "mat.msg.actualizado"));
        return "redirect:/materiales";
    }

    @GetMapping("/{id}/entrada")
    public String formularioEntrada(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return materialService.buscarPorId(id).map(m -> {
            model.addAttribute("material", m);
            model.addAttribute("movimientos", movimientoService.listarPorMaterial(id));
            model.addAttribute("proveedores", proveedorService.listarTodos());
            return "materiales/entrada-stock";
        }).orElseGet(() -> {
            flash.addFlashAttribute("error", msg("mat.msg.noEncontrado"));
            return "redirect:/materiales";
        });
    }

    @PostMapping("/{id}/entrada")
    public String registrarEntrada(@PathVariable Long id,
                                    @RequestParam(required = false) Integer cantidad,
                                    @RequestParam(required = false) Long proveedorId,
                                    @RequestParam(required = false) String observacion,
                                    @RequestParam(required = false) String responsable,
                                    Model model, RedirectAttributes flash) {

        Optional<Material> matOpt = materialService.buscarPorId(id);
        if (matOpt.isEmpty()) {
            flash.addFlashAttribute("error", msg("mat.msg.noEncontrado"));
            return "redirect:/materiales";
        }

        boolean hasErrors = false;
        if (cantidad == null || cantidad < 1) {
            model.addAttribute("errorCantidad", msg("mat.msg.cantidadInvalida"));
            hasErrors = true;
        }
        if (responsable == null || responsable.isBlank()) {
            model.addAttribute("errorResponsable", msg("validacion.movimiento.responsable.requerido"));
            hasErrors = true;
        }
        if (proveedorId == null) {
            model.addAttribute("errorProveedor", msg("validacion.movimiento.proveedor.requerido"));
            hasErrors = true;
        }

        if (hasErrors) {
            model.addAttribute("material", matOpt.get());
            model.addAttribute("movimientos", movimientoService.listarPorMaterial(id));
            model.addAttribute("proveedores", proveedorService.listarTodos());
            model.addAttribute("cantidad", cantidad);
            model.addAttribute("responsable", responsable);
            model.addAttribute("observacion", observacion);
            model.addAttribute("proveedorId", proveedorId);
            return "materiales/entrada-stock";
        }

        movimientoService.registrarEntrada(id, cantidad, observacion, responsable, proveedorId);
        flash.addFlashAttribute("exitoo", msg("mat.msg.entradaRegistrada", cantidad));
        return "redirect:/materiales";
    }
}
