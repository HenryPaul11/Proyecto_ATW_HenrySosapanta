package com.powerfit.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoMembresiaResponse {
    private Integer    id;
    private String     nombre;
    private String     descripcion;
    private Integer    duracionDias;
    private BigDecimal precio;
    private Boolean    activo;
}
