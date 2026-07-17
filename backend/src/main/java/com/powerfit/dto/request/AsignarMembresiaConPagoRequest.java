package com.powerfit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AsignarMembresiaConPagoRequest {

    @NotNull(message = "El clienteId es obligatorio")
    private Integer clienteId;

    @NotNull(message = "El tipoMembresiaId es obligatorio")
    private Integer tipoMembresiaId;

    @NotBlank(message = "El metodo de pago es obligatorio")
    @Pattern(regexp = "efectivo|tarjeta|transferencia|pago_movil",
            message = "Metodo de pago: efectivo, tarjeta, transferencia o pago_movil")
    private String metodoPago;

    @NotNull(message = "El sucursalId es obligatorio")
    private Integer sucursalId;

    private BigDecimal montoPersonalizado;
    private Integer registradoPorId;
}
