package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.repository.ClienteRepository;
import com.powerfit.repository.MembresiaRepository;
import com.powerfit.repository.PagoRepository;
import com.powerfit.entity.Cliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard")
public class AdminController {

    private final ClienteRepository   clienteRepo;
    private final MembresiaRepository membresiaRepo;
    private final PagoRepository      pagoRepo;

    @GetMapping("/stats")
    @Operation(summary = "Estadísticas del dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stats(
            @RequestParam(required = false) Long sucursalId) {

        long totalClientes    = clienteRepo.countByEstado(Cliente.EstadoGeneral.ACTIVO);
        long membresiasActivas = membresiaRepo.countActivas(sucursalId);
        BigDecimal ingresos   = pagoRepo.sumIngresos(sucursalId);
        long pagosTotales     = pagoRepo.count();

        Map<String, Object> data = Map.of(
            "totalClientes",     totalClientes,
            "clientesActivos",   totalClientes,
            "membresiasActivas", membresiasActivas,
            "ingresosMensuales", ingresos != null ? ingresos : BigDecimal.ZERO,
            "ingresosTotal",     ingresos != null ? ingresos : BigDecimal.ZERO,
            "pagosTotales",      pagosTotales
        );
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
