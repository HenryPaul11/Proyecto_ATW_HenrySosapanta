package com.powerfit.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "fieldHandler"})
@Entity
@Table(name = "equipos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEquipo categoria;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 80)
    private String marca;

    @Column(length = 80)
    private String modelo;

    @Column(name = "numero_serie", nullable = false, unique = true, length = 80)
    private String numeroSerie;

    @Column(name = "fecha_adquisicion")
    private LocalDate fechaAdquisicion;

    @Column(name = "valor_adquisicion", precision = 10, scale = 2)
    private BigDecimal valorAdquisicion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoEquipo estado = EstadoEquipo.OPERATIVO;

    @Column(length = 500)
    private String imagenUrl;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        fechaActualizacion = LocalDateTime.now();
        if (estado == null) estado = EstadoEquipo.OPERATIVO;
    }

    @PreUpdate
    public void preUpdate() { fechaActualizacion = LocalDateTime.now(); }

    public enum EstadoEquipo { OPERATIVO, MANTENIMIENTO, DADO_DE_BAJA }
}
