package com.powerfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_membresia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoMembresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    @Column(nullable = false, precision = 8, scale = 2)
    private java.math.BigDecimal precio;

    @Column(nullable = false)
    private Boolean activo = true;

    @PrePersist
    public void prePersist() {
        if (activo == null) activo = true;
    }
}
