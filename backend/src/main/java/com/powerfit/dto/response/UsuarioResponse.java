package com.powerfit.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long          id;
    private String        usuario;
    private String        nombre;
    private String        correo;
    private String        rol;
    private Boolean       activo;
    private LocalDateTime createdAt;
}
