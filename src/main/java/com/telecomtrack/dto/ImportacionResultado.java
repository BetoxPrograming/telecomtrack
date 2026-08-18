package com.telecomtrack.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportacionResultado {

    private int filasProcesadas;
    private int filasImportadas;
    private final List<String> errores = new ArrayList<>();

    public int getFilasProcesadas() {
        return filasProcesadas;
    }

    public int getFilasImportadas() {
        return filasImportadas;
    }

    public List<String> getErrores() {
        return errores;
    }

    public int getCantidadErrores() {
        return errores.size();
    }

    public boolean isTieneErrores() {
        return !errores.isEmpty();
    }

    public void registrarFilaProcesada() {
        filasProcesadas++;
    }

    public void registrarFilaImportada() {
        filasImportadas++;
    }

    public void agregarError(String error) {
        errores.add(error);
    }
}
