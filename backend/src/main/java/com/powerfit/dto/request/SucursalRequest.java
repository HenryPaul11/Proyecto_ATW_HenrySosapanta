package com.powerfit.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SucursalRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200)
    private String direccion;

    @Size(max = 15)
    private String telefono;

    @Size(max = 100)
    private String ciudad;

    private LocalDate fechaApertura;
    private Boolean   activo;
}
