package com.powerfit.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembresiaResponse {
    private Integer   id;
    private Integer   clienteId;
    private String    clienteNombre;
    private String    clienteApellido;
    private String    clienteCedula;
    private Integer   tipoMembresiaId;
    private String    tipoMembresiaNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String    estado;          // calculado
    private LocalDateTime createdAt;
}
