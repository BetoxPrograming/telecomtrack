package com.telecomtrack.controller;

import com.telecomtrack.domain.Herramienta;
import com.telecomtrack.service.HerramientaService;
import com.telecomtrack.service.UbicacionService;
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
    private final UbicacionService ubicacionService;
    private final MessageSource messageSource;

    public HerramientaController(HerramientaService herramientaService,
                                  UbicacionService ubicacionService,
                                  MessageSource messageSource) {
        this.herramientaService = herramientaService;
        this.ubicacionService = ubicacionService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("herramientas", herramientaService.getHerramientas());
        model.addAttribute("idiomaRuta", "/herramienta/listado");
        return "herramienta/listado";
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("herramientas", herramientaService.getHerramientas());
        model.addAttribute("idiomaRuta", "/herramienta/catalogo");
        return "herramienta/catalogo";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        var herramienta = new Herramienta();
        herramienta.setEstado(HerramientaService.ESTADO_DISPONIBLE);
        cargarFormulario(model, herramienta);
        return "herramienta/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid Herramienta herramienta,
            BindingResult bindingResult,
            @RequestParam(required = false) Integer ubicacionId,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        herramienta.setUbicacion(ubicacionId == null ? null : ubicacionService.getUbicacion(ubicacionId).orElse(null));

        var herramientaActual = obtenerHerramientaActual(herramienta.getIdHerramienta());

        if (herramienta.getIdHerramienta() != null && herramientaActual == null) {
            agregarMensaje(redirectAttributes, "herramienta.error.no-existe", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        if (herramientaService.codigoDuplicado(herramienta)) {
            bindingResult.rejectValue("codigo", "validacion.herramienta.codigo.duplicado");
        }

        if (bindingResult.hasErrors()) {
            cargarFormulario(model, herramienta);
            return "herramienta/formulario";
        }

        if (herramientaActual == null) {
            herramienta.setEstado(HerramientaService.ESTADO_DISPONIBLE);
            herramienta.setFechaRetornoEstimada(null);
            herramienta.setFechaBaja(null);
            herramienta.setJustificacionBaja(null);
        } else {
            herramienta.setEstado(herramientaActual.getEstado());
            herramienta.setFechaRetornoEstimada(herramientaActual.getFechaRetornoEstimada());
            herramienta.setFechaBaja(herramientaActual.getFechaBaja());
            herramienta.setJustificacionBaja(herramientaActual.getJustificacionBaja());
        }

        herramientaService.guardar(herramienta);

        var mensaje = herramientaActual == null
                ? "herramienta.mensaje.guardada"
                : "herramienta.mensaje.modificada";

        agregarMensaje(redirectAttributes, mensaje, "success", locale);
        return "redirect:/herramienta/listado";
    }

    @GetMapping("/modificar/{idHerramienta}")
    public String modificar(
            @PathVariable Integer idHerramienta,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        var herramienta = obtenerHerramientaActual(idHerramienta);

        if (herramienta == null) {
            agregarMensaje(redirectAttributes, "herramienta.error.no-existe", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        cargarFormulario(model, herramienta);
        return "herramienta/formulario";
    }

    @GetMapping("/consultar/{idHerramienta}")
    public String consultar(
            @PathVariable Integer idHerramienta,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        var herramienta = obtenerHerramientaActual(idHerramienta);

        if (herramienta == null) {
            agregarMensaje(redirectAttributes, "herramienta.error.no-existe", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        model.addAttribute("herramienta", herramienta);
        model.addAttribute("idiomaRuta", "/herramienta/consultar/" + idHerramienta);
        return "herramienta/detalle";
    }

    @GetMapping("/baja/{idHerramienta}")
    public String bajaFormulario(
            @PathVariable Integer idHerramienta,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        var herramienta = obtenerHerramientaActual(idHerramienta);

        if (herramienta == null) {
            agregarMensaje(redirectAttributes, "herramienta.error.no-existe", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        if (HerramientaService.ESTADO_BAJA.equals(herramienta.getEstado())) {
            agregarMensaje(redirectAttributes, "herramienta.error.baja.no-permitida", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        model.addAttribute("herramienta", herramienta);
        model.addAttribute("justificacionBaja", herramienta.getJustificacionBaja());
        model.addAttribute("idiomaRuta", "/herramienta/baja/" + idHerramienta);
        return "herramienta/baja";
    }

    @PostMapping("/baja")
    public String baja(
            @RequestParam Integer idHerramienta,
            @RequestParam(required = false) String justificacionBaja,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        var herramienta = obtenerHerramientaActual(idHerramienta);

        if (herramienta == null) {
            agregarMensaje(redirectAttributes, "herramienta.error.no-existe", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        if (justificacionBaja == null || justificacionBaja.isBlank()) {
            model.addAttribute("herramienta", herramienta);
            model.addAttribute("justificacionBaja", justificacionBaja);
            model.addAttribute("errorJustificacion", true);
            model.addAttribute("idiomaRuta", "/herramienta/baja/" + idHerramienta);
            return "herramienta/baja";
        }

        if (!herramientaService.darDeBaja(idHerramienta, justificacionBaja.trim())) {
            agregarMensaje(redirectAttributes, "herramienta.error.baja.no-permitida", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        agregarMensaje(redirectAttributes, "herramienta.mensaje.baja", "success", locale);
        return "redirect:/herramienta/listado";
    }

    @GetMapping("/mantenimiento/{idHerramienta}")
    public String mantenimientoFormulario(
            @PathVariable Integer idHerramienta,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        var herramienta = obtenerHerramientaActual(idHerramienta);

        if (herramienta == null) {
            agregarMensaje(redirectAttributes, "herramienta.error.no-existe", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        if (!HerramientaService.ESTADO_DISPONIBLE.equals(herramienta.getEstado())) {
            agregarMensaje(redirectAttributes, "herramienta.error.mantenimiento.no-permitido", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        model.addAttribute("herramienta", herramienta);
        model.addAttribute("idiomaRuta", "/herramienta/mantenimiento/" + idHerramienta);
        return "herramienta/mantenimiento";
    }

    @PostMapping("/mantenimiento")
    public String mantenimiento(
            Herramienta herramienta,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        var herramientaActual = obtenerHerramientaActual(herramienta.getIdHerramienta());

        if (herramientaActual == null) {
            agregarMensaje(redirectAttributes, "herramienta.error.no-existe", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        if (herramienta.getFechaRetornoEstimada() == null) {
            bindingResult.rejectValue("fechaRetornoEstimada", "validacion.herramienta.fechaRetornoEstimada.requerido");
        }

        if (bindingResult.hasErrors()) {
            herramienta.setCodigo(herramientaActual.getCodigo());
            herramienta.setNombre(herramientaActual.getNombre());
            herramienta.setCategoria(herramientaActual.getCategoria());
            herramienta.setDescripcion(herramientaActual.getDescripcion());
            herramienta.setEstado(herramientaActual.getEstado());
            herramienta.setFechaBaja(herramientaActual.getFechaBaja());
            herramienta.setJustificacionBaja(herramientaActual.getJustificacionBaja());
            model.addAttribute("idiomaRuta", "/herramienta/mantenimiento/" + herramienta.getIdHerramienta());
            return "herramienta/mantenimiento";
        }

        if (!herramientaService.enviarAMantenimiento(
                herramienta.getIdHerramienta(),
                herramienta.getFechaRetornoEstimada())) {
            agregarMensaje(redirectAttributes, "herramienta.error.mantenimiento.no-permitido", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        agregarMensaje(redirectAttributes, "herramienta.mensaje.mantenimiento", "success", locale);
        return "redirect:/herramienta/listado";
    }

    @PostMapping("/disponible")
    public String volverDisponible(
            @RequestParam Integer idHerramienta,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (!herramientaService.volverDisponible(idHerramienta)) {
            agregarMensaje(redirectAttributes, "herramienta.error.disponible.no-permitido", "danger", locale);
            return "redirect:/herramienta/listado";
        }

        agregarMensaje(redirectAttributes, "herramienta.mensaje.disponible", "success", locale);
        return "redirect:/herramienta/listado";
    }

    private Herramienta obtenerHerramientaActual(Integer idHerramienta) {
        if (idHerramienta == null) {
            return null;
        }

        return herramientaService.getHerramienta(idHerramienta).orElse(null);
    }

    private void cargarFormulario(Model model, Herramienta herramienta) {
        model.addAttribute("herramienta", herramienta);
        model.addAttribute("idiomaRuta", getRutaFormulario(herramienta));
        model.addAttribute("ubicaciones", ubicacionService.getUbicaciones());
    }

    private String getRutaFormulario(Herramienta herramienta) {
        if (herramienta.getIdHerramienta() == null) {
            return "/herramienta/nuevo";
        }

        return "/herramienta/modificar/" + herramienta.getIdHerramienta();
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
