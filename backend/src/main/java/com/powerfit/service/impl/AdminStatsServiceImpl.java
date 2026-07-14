package com.powerfit.service.impl;

import com.powerfit.dto.response.AdminStatsResponse;
import com.powerfit.repository.*;
import com.powerfit.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminStatsServiceImpl implements AdminStatsService {

    private final ClienteRepository    clienteRepository;
    private final MembresiaRepository  membresiaRepository;
    private final PagoRepository       pagoRepository;
    private final SesionRepository     sesionRepository;
    private final EquipoRepository     equipoRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final SucursalRepository   sucursalRepository;

    @Override
    public AdminStatsResponse obtenerStats() {
        BigDecimal ingresosMes   = pagoRepository.sumIngresosMesActual();
        BigDecimal ingresosTotal = pagoRepository.sumTotalIngresos();
        BigDecimal promedio      = pagoRepository.avgMonto();

        return AdminStatsResponse.builder()
                // Clientes
                .totalClientes(clienteRepository.count())
                .clientesActivos(clienteRepository.countByActivoTrue())
                // Membresías
                .membresiasActivas((long) membresiaRepository.findActivas(LocalDate.now()).size())
                .membresiasTotales(membresiaRepository.count())
                // Ingresos
                .ingresosMensuales(ingresosMes   != null ? ingresosMes   : BigDecimal.ZERO)
                .ingresosTotal(ingresosTotal     != null ? ingresosTotal  : BigDecimal.ZERO)
                // Sesiones
                .sesionesTotales(sesionRepository.count())
                .sesionesCompletadas(sesionRepository.countCompletadas())
                .sesionesPendientes(sesionRepository.countPendientes())
                .sesionesCanceladas(sesionRepository.countCanceladas())
                // Pagos
                .pagosTotales(pagoRepository.count())
                .promedioMembresia(promedio != null ? promedio : BigDecimal.ZERO)
                // Equipos
                .equiposDisponibles(equipoRepository.countDisponibles())
                .equiposMantenimiento(equipoRepository.countMantenimiento())
                // Infraestructura
                .totalSucursales(sucursalRepository.count())
                .totalEntrenadores(entrenadorRepository.count())
                .build();
    }
}
