package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.request.PagoRequest;
import com.powerfit.dto.response.PagoEstadisticasResponse;
import com.powerfit.dto.response.PagoResponse;
import com.powerfit.service.PagoService;
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
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Gestión de pagos")
public class PagoController {

    private final PagoService pagoService;

    // ── Paginación FÍSICA ────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar pagos — paginación FÍSICA",
               description = "Trae solo los registros de la página desde la BD. " +
                             "Parámetros: page (0-based), size, ordenado por fecha_pago DESC")
    public ResponseEntity<ApiResponse<Page<PagoResponse>>> listar(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PagoResponse> resultado = pagoService.listarPaginado(
                PageRequest.of(page, size, Sort.by("fechaPago").descending()));
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pago por ID")
    public ResponseEntity<ApiResponse<PagoResponse>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(pagoService.buscarPorId(id)));
    }

    // ── Paginación LÓGICA (filtro por cliente) ────────────────────────────────

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Pagos de un cliente — paginación LÓGICA",
               description = "Filtra lógicamente todos los pagos de un cliente específico")
    public ResponseEntity<ApiResponse<List<PagoResponse>>> porCliente(
            @PathVariable Integer clienteId) {
        return ResponseEntity.ok(ApiResponse.ok(pagoService.listarPorCliente(clienteId)));
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Estadísticas de pagos (totales, promedios, por método)")
    public ResponseEntity<ApiResponse<PagoEstadisticasResponse>> estadisticas() {
        return ResponseEntity.ok(ApiResponse.ok(pagoService.estadisticas()));
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Registrar pago")
    public ResponseEntity<ApiResponse<PagoResponse>> registrar(
            @Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(pagoService.registrar(request),
                        "Pago registrado correctamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pago (eliminación FÍSICA)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        pagoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Pago eliminado"));
    }
}
