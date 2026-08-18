package com.telecomtrack.dto;

import com.telecomtrack.domain.Proyecto;
import com.telecomtrack.domain.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteTecnicoFila {

    private Usuario tecnico;
    private String herramientaCodigo;
    private String herramientaNombre;
    private Proyecto proyecto;
    private LocalDate fechaAsignacion;
    private String estadoActual;
}
