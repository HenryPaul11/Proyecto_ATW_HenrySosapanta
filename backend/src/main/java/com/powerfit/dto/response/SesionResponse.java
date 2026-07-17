package com.powerfit.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionResponse {
    private Long      id;
    private Long      entrenadorId;
    private String    entrenadorNombre;
    private Long      clienteId;
    private String    clienteNombre;
    private LocalDate fecha;
    private LocalTime hora;
    private String    tipo;
    private String    estado;
    private String    notas;
}
