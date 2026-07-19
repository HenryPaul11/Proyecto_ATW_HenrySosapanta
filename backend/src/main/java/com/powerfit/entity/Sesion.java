package com.powerfit.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "fieldHandler"})
@Entity
@Table(name = "sesiones")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sesion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenador_id", nullable = false)
    private Entrenador entrenador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TipoSesion tipo = TipoSesion.CLASE_GRUPAL;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "cupo_maximo", nullable = false)
    @Builder.Default
    private Integer cupoMaximo = 20;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoGeneral estado = EstadoGeneral.ACTIVO;

    public enum TipoSesion    { CLASE_GRUPAL, ENTRENAMIENTO_PERSONAL, LIBRE }
    public enum EstadoGeneral { ACTIVO, INACTIVO }
}
