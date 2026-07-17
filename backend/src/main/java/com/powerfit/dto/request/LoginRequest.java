package com.powerfit.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @JsonAlias({"usuario", "username"})
    @NotBlank(message = "El usuario o email es obligatorio")
    private String email;

    @JsonAlias("contrasena")
    @NotBlank(message = "La contrasena es obligatoria")
    private String password;
}
