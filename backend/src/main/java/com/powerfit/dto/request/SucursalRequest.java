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

    // ── Credenciales del administrador de la sucursal ──────────────────────
    /** Si se envía, se crea automáticamente el usuario y se vincula a la sucursal */
    @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
    private String usuario;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    /** Si ya existe el usuario, se puede pasar su ID directamente */
    private Integer usuarioId;
}
