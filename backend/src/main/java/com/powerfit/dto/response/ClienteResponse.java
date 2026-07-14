package com.powerfit.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private Integer   id;
    private String    nombre;
    private String    apellido;
    private String    cedula;
    private String    telefono;
    private String    email;
    private LocalDate fechaRegistro;
    private Integer   usuarioId;
    private String    usuarioNombre;
}
