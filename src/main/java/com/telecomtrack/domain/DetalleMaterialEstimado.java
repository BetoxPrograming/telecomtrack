package com.telecomtrack.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "detalle_material_estimado")
public class DetalleMaterialEstimado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_listado", nullable = false)
    private ListadoMaterialEstimado listado;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_material", nullable = false)
    private Material material;

    @NotNull(message = "{validacion.listadoMaterialEstimado.cantidad.requerida}")
    @Min(value = 1, message = "{validacion.listadoMaterialEstimado.cantidad.minima}")
    @Column(name = "cantidad_estimada", nullable = false)
    private Integer cantidadEstimada;
}
