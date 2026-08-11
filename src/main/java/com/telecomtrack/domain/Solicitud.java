package com.telecomtrack.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "solicitud")
public class Solicitud implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_APROBADA = "APROBADA";
    public static final String ESTADO_RECHAZADA = "RECHAZADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @NotNull(message = "{validacion.solicitud.tecnico.requerido}")
    @ManyToOne
    @JoinColumn(name = "id_tecnico", nullable = false)
    private Usuario tecnico;

    @NotNull(message = "{validacion.solicitud.proyecto.requerido}")
    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @NotNull(message = "{validacion.solicitud.ubicacion.requerida}")
    @ManyToOne
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private Ubicacion ubicacion;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(nullable = false, length = 20)
    private String estado = ESTADO_PENDIENTE;

    @Size(max = 255, message = "{validacion.solicitud.motivoRechazo.longitud}")
    @Column(name = "motivo_rechazo", length = 255)
    private String motivoRechazo;

    @ManyToOne
    @JoinColumn(name = "id_usuario_decision")
    private Usuario usuarioDecision;

    @Column(name = "fecha_decision")
    private LocalDateTime fechaDecision;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleSolicitud> detalles = new ArrayList<>();
}
