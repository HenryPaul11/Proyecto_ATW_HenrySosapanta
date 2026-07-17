package com.powerfit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer id;
    private String  usuario;
    private String  nombre;
    private String  correo;
    private String  rol;
    private String  token;
    private Integer sucursalId;    // null = admin matriz, valor = admin de esa sucursal
    private String  sucursalNombre;
}
