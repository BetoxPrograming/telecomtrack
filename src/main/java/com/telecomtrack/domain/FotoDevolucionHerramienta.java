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
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "foto_devolucion_herramienta")
public class FotoDevolucionHerramienta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto_devolucion")
    private Integer idFotoDevolucion;

    @NotNull(message = "{validacion.foto.devolucion.requerida}")
    @ManyToOne
    @JoinColumn(name = "id_devolucion", nullable = false)
    private DevolucionHerramienta devolucionHerramienta;

    @NotBlank(message = "{validacion.foto.ruta.requerida}")
    @Size(max = 500, message = "{validacion.foto.ruta.longitud}")
    @Column(name = "ruta_imagen", nullable = false, length = 500)
    private String rutaImagen;

    @NotBlank(message = "{validacion.foto.nombre.requerido}")
    @Size(max = 255, message = "{validacion.foto.nombre.longitud}")
    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "fecha_carga", nullable = false)
    private LocalDateTime fechaCarga;
}
