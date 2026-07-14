package com.powerfit.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SesionRequest {

    @NotNull(message = "El entrenadorId es obligatorio")
    private Integer entrenadorId;

    @NotNull(message = "El clienteId es obligatorio")
    private Integer clienteId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @Size(max = 50)
    private String tipo;

    @Pattern(regexp = "pendiente|completada|cancelada",
             message = "Estado debe ser: pendiente, completada o cancelada")
    private String estado;

    private String notas;
}
