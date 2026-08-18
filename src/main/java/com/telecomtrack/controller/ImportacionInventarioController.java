package com.telecomtrack.controller;

import com.telecomtrack.service.ImportacionInventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Controller
@RequestMapping("/inventario/importar")
public class ImportacionInventarioController {

    private final ImportacionInventarioService importacionInventarioService;

    public ImportacionInventarioController(
            ImportacionInventarioService importacionInventarioService) {
        this.importacionInventarioService = importacionInventarioService;
    }

    @GetMapping
    public String formulario(Model model) {
        cargarModelo(model);
        return "inventario/importar";
    }

    @PostMapping("/procesar")
    public String procesar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) MultipartFile archivo,
            Model model,
            Locale locale) {

        var resultado = importacionInventarioService.importar(tipo, archivo, locale);

        model.addAttribute("resultado", resultado);
        model.addAttribute("tipoSeleccionado", tipo);
        cargarModelo(model);
        return "inventario/importar";
    }

    private void cargarModelo(Model model) {
        model.addAttribute("tipoHerramienta", ImportacionInventarioService.TIPO_HERRAMIENTA);
        model.addAttribute("tipoMaterial", ImportacionInventarioService.TIPO_MATERIAL);
        model.addAttribute("idiomaRuta", "/inventario/importar");
    }
}
