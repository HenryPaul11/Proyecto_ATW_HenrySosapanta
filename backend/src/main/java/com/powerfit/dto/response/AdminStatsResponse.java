package com.powerfit.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    // Totales generales
    private Long       totalClientes;
    private Long       clientesActivos;
    private Long       membresiasActivas;
    private Long       membresiasTotales;
    private BigDecimal ingresosMensuales;
    private BigDecimal ingresosTotal;

    // Métricas de sesiones
    private Long       sesionesTotales;
    private Long       sesionesCompletadas;
    private Long       sesionesPendientes;
    private Long       sesionesCanceladas;

    // Métricas de pagos
    private Long       pagosTotales;
    private BigDecimal promedioMembresia;

    // Métricas de equipos
    private Long       equiposDisponibles;
    private Long       equiposMantenimiento;

    // Métricas de sucursales
    private Long       totalSucursales;
    private Long       totalEntrenadores;
}
