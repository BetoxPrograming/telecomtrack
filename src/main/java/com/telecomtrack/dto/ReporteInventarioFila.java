package com.telecomtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteInventarioFila {

    private String tipoElemento;
    private String codigo;
    private String nombre;
    private String categoria;
    private String estado;
    private String ubicacion;
    private LocalDate fechaReferencia;
    private int cantidad;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
}
