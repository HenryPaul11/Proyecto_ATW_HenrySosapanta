package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.request.ClienteRequest;
import com.powerfit.dto.response.ClienteResponse;
import com.powerfit.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar todos los clientes activos")
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.listarTodos()));
    }

    @GetMapping("/paginado")
    @Operation(summary = "Listar clientes con paginación, búsqueda y filtro por sucursal")
    public ResponseEntity<ApiResponse<Page<ClienteResponse>>> listarPaginado(
            @RequestParam(defaultValue = "0")   int     page,
            @RequestParam(defaultValue = "10")  int     size,
            @RequestParam(required = false)     String  busqueda,
            @RequestParam(required = false)     Integer sucursalId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("apellido").ascending());
        Page<ClienteResponse> resultado = clienteService.listarPaginado(busqueda, sucursalId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<ApiResponse<ClienteResponse>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.buscarPorId(id)));
    }

    @GetMapping("/cedula/{cedula}")
    @Operation(summary = "Buscar cliente por cédula")
    public ResponseEntity<ApiResponse<ClienteResponse>> buscarPorCedula(@PathVariable String cedula) {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.buscarPorCedula(cedula)));
    }

    @GetMapping("/sin-membresia")
    @Operation(summary = "Clientes sin membresía vigente")
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> sinMembresia(
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.sinMembresia(sucursalId)));
    }

    @PostMapping
    @Operation(summary = "Registrar cliente")
    public ResponseEntity<ApiResponse<ClienteResponse>> crear(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(clienteService.crear(request), "Cliente registrado correctamente"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente")
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(clienteService.actualizar(id, request), "Cliente actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminación lógica del cliente (activo = false)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        clienteService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Cliente desactivado correctamente"));
    }
}
