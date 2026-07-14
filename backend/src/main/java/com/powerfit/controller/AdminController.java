package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.response.AdminStatsResponse;
import com.powerfit.service.AdminStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Estadísticas globales para el panel administrador")
public class AdminController {

    private final AdminStatsService adminStatsService;

    @GetMapping("/stats")
    @Operation(summary = "Estadísticas del dashboard admin")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> stats() {
        return ResponseEntity.ok(ApiResponse.ok(adminStatsService.obtenerStats()));
    }
}
