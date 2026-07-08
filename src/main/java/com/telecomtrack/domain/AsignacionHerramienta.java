package com.telecomtrack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "asignacion_herramienta")
public class AsignacionHerramienta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Integer idAsignacion;

    @NotNull(message = "{validacion.asignacion.herramienta.requerida}")
    @ManyToOne
    @JoinColumn(name = "id_herramienta", nullable = false)
    private Herramienta herramienta;

    @NotNull(message = "{validacion.asignacion.tecnico.requerido}")
    @ManyToOne
    @JoinColumn(name = "id_tecnico", nullable = false)
    private Usuario tecnico;

    @NotNull(message = "{validacion.asignacion.proyecto.requerido}")
    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @NotNull(message = "{validacion.asignacion.fecha.requerida}")
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(nullable = false)
    private boolean activa;
}