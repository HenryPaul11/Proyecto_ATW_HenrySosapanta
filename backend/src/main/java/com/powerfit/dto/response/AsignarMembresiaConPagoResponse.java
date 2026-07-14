package com.powerfit.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Respuesta del proceso transaccional:
 * contiene datos de la membresía creada y el pago registrado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarMembresiaConPagoResponse {
    // Datos de la membresía
    private Integer   membresiaId;
    private Integer   clienteId;
    private String    clienteNombre;
    private String    tipoMembresia;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String    estadoMembresia;

    // Datos del pago
    private Integer       pagoId;
    private BigDecimal    monto;
    private String        metodoPago;
    private LocalDateTime fechaPago;
    private String        sucursal;
}
