package com.telecomtrack.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "detalle_solicitud")
public class DetalleSolicitud implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitud solicitud;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_herramienta")
    private Herramienta herramienta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_material")
    private Material material;

    @Column(name = "cantidad")
    private Integer cantidad;
}
