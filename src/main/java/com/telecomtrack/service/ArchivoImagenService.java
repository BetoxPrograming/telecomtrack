package com.telecomtrack.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ArchivoImagenService {

    private final FirebaseStorageService firebaseStorageService;

    public ArchivoImagenService(FirebaseStorageService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    public String guardarImagen(MultipartFile archivo, String prefijo) {
        try {
            return firebaseStorageService.uploadImage(
                    archivo,
                    "devoluciones",
                    prefijo);
        } catch (IOException exception) {
            throw new IllegalStateException("imagen.error.guardado", exception);
        }
    }
}
