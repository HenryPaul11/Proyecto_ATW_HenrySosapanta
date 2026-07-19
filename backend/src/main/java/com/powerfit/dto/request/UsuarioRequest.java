package com.powerfit.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioRequest {

    // Campo usuario — opcional, se usa el correo si no viene
    private String usuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    // Acepta cualquier rol — validación en el servicio
    private String rol;

    private Boolean activo;
    private Long    sucursalId;
}
