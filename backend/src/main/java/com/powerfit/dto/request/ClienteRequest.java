package com.powerfit.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String apellido;

    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "\\d{10}", message = "La cédula debe tener exactamente 10 dígitos numéricos")
    private String cedula;

    @Size(max = 15)
    private String telefono;

    @Email(message = "Formato de email inválido")
    private String email;

    /**
     * Opcional. Si se envía, debe ser el ID de un usuario existente en usuarios_sistema.
     * Si no se envía o es null, el cliente se crea sin usuario asociado.
     */
    private Integer usuarioId;
}
