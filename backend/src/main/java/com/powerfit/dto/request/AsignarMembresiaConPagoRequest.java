package com.powerfit.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Request para el proceso transaccional de asignar membresía y registrar pago
 * en una sola operación atómica (@Transactional).
 */
@Data
public class AsignarMembresiaConPagoRequest {

    @NotNull(message = "El clienteId es obligatorio")
    private Integer clienteId;

    @NotNull(message = "El tipoMembresiaId es obligatorio")
    private Integer tipoMembresiaId;

    @NotBlank(message = "El método de pago es obligatorio")
    @Pattern(regexp = "efectivo|tarjeta|transferencia",
             message = "Método de pago: efectivo, tarjeta o transferencia")
    private String metodoPago;

    @NotNull(message = "El sucursalId es obligatorio")
    private Integer sucursalId;

    // Opcional: si no se envía, se usa el precio del tipo de membresía
    private BigDecimal montoPersonalizado;

    private Integer registradoPorId;
}
