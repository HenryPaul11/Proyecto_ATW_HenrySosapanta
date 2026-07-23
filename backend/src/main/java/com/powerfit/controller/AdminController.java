package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.repository.*;
import com.powerfit.entity.Cliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard")
public class AdminController {

    private final ClienteRepository   clienteRepo;
    private final MembresiaRepository membresiaRepo;
    private final PagoRepository      pagoRepo;
    private final EquipoRepository    equipoRepo;

    @GetMapping("/stats")
    @Operation(summary = "Estadisticas del dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stats(
            @RequestParam(required = false) Long sucursalId) {

        long totalClientes    = clienteRepo.countByEstadoAndSucursalId(Cliente.EstadoGeneral.ACTIVO, sucursalId);
        long membresiasActivas = membresiaRepo.countActivas(sucursalId);
        BigDecimal ingresos   = pagoRepo.sumIngresos(sucursalId);
        long pagosTotales     = pagoRepo.countBySucursalId(sucursalId);

        BigDecimal egresosEquipos = equipoRepo.sumValorAdquisicionMesActual(sucursalId);
        BigDecimal totalEgresos = egresosEquipos != null ? egresosEquipos : BigDecimal.ZERO;
        BigDecimal ingresosVal = ingresos != null ? ingresos : BigDecimal.ZERO;
        BigDecimal balance = ingresosVal.subtract(totalEgresos);

        Map<String, Object> data = Map.of(
            "totalClientes",     totalClientes,
            "clientesActivos",   totalClientes,
            "membresiasActivas", membresiasActivas,
            "ingresosMensuales", ingresosVal,
            "ingresosTotal",     ingresosVal,
            "egresosMensuales",  totalEgresos,
            "balance",           balance,
            "pagosTotales",      pagosTotales
        );
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
