package com.telecomtrack.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "configuracion_notificacion")
public class ConfiguracionNotificacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Integer idConfiguracion;

    @Column(name = "notificar_stock_minimo", nullable = false)
    private boolean notificarStockMinimo;

    @Column(name = "notificar_solicitudes_pendientes", nullable = false)
    private boolean notificarSolicitudesPendientes;
}
