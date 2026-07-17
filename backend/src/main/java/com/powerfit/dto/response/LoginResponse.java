package com.powerfit.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginResponse {
    private Long    id;
    private String  email;
    private String  nombreCompleto;
    private String  rol;
    private String  token;
    private Long    sucursalId;
    private String  sucursalNombre;
}
