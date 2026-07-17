package com.powerfit.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
    private String usuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "admin|cliente|entrenador", message = "Rol debe ser: admin, cliente o entrenador")
    private String rol;

    private Boolean activo;

    private Long sucursalId;
}