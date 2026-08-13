package com.telecomtrack.controller;

import com.telecomtrack.dto.ReporteConsumoProyectoFila;
import com.telecomtrack.dto.ReporteInventarioFila;
import com.telecomtrack.dto.ReporteTecnicoFila;
import com.telecomtrack.service.ProyectoService;
import com.telecomtrack.service.ReporteExportService;
import com.telecomtrack.service.ReporteService;
import com.telecomtrack.service.UbicacionService;
import com.telecomtrack.service.UsuarioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Restriccion por rol (Tecnico no deberia acceder aqui) queda pendiente de Spring Security (Issue #14);
// por ahora estas rutas quedan abiertas igual que el resto del proyecto.
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ReporteService reporteService;
    private final ReporteExportService reporteExportService;
    private final ProyectoService proyectoService;
    private final UsuarioService usuarioService;
    private final UbicacionService ubicacionService;

    public ReporteController(ReporteService reporteService,
                              ReporteExportService reporteExportService,
                              ProyectoService proyectoService,
                              UsuarioService usuarioService,
                              UbicacionService ubicacionService) {
        this.reporteService = reporteService;
        this.reporteExportService = reporteExportService;
        this.proyectoService = proyectoService;
        this.usuarioService = usuarioService;
        this.ubicacionService = ubicacionService;
    }

    @GetMapping
    public String menu() {
        return "reporte/menu";
    }

    // ---------- Consumo por proyecto ----------

    @GetMapping("/consumo-proyecto")
    public String consumoProyecto(@RequestParam(required = false) Integer proyectoId, Model model) {

        model.addAttribute("proyectos", proyectoService.getProyectos());
        model.addAttribute("proyectoId", proyectoId);
        model.addAttribute("filas", reporteService.getConsumoPorProyecto(proyectoId));

        return "reporte/consumo-proyecto";
    }

    @GetMapping("/consumo-proyecto/exportar/excel")
    public ResponseEntity<byte[]> consumoProyectoExcel(@RequestParam(required = false) Integer proyectoId) {

        List<ReporteConsumoProyectoFila> filas = reporteService.getConsumoPorProyecto(proyectoId);

        List<String> encabezados = List.of("Proyecto", "Cantidad estimada", "Consumo real", "Diferencia");

        List<List<String>> datos = new ArrayList<>();
        for (ReporteConsumoProyectoFila fila : filas) {
            datos.add(List.of(
                    fila.getProyecto().getNombre(),
                    String.valueOf(fila.getCantidadEstimada()),
                    String.valueOf(fila.getCantidadReal()),
                    String.valueOf(fila.getDiferencia())
            ));
        }

        return archivoExcel("consumo-por-proyecto", encabezados, datos);
    }

    @GetMapping("/consumo-proyecto/exportar/pdf")
    public ResponseEntity<byte[]> consumoProyectoPdf(@RequestParam(required = false) Integer proyectoId) {

        List<ReporteConsumoProyectoFila> filas = reporteService.getConsumoPorProyecto(proyectoId);

        List<String> encabezados = List.of("Proyecto", "Cantidad estimada", "Consumo real", "Diferencia");

        List<List<String>> datos = new ArrayList<>();
        for (ReporteConsumoProyectoFila fila : filas) {
            datos.add(List.of(
                    fila.getProyecto().getNombre(),
                    String.valueOf(fila.getCantidadEstimada()),
                    String.valueOf(fila.getCantidadReal()),
                    String.valueOf(fila.getDiferencia())
            ));
        }

        return archivoPdf("Consumo por proyecto", "consumo-por-proyecto", encabezados, datos);
    }

    // ---------- Inventario general ----------

    @GetMapping("/inventario")
    public String inventario(@RequestParam(required = false) String tipo,
                              @RequestParam(required = false) String categoria,
                              @RequestParam(required = false) String estado,
                              @RequestParam(required = false) String ubicacion,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                              Model model) {

        model.addAttribute("ubicaciones", ubicacionService.getUbicaciones());
        model.addAttribute("tipo", tipo);
        model.addAttribute("categoria", categoria);
        model.addAttribute("estado", estado);
        model.addAttribute("ubicacion", ubicacion);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("filas", reporteService.getInventarioGeneral(tipo, categoria, estado, ubicacion, desde, hasta));

        return "reporte/inventario";
    }

    @GetMapping("/inventario/exportar/excel")
    public ResponseEntity<byte[]> inventarioExcel(@RequestParam(required = false) String tipo,
                                                    @RequestParam(required = false) String categoria,
                                                    @RequestParam(required = false) String estado,
                                                    @RequestParam(required = false) String ubicacion,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<ReporteInventarioFila> filas = reporteService.getInventarioGeneral(tipo, categoria, estado, ubicacion, desde, hasta);
        List<String> encabezados = List.of("Tipo", "Codigo", "Nombre", "Categoria", "Estado", "Ubicacion",
                "Fecha", "Cantidad", "Valor unitario", "Valor total");

        return archivoExcel("inventario-general", encabezados, filasInventarioComoTexto(filas));
    }

    @GetMapping("/inventario/exportar/pdf")
    public ResponseEntity<byte[]> inventarioPdf(@RequestParam(required = false) String tipo,
                                                  @RequestParam(required = false) String categoria,
                                                  @RequestParam(required = false) String estado,
                                                  @RequestParam(required = false) String ubicacion,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<ReporteInventarioFila> filas = reporteService.getInventarioGeneral(tipo, categoria, estado, ubicacion, desde, hasta);
        List<String> encabezados = List.of("Tipo", "Codigo", "Nombre", "Categoria", "Estado", "Ubicacion",
                "Fecha", "Cantidad", "Valor unitario", "Valor total");

        return archivoPdf("Reporte general de inventario", "inventario-general", encabezados, filasInventarioComoTexto(filas));
    }

    private List<List<String>> filasInventarioComoTexto(List<ReporteInventarioFila> filas) {
        List<List<String>> datos = new ArrayList<>();
        for (ReporteInventarioFila fila : filas) {
            datos.add(List.of(
                    fila.getTipoElemento(),
                    fila.getCodigo() != null ? fila.getCodigo() : "",
                    fila.getNombre() != null ? fila.getNombre() : "",
                    fila.getCategoria() != null ? fila.getCategoria() : "",
                    fila.getEstado() != null ? fila.getEstado() : "",
                    fila.getUbicacion() != null ? fila.getUbicacion() : "",
                    fila.getFechaReferencia() != null ? fila.getFechaReferencia().format(FORMATO_FECHA) : "",
                    String.valueOf(fila.getCantidad()),
                    fila.getValorUnitario() != null ? fila.getValorUnitario().toPlainString() : "0",
                    fila.getValorTotal() != null ? fila.getValorTotal().toPlainString() : "0"
            ));
        }
        return datos;
    }

    // ---------- Activos por técnico ----------

    @GetMapping("/tecnico")
    public String tecnico(@RequestParam(required = false) Integer tecnicoId,
                           @RequestParam(required = false) Integer proyectoId,
                           @RequestParam(required = false) String estado,
                           Model model) {

        model.addAttribute("tecnicos", usuarioService.getTecnicosActivos());
        model.addAttribute("proyectos", proyectoService.getProyectos());
        model.addAttribute("tecnicoId", tecnicoId);
        model.addAttribute("proyectoId", proyectoId);
        model.addAttribute("estado", estado);
        model.addAttribute("filas", reporteService.getActivosPorTecnico(tecnicoId, proyectoId, estado));

        return "reporte/tecnico";
    }

    @GetMapping("/tecnico/exportar/excel")
    public ResponseEntity<byte[]> tecnicoExcel(@RequestParam(required = false) Integer tecnicoId,
                                                 @RequestParam(required = false) Integer proyectoId,
                                                 @RequestParam(required = false) String estado) {

        List<ReporteTecnicoFila> filas = reporteService.getActivosPorTecnico(tecnicoId, proyectoId, estado);
        List<String> encabezados = List.of("Tecnico", "Herramienta", "Proyecto", "Fecha de asignacion", "Estado actual");

        return archivoExcel("activos-por-tecnico", encabezados, filasTecnicoComoTexto(filas));
    }

    @GetMapping("/tecnico/exportar/pdf")
    public ResponseEntity<byte[]> tecnicoPdf(@RequestParam(required = false) Integer tecnicoId,
                                               @RequestParam(required = false) Integer proyectoId,
                                               @RequestParam(required = false) String estado) {

        List<ReporteTecnicoFila> filas = reporteService.getActivosPorTecnico(tecnicoId, proyectoId, estado);
        List<String> encabezados = List.of("Tecnico", "Herramienta", "Proyecto", "Fecha de asignacion", "Estado actual");

        return archivoPdf("Activos por tecnico", "activos-por-tecnico", encabezados, filasTecnicoComoTexto(filas));
    }

    private List<List<String>> filasTecnicoComoTexto(List<ReporteTecnicoFila> filas) {
        List<List<String>> datos = new ArrayList<>();
        for (ReporteTecnicoFila fila : filas) {
            datos.add(List.of(
                    fila.getTecnico().getNombre() + " " + fila.getTecnico().getApellido(),
                    fila.getHerramientaCodigo() + " - " + fila.getHerramientaNombre(),
                    fila.getProyecto().getNombre(),
                    fila.getFechaAsignacion() != null ? fila.getFechaAsignacion().format(FORMATO_FECHA) : "",
                    fila.getEstadoActual()
            ));
        }
        return datos;
    }

    // ---------- Helpers de descarga ----------

    private ResponseEntity<byte[]> archivoExcel(String nombreArchivo, List<String> encabezados, List<List<String>> datos) {

        byte[] contenido = reporteExportService.exportarExcel(nombreArchivo, encabezados, datos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + ".xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(contenido);
    }

    private ResponseEntity<byte[]> archivoPdf(String titulo, String nombreArchivo, List<String> encabezados, List<List<String>> datos) {

        byte[] contenido = reporteExportService.exportarPdf(titulo, encabezados, datos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(contenido);
    }
}
