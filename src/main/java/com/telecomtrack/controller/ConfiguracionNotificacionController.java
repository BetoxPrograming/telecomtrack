package com.telecomtrack.controller;

import com.telecomtrack.domain.ConfiguracionNotificacion;
import com.telecomtrack.service.ConfiguracionNotificacionService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequestMapping("/notificacion")
public class ConfiguracionNotificacionController {

    private final ConfiguracionNotificacionService configuracionNotificacionService;
    private final MessageSource messageSource;

    public ConfiguracionNotificacionController(
            ConfiguracionNotificacionService configuracionNotificacionService,
            MessageSource messageSource) {
        this.configuracionNotificacionService = configuracionNotificacionService;
        this.messageSource = messageSource;
    }

    @GetMapping("/configuracion")
    public String configuracion(Model model) {
        model.addAttribute("configuracion",
                configuracionNotificacionService.getConfiguracion());
        model.addAttribute("idiomaRuta", "/notificacion/configuracion");
        return "notificacion/configuracion";
    }

    @PostMapping("/guardar")
    public String guardar(
            ConfiguracionNotificacion configuracion,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        configuracionNotificacionService.save(configuracion);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage(
                        "notificacion.mensaje.guardado",
                        null,
                        locale));
        return "redirect:/notificacion/configuracion";
    }
}
