package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.request.SesionRequest;
import com.powerfit.dto.response.SesionResponse;
import com.powerfit.service.SesionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sesiones")
@RequiredArgsConstructor
@Tag(name = "Sesiones", description = "Gestión de sesiones de entrenamiento")
public class SesionController {

    private final SesionService sesionService;

    // ── Paginación FÍSICA ────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar sesiones — paginación FÍSICA",
               description = "Parámetros: page (0-based), size, estado (opcional: pendiente/completada/cancelada)")
    public ResponseEntity<ApiResponse<Page<SesionResponse>>> listar(
            @RequestParam(defaultValue = "0")   int    page,
            @RequestParam(defaultValue = "20")  int    size,
            @RequestParam(required = false)     String estado) {
        Page<SesionResponse> resultado = sesionService.listarPaginado(
                estado,
                PageRequest.of(page, size, Sort.by("fecha").descending()));
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sesión por ID")
    public ResponseEntity<ApiResponse<SesionResponse>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(sesionService.buscarPorId(id)));
    }

    // ── Paginación LÓGICA (filtro por entrenador o cliente) ───────────────────

    @GetMapping("/entrenador/{entrenadorId}")
    @Operation(summary = "Sesiones de un entrenador — paginación LÓGICA",
               description = "Filtra lógicamente por entrenador")
    public ResponseEntity<ApiResponse<List<SesionResponse>>> porEntrenador(
            @PathVariable Integer entrenadorId) {
        return ResponseEntity.ok(ApiResponse.ok(sesionService.listarPorEntrenador(entrenadorId)));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Sesiones de un cliente — paginación LÓGICA",
               description = "Filtra lógicamente por cliente")
    public ResponseEntity<ApiResponse<List<SesionResponse>>> porCliente(
            @PathVariable Integer clienteId) {
        return ResponseEntity.ok(ApiResponse.ok(sesionService.listarPorCliente(clienteId)));
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Crear sesión")
    public ResponseEntity<ApiResponse<SesionResponse>> crear(
            @Valid @RequestBody SesionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(sesionService.crear(request), "Sesión creada correctamente"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sesión — cambiar estado, agregar notas")
    public ResponseEntity<ApiResponse<SesionResponse>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody SesionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                sesionService.actualizar(id, request), "Sesión actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sesión (eliminación FÍSICA)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        sesionService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Sesión eliminada"));
    }
}
