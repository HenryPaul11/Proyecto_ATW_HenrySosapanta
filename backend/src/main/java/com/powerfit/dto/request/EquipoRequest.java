package com.powerfit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EquipoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Pattern(regexp = "Cardio|Fuerza|Funcional|Flexibilidad",
             message = "Categoría debe ser: Cardio, Fuerza, Funcional o Flexibilidad")
    private String categoria;

    @Size(max = 255)
    private String descripcion;

    @Pattern(regexp = "disponible|mantenimiento|fuera_de_servicio",
             message = "Estado debe ser: disponible, mantenimiento o fuera_de_servicio")
    private String estado;

    @JsonAlias("imagen")
    private String imagenUrl;
}
