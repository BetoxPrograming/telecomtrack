package com.telecomtrack.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Service
public class ReporteExportService {

    public byte[] exportarExcel(String hoja, List<String> encabezados, List<List<String>> filas) {

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet(hoja);

            CellStyle estiloEncabezado = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font fuenteEncabezado = workbook.createFont();
            fuenteEncabezado.setBold(true);
            estiloEncabezado.setFont(fuenteEncabezado);

            Row filaEncabezado = sheet.createRow(0);
            for (int columna = 0; columna < encabezados.size(); columna++) {
                Cell celda = filaEncabezado.createCell(columna);
                celda.setCellValue(encabezados.get(columna));
                celda.setCellStyle(estiloEncabezado);
            }

            for (int indiceFila = 0; indiceFila < filas.size(); indiceFila++) {
                Row fila = sheet.createRow(indiceFila + 1);
                List<String> valores = filas.get(indiceFila);
                for (int columna = 0; columna < valores.size(); columna++) {
                    fila.createCell(columna).setCellValue(valores.get(columna));
                }
            }

            for (int columna = 0; columna < encabezados.size(); columna++) {
                sheet.autoSizeColumn(columna);
            }

            workbook.write(salida);
            return salida.toByteArray();

        } catch (IOException excepcion) {
            throw new UncheckedIOException(excepcion);
        }
    }

    public byte[] exportarPdf(String titulo, List<String> encabezados, List<List<String>> filas) {

        Document documento = new Document(PageSize.A4.rotate(), 24, 24, 36, 36);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            documento.add(new Paragraph(titulo, fuenteTitulo));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(encabezados.size());
            tabla.setWidthPercentage(100);

            Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            for (String encabezado : encabezados) {
                PdfPCell celda = new PdfPCell(new Paragraph(encabezado, fuenteEncabezado));
                celda.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                tabla.addCell(celda);
            }

            Font fuenteCelda = FontFactory.getFont(FontFactory.HELVETICA, 9);
            for (List<String> fila : filas) {
                for (String valor : fila) {
                    tabla.addCell(new Paragraph(valor != null ? valor : "", fuenteCelda));
                }
            }

            documento.add(tabla);

        } catch (com.lowagie.text.DocumentException excepcion) {
            throw new IllegalStateException("No se pudo generar el PDF", excepcion);
        } finally {
            documento.close();
        }

        return salida.toByteArray();
    }
}
