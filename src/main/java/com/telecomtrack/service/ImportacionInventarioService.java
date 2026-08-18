package com.telecomtrack.service;

import com.telecomtrack.domain.Categoria;
import com.telecomtrack.domain.Herramienta;
import com.telecomtrack.domain.Material;
import com.telecomtrack.domain.Proveedor;
import com.telecomtrack.domain.Ubicacion;
import com.telecomtrack.dto.ImportacionResultado;
import com.telecomtrack.repository.CategoriaRepository;
import com.telecomtrack.repository.HerramientaRepository;
import com.telecomtrack.repository.MaterialRepository;
import com.telecomtrack.repository.ProveedorRepository;
import com.telecomtrack.repository.UbicacionRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ImportacionInventarioService {

    public static final String TIPO_HERRAMIENTA = "HERRAMIENTA";
    public static final String TIPO_MATERIAL = "MATERIAL";

    private static final List<String> ENCABEZADO_HERRAMIENTA = List.of(
            "codigo", "nombre", "categoria", "descripcion", "ubicacion");

    private static final List<String> ENCABEZADO_MATERIAL = List.of(
            "codigo", "nombre", "descripcion", "unidad_medida", "stock_actual",
            "stock_minimo", "valor_unitario", "categoria", "proveedor", "ubicacion");

    private final HerramientaRepository herramientaRepository;
    private final MaterialRepository materialRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final UbicacionRepository ubicacionRepository;
    private final MessageSource messageSource;

    public ImportacionInventarioService(
            HerramientaRepository herramientaRepository,
            MaterialRepository materialRepository,
            CategoriaRepository categoriaRepository,
            ProveedorRepository proveedorRepository,
            UbicacionRepository ubicacionRepository,
            MessageSource messageSource) {
        this.herramientaRepository = herramientaRepository;
        this.materialRepository = materialRepository;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.messageSource = messageSource;
    }

    public ImportacionResultado importar(
            String tipo,
            MultipartFile archivo,
            Locale locale) {

        ImportacionResultado resultado = new ImportacionResultado();

        if (!TIPO_HERRAMIENTA.equals(tipo) && !TIPO_MATERIAL.equals(tipo)) {
            resultado.agregarError(msg("importacion.error.tipo", locale));
            return resultado;
        }

        if (archivo == null || archivo.isEmpty()) {
            resultado.agregarError(msg("importacion.error.archivoVacio", locale));
            return resultado;
        }

        String nombreArchivo = archivo.getOriginalFilename();
        if (nombreArchivo == null || !nombreArchivo.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            resultado.agregarError(msg("importacion.error.formato", locale));
            return resultado;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                archivo.getInputStream(), StandardCharsets.UTF_8))) {

            String encabezadoTexto = reader.readLine();
            if (encabezadoTexto == null || encabezadoTexto.isBlank()) {
                resultado.agregarError(msg("importacion.error.encabezadoVacio", locale));
                return resultado;
            }

            encabezadoTexto = quitarBom(encabezadoTexto);
            char separador = detectarSeparador(encabezadoTexto);
            List<String> encabezado = normalizarEncabezado(parsearLinea(encabezadoTexto, separador));
            List<String> esperado = TIPO_HERRAMIENTA.equals(tipo)
                    ? ENCABEZADO_HERRAMIENTA
                    : ENCABEZADO_MATERIAL;

            if (!encabezado.equals(esperado)) {
                resultado.agregarError(msg(
                        "importacion.error.encabezado",
                        locale,
                        String.join(String.valueOf(separador), esperado)));
                return resultado;
            }

            String linea;
            int numeroFila = 1;

            while ((linea = reader.readLine()) != null) {
                numeroFila++;

                if (linea.isBlank()) {
                    continue;
                }

                resultado.registrarFilaProcesada();

                try {
                    List<String> valores = parsearLinea(linea, separador);
                    if (valores.size() != esperado.size()) {
                        throw new IllegalArgumentException(msg(
                                "importacion.error.columnas",
                                locale,
                                esperado.size(),
                                valores.size()));
                    }

                    if (TIPO_HERRAMIENTA.equals(tipo)) {
                        importarHerramienta(valores, locale);
                    } else {
                        importarMaterial(valores, locale);
                    }

                    resultado.registrarFilaImportada();

                } catch (IllegalArgumentException ex) {
                    resultado.agregarError(msg(
                            "importacion.error.fila",
                            locale,
                            numeroFila,
                            ex.getMessage()));
                } catch (Exception ex) {
                    resultado.agregarError(msg(
                            "importacion.error.fila",
                            locale,
                            numeroFila,
                            msg("importacion.error.inesperado", locale)));
                }
            }

        } catch (IOException ex) {
            resultado.agregarError(msg("importacion.error.lectura", locale));
        }

        return resultado;
    }

    private void importarHerramienta(List<String> valores, Locale locale) {
        String codigo = requerido(valores.get(0), "codigo", locale);
        String nombre = requerido(valores.get(1), "nombre", locale);
        String categoria = requerido(valores.get(2), "categoria", locale);
        String descripcion = limpiar(valores.get(3));
        String ubicacionNombre = requerido(valores.get(4), "ubicacion", locale);

        validarLongitud(codigo, "codigo", 50, locale);
        validarLongitud(nombre, "nombre", 100, locale);
        validarLongitud(categoria, "categoria", 100, locale);
        validarLongitud(descripcion, "descripcion", 255, locale);

        if (herramientaRepository.findByCodigo(codigo).isPresent()) {
            throw new IllegalArgumentException(msg(
                    "importacion.error.duplicado", locale, codigo));
        }

        Ubicacion ubicacion = ubicacionRepository
                .findFirstByNombreIgnoreCase(ubicacionNombre)
                .orElseThrow(() -> new IllegalArgumentException(msg(
                        "importacion.error.noEncontrado",
                        locale,
                        msg("importacion.entidad.ubicacion", locale),
                        ubicacionNombre)));

        Herramienta herramienta = new Herramienta();
        herramienta.setCodigo(codigo);
        herramienta.setNombre(nombre);
        herramienta.setCategoria(categoria);
        herramienta.setDescripcion(descripcion.isBlank() ? null : descripcion);
        herramienta.setEstado(HerramientaService.ESTADO_DISPONIBLE);
        herramienta.setUbicacion(ubicacion);
        herramienta.setFechaRetornoEstimada(null);
        herramienta.setFechaBaja(null);
        herramienta.setJustificacionBaja(null);

        herramientaRepository.save(herramienta);
    }

    private void importarMaterial(List<String> valores, Locale locale) {
        String codigo = requerido(valores.get(0), "codigo", locale);
        String nombre = requerido(valores.get(1), "nombre", locale);
        String descripcion = limpiar(valores.get(2));
        String unidadMedida = requerido(valores.get(3), "unidad_medida", locale);
        Integer stockActual = enteroNoNegativo(valores.get(4), "stock_actual", locale);
        Integer stockMinimo = enteroNoNegativo(valores.get(5), "stock_minimo", locale);
        BigDecimal valorUnitario = decimalNoNegativo(valores.get(6), "valor_unitario", locale);
        String categoriaNombre = requerido(valores.get(7), "categoria", locale);
        String proveedorNombre = requerido(valores.get(8), "proveedor", locale);
        String ubicacionNombre = requerido(valores.get(9), "ubicacion", locale);

        validarLongitud(codigo, "codigo", 50, locale);
        validarLongitud(nombre, "nombre", 100, locale);
        validarLongitud(descripcion, "descripcion", 255, locale);
        validarLongitud(unidadMedida, "unidad_medida", 30, locale);

        if (materialRepository.existsByCodigoUnico(codigo)) {
            throw new IllegalArgumentException(msg(
                    "importacion.error.duplicado", locale, codigo));
        }

        Categoria categoria = categoriaRepository
                .findFirstByNombreIgnoreCase(categoriaNombre)
                .orElseThrow(() -> new IllegalArgumentException(msg(
                        "importacion.error.noEncontrado",
                        locale,
                        msg("importacion.entidad.categoria", locale),
                        categoriaNombre)));

        Proveedor proveedor = proveedorRepository
                .findFirstByNombreIgnoreCase(proveedorNombre)
                .orElseThrow(() -> new IllegalArgumentException(msg(
                        "importacion.error.noEncontrado",
                        locale,
                        msg("importacion.entidad.proveedor", locale),
                        proveedorNombre)));

        Ubicacion ubicacion = ubicacionRepository
                .findFirstByNombreIgnoreCase(ubicacionNombre)
                .orElseThrow(() -> new IllegalArgumentException(msg(
                        "importacion.error.noEncontrado",
                        locale,
                        msg("importacion.entidad.ubicacion", locale),
                        ubicacionNombre)));

        Material material = new Material();
        material.setCodigoUnico(codigo);
        material.setNombre(nombre);
        material.setDescripcion(descripcion.isBlank() ? null : descripcion);
        material.setUnidadMedida(unidadMedida);
        material.setStockActual(stockActual);
        material.setStockMinimo(stockMinimo);
        material.setValorUnitario(valorUnitario);
        material.setCategoria(categoria);
        material.setProveedor(proveedor);
        material.setUbicacion(ubicacion);

        materialRepository.save(material);
    }

    private String requerido(String valor, String campo, Locale locale) {
        String limpio = limpiar(valor);
        if (limpio.isBlank()) {
            throw new IllegalArgumentException(msg(
                    "importacion.error.requerido", locale, campo));
        }
        return limpio;
    }

    private void validarLongitud(String valor, String campo, int maximo, Locale locale) {
        if (valor != null && valor.length() > maximo) {
            throw new IllegalArgumentException(msg(
                    "importacion.error.longitud", locale, campo, maximo));
        }
    }

    private Integer enteroNoNegativo(String valor, String campo, Locale locale) {
        try {
            int numero = Integer.parseInt(limpiar(valor));
            if (numero < 0) {
                throw new NumberFormatException();
            }
            return numero;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(msg(
                    "importacion.error.entero", locale, campo));
        }
    }

    private BigDecimal decimalNoNegativo(String valor, String campo, Locale locale) {
        try {
            BigDecimal numero = new BigDecimal(limpiar(valor));
            if (numero.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException();
            }
            return numero;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(msg(
                    "importacion.error.decimal", locale, campo));
        }
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String quitarBom(String texto) {
        if (!texto.isEmpty() && texto.charAt(0) == '\uFEFF') {
            return texto.substring(1);
        }
        return texto;
    }

    private List<String> normalizarEncabezado(List<String> encabezado) {
        List<String> normalizado = new ArrayList<>();
        for (String valor : encabezado) {
            normalizado.add(limpiar(valor).toLowerCase(Locale.ROOT));
        }
        return normalizado;
    }

    private char detectarSeparador(String linea) {
        int comas = contarSeparador(linea, ',');
        int puntoComas = contarSeparador(linea, ';');
        return puntoComas > comas ? ';' : ',';
    }

    private int contarSeparador(String linea, char separador) {
        boolean entreComillas = false;
        int cantidad = 0;

        for (int i = 0; i < linea.length(); i++) {
            char actual = linea.charAt(i);

            if (actual == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (actual == separador && !entreComillas) {
                cantidad++;
            }
        }

        return cantidad;
    }

    private List<String> parsearLinea(String linea, char separador) {
        List<String> valores = new ArrayList<>();
        StringBuilder valorActual = new StringBuilder();
        boolean entreComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char actual = linea.charAt(i);

            if (actual == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    valorActual.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (actual == separador && !entreComillas) {
                valores.add(valorActual.toString().trim());
                valorActual.setLength(0);
            } else {
                valorActual.append(actual);
            }
        }

        valores.add(valorActual.toString().trim());
        return valores;
    }

    private String msg(String codigo, Locale locale, Object... argumentos) {
        return messageSource.getMessage(codigo, argumentos, locale);
    }
}
