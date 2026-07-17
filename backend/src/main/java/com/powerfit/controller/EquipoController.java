package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.request.EquipoRequest;
import com.powerfit.dto.response.EquipoResponse;
import com.powerfit.service.EquipoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
@Tag(name = "Equipos", description = "Gestión de equipos del gimnasio")
public class EquipoController {

    private final EquipoService equipoService;

    @GetMapping
    @Operation(summary = "Listar todos los equipos filtrados por sucursal")
    public ResponseEntity<ApiResponse<List<EquipoResponse>>> listar(
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(ApiResponse.ok(equipoService.listarTodos(sucursalId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar equipo por ID")
    public ResponseEntity<ApiResponse<EquipoResponse>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(equipoService.buscarPorId(id)));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar equipos por categoría")
    public ResponseEntity<ApiResponse<List<EquipoResponse>>> porCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(ApiResponse.ok(equipoService.listarPorCategoria(categoria)));
    }

    @PostMapping
    @Operation(summary = "Registrar equipo")
    public ResponseEntity<ApiResponse<EquipoResponse>> crear(@Valid @RequestBody EquipoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(equipoService.crear(request), "Equipo registrado correctamente"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar equipo")
    public ResponseEntity<ApiResponse<EquipoResponse>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody EquipoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(equipoService.actualizar(id, request), "Equipo actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar equipo")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        equipoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Equipo eliminado"));
    }
}
