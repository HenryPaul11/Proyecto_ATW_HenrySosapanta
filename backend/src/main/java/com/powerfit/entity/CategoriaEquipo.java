package com.powerfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias_equipo")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(length = 255)
    private String descripcion;
}
