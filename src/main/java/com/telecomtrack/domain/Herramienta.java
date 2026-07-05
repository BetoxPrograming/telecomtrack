package com.telecomtrack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "herramienta")
public class Herramienta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_herramienta")
    private Integer idHerramienta;

    @NotBlank(message = "{validacion.herramienta.codigo.requerido}")
    @Size(max = 50, message = "{validacion.herramienta.codigo.longitud}")
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "{validacion.herramienta.nombre.requerido}")
    @Size(max = 100, message = "{validacion.herramienta.nombre.longitud}")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 255, message = "{validacion.herramienta.descripcion.longitud}")
    @Column(length = 255)
    private String descripcion;

    @NotBlank(message = "{validacion.herramienta.estado.requerido}")
    @Size(max = 20, message = "{validacion.herramienta.estado.longitud}")
    @Column(nullable = false, length = 20)
    private String estado;

    @NotNull(message = "{validacion.herramienta.fechaRetornoEstimada.requerido}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_retorno_estimada")
    private LocalDate fechaRetornoEstimada;

    @Column(name = "fecha_baja_definitiva")
    private LocalDate fechaBajaDefinitiva;

    @Size(max = 255, message = "{validacion.herramienta.justificacion.longitud}")
    @Column(name = "justificacion_baja_definitiva", length = 255)
    private String justificacionBajaDefinitiva;
}
