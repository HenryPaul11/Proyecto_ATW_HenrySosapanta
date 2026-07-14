package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.request.TipoMembresiaRequest;
import com.powerfit.dto.response.TipoMembresiaResponse;
import com.powerfit.service.TipoMembresiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-membresia")
@RequiredArgsConstructor
@Tag(name = "Tipos de Membresía", description = "Gestión de planes de membresía")
public class TipoMembresiaController {

    private final TipoMembresiaService tipoService;

    @GetMapping
    @Operation(summary = "Listar todos los tipos de membresía")
    public ResponseEntity<ApiResponse<List<TipoMembresiaResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(tipoService.listarTodos()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tipo de membresía por ID")
    public ResponseEntity<ApiResponse<TipoMembresiaResponse>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(tipoService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Crear tipo de membresía")
    public ResponseEntity<ApiResponse<TipoMembresiaResponse>> crear(
            @Valid @RequestBody TipoMembresiaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(tipoService.crear(request), "Tipo de membresía creado"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de membresía")
    public ResponseEntity<ApiResponse<TipoMembresiaResponse>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody TipoMembresiaRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(tipoService.actualizar(id, request), "Tipo actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de membresía")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        tipoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Tipo de membresía eliminado"));
    }
}
