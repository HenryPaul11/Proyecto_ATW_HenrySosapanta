package com.powerfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "sucursales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(length = 15)
    private String telefono;

    @Column(length = 100)
    private String ciudad;

    @Column(name = "fecha_apertura")
    private LocalDate fechaApertura;

    @Column(nullable = false)
    private Boolean activo = true;

    /** Usuario administrador asignado a esta sucursal */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioSistema usuarioSistema;

    @PrePersist
    public void prePersist() {
        if (activo == null) activo = true;
    }
}
