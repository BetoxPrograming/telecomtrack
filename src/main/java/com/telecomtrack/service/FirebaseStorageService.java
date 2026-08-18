package com.telecomtrack.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private final ObjectProvider<Storage> storageProvider;

    @Value("${firebase.bucket.name:}")
    private String bucketName;

    @Value("${firebase.storage.path:telecomtrack}")
    private String storagePath;

    public FirebaseStorageService(ObjectProvider<Storage> storageProvider) {
        this.storageProvider = storageProvider;
    }

    public String uploadImage(
            MultipartFile archivo,
            String carpeta,
            String nombreBase) throws IOException {

        Storage storage = storageProvider.getIfAvailable();

        if (storage == null || bucketName == null || bucketName.isBlank()) {
            throw new IllegalStateException("imagen.error.configuracion");
        }

        String extension = obtenerExtension(archivo.getOriginalFilename());
        String token = UUID.randomUUID().toString();
        String nombreArchivo = nombreBase + "-" + UUID.randomUUID() + extension;
        String ruta = storagePath + "/" + carpeta + "/" + nombreArchivo;

        Map<String, String> metadata = new HashMap<>();
        metadata.put("firebaseStorageDownloadTokens", token);

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, ruta))
                .setContentType(archivo.getContentType())
                .setMetadata(metadata)
                .build();

        storage.create(blobInfo, archivo.getBytes());

        String rutaCodificada = URLEncoder
                .encode(ruta, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return "https://firebasestorage.googleapis.com/v0/b/"
                + bucketName
                + "/o/"
                + rutaCodificada
                + "?alt=media&token="
                + token;
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return ".png";
        }

        return nombreOriginal
                .substring(nombreOriginal.lastIndexOf('.'))
                .toLowerCase();
    }
}
