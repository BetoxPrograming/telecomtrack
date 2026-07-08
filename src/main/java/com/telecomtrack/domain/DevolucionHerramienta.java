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

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "devolucion_herramienta")
public class DevolucionHerramienta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devolucion")
    private Integer idDevolucion;

    @NotNull(message = "{validacion.devolucion.asignacion.requerida}")
    @ManyToOne
    @JoinColumn(name = "id_asignacion", nullable = false)
    private AsignacionHerramienta asignacionHerramienta;

    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDate fechaDevolucion;

    @NotBlank(message = "{validacion.devolucion.estado.requerido}")
    @Size(
            max = 20,
            message = "{validacion.devolucion.estado.longitud}"
    )
    @Column(
            name = "estado_devolucion",
            nullable = false,
            length = 20
    )
    private String estadoDevolucion;

    @Size(
            max = 500,
            message = "{validacion.devolucion.foto.longitud}"
    )
    @Column(
            name = "ruta_foto",
            length = 500
    )
    private String rutaFoto;
}