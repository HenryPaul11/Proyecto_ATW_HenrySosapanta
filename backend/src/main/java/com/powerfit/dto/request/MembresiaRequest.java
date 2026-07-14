package com.powerfit.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MembresiaRequest {
    @NotNull(message = "El clienteId es obligatorio")
    private Integer clienteId;

    @NotNull(message = "El tipoMembresiaId es obligatorio")
    private Integer tipoMembresiaId;
}
