package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.request.EntrenadorRequest;
import com.powerfit.dto.response.ClienteResponse;
import com.powerfit.dto.response.EntrenadorResponse;
import com.powerfit.dto.response.SesionResponse;
import com.powerfit.service.EntrenadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrenadores")
@RequiredArgsConstructor
@Tag(name = "Entrenadores", description = "Gestión de entrenadores")
public class EntrenadorController {

    private final EntrenadorService entrenadorService;

    @GetMapping
    @Operation(summary = "Listar todos los entrenadores")
    public ResponseEntity<ApiResponse<List<EntrenadorResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(entrenadorService.listarTodos()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar entrenador por ID")
    public ResponseEntity<ApiResponse<EntrenadorResponse>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(entrenadorService.buscarPorId(id)));
    }

    @GetMapping("/usuario/{username}")
    @Operation(summary = "Buscar entrenador por nombre de usuario")
    public ResponseEntity<ApiResponse<EntrenadorResponse>> buscarPorUsername(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.ok(entrenadorService.buscarPorUsername(username)));
    }

    @GetMapping("/{id}/clientes")
    @Operation(summary = "Clientes asignados a un entrenador")
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> clientes(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(entrenadorService.clientesDeEntrenador(id)));
    }

    @GetMapping("/{id}/sesiones")
    @Operation(summary = "Sesiones de un entrenador")
    public ResponseEntity<ApiResponse<List<SesionResponse>>> sesiones(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(entrenadorService.sesionesDeEntrenador(id)));
    }

    @PostMapping
    @Operation(summary = "Registrar entrenador")
    public ResponseEntity<ApiResponse<EntrenadorResponse>> crear(@Valid @RequestBody EntrenadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(entrenadorService.crear(request), "Entrenador registrado correctamente"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar entrenador")
    public ResponseEntity<ApiResponse<EntrenadorResponse>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody EntrenadorRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(entrenadorService.actualizar(id, request), "Entrenador actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar entrenador")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        entrenadorService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Entrenador eliminado"));
    }
}
