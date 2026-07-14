package com.powerfit.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionResponse {
    private Integer   id;
    private Integer   entrenadorId;
    private String    entrenadorNombre;
    private Integer   clienteId;
    private String    clienteNombre;
    private LocalDate fecha;
    private LocalTime hora;
    private String    tipo;
    private String    estado;
    private String    notas;
}
