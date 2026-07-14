package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.request.MembresiaRequest;
import com.powerfit.dto.response.MembresiaResponse;
import com.powerfit.dto.response.PagedResponse;
import com.powerfit.service.MembresiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membresias")
@RequiredArgsConstructor
@Tag(name = "Membresías", description = "Gestión de membresías de clientes")
public class MembresiaController {

    private final MembresiaService membresiaService;

    // ── Sin paginación ───────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar todas las membresías (sin paginar)")
    public ResponseEntity<ApiResponse<List<MembresiaResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(membresiaService.listarTodas()));
    }

    // ── Con paginación FÍSICA (Spring Data Page) ─────────────────────────────

    @GetMapping("/activas")
    @Operation(summary = "Membresías activas — paginación FÍSICA",
               description = "Trae solo los registros de la página solicitada desde la BD. " +
                             "Parámetros: page (0-based), size")
    public ResponseEntity<ApiResponse<PagedResponse<MembresiaResponse>>> activas(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                membresiaService.listarActivasPaginado(page, size),
                "Membresías activas — página " + page));
    }

    @GetMapping("/vencidas")
    @Operation(summary = "Membresías vencidas — paginación FÍSICA",
               description = "Trae solo los registros de la página solicitada desde la BD.")
    public ResponseEntity<ApiResponse<PagedResponse<MembresiaResponse>>> vencidas(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                membresiaService.listarVencidasPaginado(page, size),
                "Membresías vencidas — página " + page));
    }

    // ── Paginación LÓGICA (filtro por cliente) ────────────────────────────────

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Membresías de un cliente — paginación LÓGICA",
               description = "Filtra lógicamente por cliente. Incluye activas y vencidas.")
    public ResponseEntity<ApiResponse<List<MembresiaResponse>>> porCliente(
            @PathVariable Integer clienteId) {
        return ResponseEntity.ok(ApiResponse.ok(membresiaService.listarPorCliente(clienteId)));
    }

    // ── Operaciones CRUD ──────────────────────────────────────────────────────

    @PostMapping("/asignar")
    @Operation(summary = "Asignar membresía (calcula fechas automáticamente)")
    public ResponseEntity<ApiResponse<MembresiaResponse>> asignar(
            @Valid @RequestBody MembresiaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(membresiaService.asignar(request),
                        "Membresía asignada correctamente"));
    }

    @PutMapping("/{id}/renovar")
    @Operation(summary = "Renovar membresía — extiende fecha_fin")
    public ResponseEntity<ApiResponse<MembresiaResponse>> renovar(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(
                membresiaService.renovar(id), "Membresía renovada correctamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar membresía (eliminación FÍSICA)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        membresiaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Membresía eliminada"));
    }
}
