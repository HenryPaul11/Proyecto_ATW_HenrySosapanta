package com.powerfit.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalResponse {
    private Integer   id;
    private String    nombre;
    private String    direccion;
    private String    telefono;
    private String    ciudad;
    private LocalDate fechaApertura;
    private Boolean   activo;
}
