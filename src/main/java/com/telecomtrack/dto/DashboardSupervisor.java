package com.telecomtrack.dto;

import com.telecomtrack.domain.AsignacionHerramienta;
import com.telecomtrack.domain.Material;
import com.telecomtrack.domain.Proyecto;
import com.telecomtrack.domain.Solicitud;
import com.telecomtrack.domain.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSupervisor {

    private Usuario supervisor;
    private List<Proyecto> misProyectos;
    private long herramientasDisponibles;
    private List<AsignacionHerramienta> elementosEnUso;
    private List<Material> materialesStockCritico;
    private List<Solicitud> actividadReciente;
}
