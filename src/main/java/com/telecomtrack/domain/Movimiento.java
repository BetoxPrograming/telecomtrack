package com.telecomtrack.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "movimiento")
public class Movimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @NotNull(message = "{validacion.movimiento.cantidad.requerida}")
    @Min(value = 1, message = "{validacion.movimiento.cantidad.minima}")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Size(max = 255, message = "{validacion.movimiento.observacion.longitud}")
    @Column(name = "observacion", length = 255)
    private String observacion;

    @NotBlank(message = "{validacion.movimiento.responsable.requerido}")
    @Size(max = 100, message = "{validacion.movimiento.responsable.longitud}")
    @Column(name = "responsable", nullable = false, length = 100)
    private String responsable;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_material", nullable = false)
    private Material material;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;
}
