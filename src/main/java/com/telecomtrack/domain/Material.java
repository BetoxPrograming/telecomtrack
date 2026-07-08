package com.telecomtrack.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "material")
public class Material implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material")
    private Long idMaterial;

    @NotBlank(message = "{validacion.material.codigo.requerido}")
    @Size(max = 50, message = "{validacion.material.codigo.longitud}")
    @Column(name = "codigo_unico", unique = true, nullable = false, length = 50)
    private String codigoUnico;

    @NotBlank(message = "{validacion.material.nombre.requerido}")
    @Size(max = 100, message = "{validacion.material.nombre.longitud}")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 255, message = "{validacion.material.descripcion.longitud}")
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @NotBlank(message = "{validacion.material.unidad.requerida}")
    @Size(max = 30, message = "{validacion.material.unidad.longitud}")
    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida;

    @Min(value = 0, message = "{validacion.material.stock.negativo}")
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    @NotNull(message = "{validacion.material.stockMinimo.requerido}")
    @Min(value = 0, message = "{validacion.material.stockMinimo.negativo}")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    public boolean isStockBajo() {
        return stockActual != null && stockMinimo != null && stockActual <= stockMinimo;
    }
}
