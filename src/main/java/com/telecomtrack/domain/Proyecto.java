package com.telecomtrack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "proyecto")
public class Proyecto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Integer idProyecto;

    @NotBlank(message = "{validacion.proyecto.nombre.requerido}")
    @Size(max = 100, message = "{validacion.proyecto.nombre.longitud}")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 255, message = "{validacion.proyecto.descripcion.longitud}")
    @Column(length = 255)
    private String descripcion;

    @NotNull(message = "{validacion.proyecto.fechaInicio.requerida}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_fin_estimada")
    private LocalDate fechaFinEstimada;

    @NotBlank(message = "{validacion.proyecto.estado.requerido}")
    @Size(max = 20, message = "{validacion.proyecto.estado.longitud}")
    @Column(nullable = false, length = 20)
    private String estado;

    @NotNull(message = "{validacion.proyecto.supervisor.requerido}")
    @ManyToOne
    @JoinColumn(name = "id_supervisor", nullable = false)
    private Usuario supervisor;
}