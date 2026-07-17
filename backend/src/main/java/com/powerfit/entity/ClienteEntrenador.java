package com.powerfit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "clientes_entrenadores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteEntrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entrenador_id", nullable = false)
    private Entrenador entrenador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_asignacion")
    private LocalDate fechaAsignacion;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @PrePersist
    public void prePersist() {
        if (fechaAsignacion == null) fechaAsignacion = LocalDate.now();
        if (activo == null) activo = true;
    }
}
