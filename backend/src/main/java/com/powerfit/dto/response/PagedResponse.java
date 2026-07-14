package com.powerfit.dto.response;

import lombok.*;
import java.util.List;

/**
 * Respuesta paginada estándar para cualquier entidad.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> contenido;
    private int     paginaActual;
    private int     totalPaginas;
    private long    totalElementos;
    private int     tamanioPagina;
    private boolean esUltima;
}
