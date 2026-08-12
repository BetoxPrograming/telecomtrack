package com.telecomtrack.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ArchivoImagenService {

    private static final String DIRECTORIO_IMAGENES = "uploads";

    public String guardarImagen(MultipartFile archivo, String prefijo) {
        try {
            Path directorio = Paths.get(DIRECTORIO_IMAGENES);

            if (!Files.exists(directorio)) {
                Files.createDirectories(directorio);
            }

            String nombreArchivo = prefijo + "-" + UUID.randomUUID() + obtenerExtension(archivo.getOriginalFilename());
            Path destino = directorio.resolve(nombreArchivo);

            Files.write(destino, archivo.getBytes());
            return "/" + DIRECTORIO_IMAGENES + "/" + nombreArchivo;
        } catch (IOException exception) {
            throw new IllegalStateException("imagen.error.guardado", exception);
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return ".png";
        }

        return nombreOriginal.substring(nombreOriginal.lastIndexOf('.')).toLowerCase();
    }
}
