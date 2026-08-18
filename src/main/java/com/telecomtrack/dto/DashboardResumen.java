package com.telecomtrack.dto;

import com.telecomtrack.domain.Material;
import com.telecomtrack.domain.Movimiento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResumen {

    private long herramientasDisponibles;
    private long herramientasEnUso;
    private List<Material> materialesStockCritico;
    private long solicitudesPendientes;
    private long proyectosActivos;
    private List<Movimiento> ultimosMovimientos;
}
