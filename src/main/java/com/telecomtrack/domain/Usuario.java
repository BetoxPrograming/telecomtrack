package com.telecomtrack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank(message = "{validacion.nombre.requerido}")
    @Size(max = 50, message = "{validacion.usuario.nombre.longitud}")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "{validacion.usuario.apellido.requerido}")
    @Size(max = 50, message = "{validacion.usuario.apellido.longitud}")
    @Column(nullable = false, length = 50)
    private String apellido;

    @NotBlank(message = "{validacion.usuario.correo.requerido}")
    @Email(message = "{validacion.usuario.correo.formato}")
    @Size(max = 100, message = "{validacion.usuario.correo.longitud}")
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    /*
     * Campo temporal de compatibilidad con el CRUD creado en el Issue 2.
     * Se conserva durante la transición a la relación usuario_rol para no
     * reemplazar ni romper el CRUD existente.
     */
    @NotBlank(message = "{validacion.usuario.rol.requerido}")
    @Size(max = 20, message = "{validacion.usuario.rol.longitud}")
    @Column(nullable = false, length = 20)
    private String rol;

    /*
     * La contraseña se incorpora al modelo en esta tarea.
     * Se deja opcional temporalmente porque el cifrado y la autenticación
     * se implementan en la siguiente tarea del Issue 14.
     */
    @Size(max = 255, message = "{validacion.usuario.password.longitud}")
    @Column(length = 255)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private List<Rol> roles = new ArrayList<>();

    private boolean activo;
}
