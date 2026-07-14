package com.powerfit.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoEstadisticasResponse {
    private BigDecimal totalIngresos;
    private Long       totalPagos;
    private BigDecimal promedio;
    private Long       conteoEfectivo;
    private Long       conteoTarjeta;
    private Long       conteoTransferencia;
}
