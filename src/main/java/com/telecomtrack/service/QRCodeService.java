package com.telecomtrack.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
public class QRCodeService {

    private static final int TAMANO = 300;

    public void escribirQr(String texto, OutputStream outputStream) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(texto, BarcodeFormat.QR_CODE, TAMANO, TAMANO);
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
        } catch (WriterException | IOException exception) {
            throw new IllegalStateException("qr.error.generacion", exception);
        }
    }
}
