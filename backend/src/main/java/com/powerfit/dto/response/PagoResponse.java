package com.powerfit.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {
    private Integer       id;
    private Integer       membresiaId;
    private Integer       clienteId;
    private String        clienteNombre;
    private String        clienteApellido;
    private String        cedula;
    private String        tipoMembresia;
    private BigDecimal    monto;
    private String        metodoPago;
    private LocalDateTime fechaPago;
    private LocalDate     fechaInicio;
    private LocalDate     fechaFin;
    private String        estadoMembresia;
    private String        registradoPorNombre;
}
