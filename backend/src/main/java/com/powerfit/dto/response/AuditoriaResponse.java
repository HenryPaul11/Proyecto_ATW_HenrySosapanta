package com.powerfit.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaResponse {
    private Integer       id;
    private String        tablaAfectada;
    private String        accion;
    private String        usuario;
    private LocalDateTime fechaHora;
    private String        datosAnteriores;
    private String        datosNuevos;
    private String        ipAddress;
}
