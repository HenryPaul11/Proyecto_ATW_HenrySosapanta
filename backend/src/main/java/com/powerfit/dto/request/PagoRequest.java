package com.powerfit.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoRequest {

    @NotNull(message = "El membresiaId es obligatorio")
    private Integer membresiaId;

    @NotNull(message = "El clienteId es obligatorio")
    private Integer clienteId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotBlank(message = "El método de pago es obligatorio")
    @Pattern(regexp = "efectivo|tarjeta|transferencia",
             message = "Método de pago debe ser: efectivo, tarjeta o transferencia")
    private String metodoPago;

    private Integer registradoPorId;
}
