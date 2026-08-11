package com.telecomtrack.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "listado_material_estimado")
public class ListadoMaterialEstimado implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_APROBADO = "APROBADO";
    public static final String ESTADO_RECHAZADO = "RECHAZADO";
    public static final String ESTADO_MODIFICACION_SOLICITADA = "MODIFICACION_SOLICITADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_listado")
    private Integer idListado;

    @NotNull(message = "{validacion.listadoMaterialEstimado.proyecto.requerido}")
    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(nullable = false, length = 30)
    private String estado = ESTADO_PENDIENTE;

    @Size(max = 255, message = "{validacion.listadoMaterialEstimado.comentario.longitud}")
    @Column(name = "comentario_supervisor", length = 255)
    private String comentarioSupervisor;

    @ManyToOne
    @JoinColumn(name = "id_usuario_decision")
    private Usuario usuarioDecision;

    @Column(name = "fecha_decision")
    private LocalDate fechaDecision;

    @OneToMany(mappedBy = "listado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleMaterialEstimado> detalles = new ArrayList<>();
}
