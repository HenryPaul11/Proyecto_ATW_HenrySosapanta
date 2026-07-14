package com.powerfit.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipoResponse {
    private Integer id;
    private String  nombre;
    private String  categoria;
    private String  descripcion;
    private String  estado;
    private String imagenUrl;

    @JsonProperty("imagen")
    public String getImagen() {
        return imagenUrl;
    }
}
