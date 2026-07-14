package com.powerfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Column(length = 255)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEquipo estado = EstadoEquipo.disponible;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @PrePersist
    public void prePersist() {
        if (estado == null) estado = EstadoEquipo.disponible;
    }

    public enum Categoria {
        Cardio, Fuerza, Funcional, Flexibilidad
    }

    public enum EstadoEquipo {
        disponible, mantenimiento, fuera_de_servicio
    }
}
