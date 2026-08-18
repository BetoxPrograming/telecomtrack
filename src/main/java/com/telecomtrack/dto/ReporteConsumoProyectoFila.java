package com.telecomtrack.dto;

import com.telecomtrack.domain.Proyecto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteConsumoProyectoFila {

    private Proyecto proyecto;
    private int cantidadEstimada;
    private int cantidadReal;

    public int getDiferencia() {
        return cantidadReal - cantidadEstimada;
    }
}
