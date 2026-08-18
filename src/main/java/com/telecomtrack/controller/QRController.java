package com.telecomtrack.controller;

import com.telecomtrack.domain.AsignacionHerramienta;
import com.telecomtrack.domain.DevolucionHerramienta;
import com.telecomtrack.service.DevolucionHerramientaService;
import com.telecomtrack.service.HerramientaService;
import com.telecomtrack.service.QRCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/qr")
public class QRController {

    private final QRCodeService qrCodeService;
    private final HerramientaService herramientaService;
    private final DevolucionHerramientaService devolucionHerramientaService;

    public QRController(
            QRCodeService qrCodeService,
            HerramientaService herramientaService,
            DevolucionHerramientaService devolucionHerramientaService) {
        this.qrCodeService = qrCodeService;
        this.herramientaService = herramientaService;
        this.devolucionHerramientaService = devolucionHerramientaService;
    }

    @GetMapping("/herramienta/{idHerramienta}")
    public void generarQr(
            @PathVariable Integer idHerramienta,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        var herramienta = herramientaService.getHerramienta(idHerramienta).orElse(null);

        if (herramienta == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String urlActual = request.getRequestURL().toString();
        String sufijo = "/qr/herramienta/" + idHerramienta;
        String urlBase = urlActual.substring(0, urlActual.length() - sufijo.length());
        String urlFicha = urlBase + "/qr/herramienta/" + idHerramienta + "/ficha";

        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-store");
        qrCodeService.escribirQr(urlFicha, response.getOutputStream());
    }

    @GetMapping("/herramienta/{idHerramienta}/ficha")
    public String ficha(@PathVariable Integer idHerramienta, Model model) {
        var herramienta = herramientaService.getHerramienta(idHerramienta).orElse(null);

        if (herramienta == null) {
            return "redirect:/herramienta/catalogo";
        }

        AsignacionHerramienta ultimaAsignacion =
                devolucionHerramientaService.getUltimaAsignacionHerramienta(idHerramienta);
        List<DevolucionHerramienta> historialReciente =
                devolucionHerramientaService.getHistorialRecienteHerramienta(idHerramienta);

        model.addAttribute("herramienta", herramienta);
        model.addAttribute("ultimaAsignacion", ultimaAsignacion);
        model.addAttribute("historialReciente", historialReciente);
        model.addAttribute("idiomaRuta", "/qr/herramienta/" + idHerramienta + "/ficha");
        return "herramienta/ficha-qr";
    }
}
