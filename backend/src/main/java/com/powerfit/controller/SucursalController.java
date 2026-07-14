package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.request.SucursalRequest;
import com.powerfit.dto.response.SucursalResponse;
import com.powerfit.service.SucursalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
@Tag(name = "Sucursales", description = "Gestión de sucursales del gimnasio PowerFit")
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    @Operation(summary = "Listar todas las sucursales activas")
    public ResponseEntity<ApiResponse<List<SucursalResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(sucursalService.listarTodas()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sucursal por ID")
    public ResponseEntity<ApiResponse<SucursalResponse>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(sucursalService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Crear nueva sucursal")
    public ResponseEntity<ApiResponse<SucursalResponse>> crear(@Valid @RequestBody SucursalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(sucursalService.crear(request), "Sucursal creada correctamente"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sucursal")
    public ResponseEntity<ApiResponse<SucursalResponse>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody SucursalRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(sucursalService.actualizar(id, request), "Sucursal actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminación lógica de sucursal (activo = false)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        sucursalService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Sucursal desactivada correctamente"));
    }
}
