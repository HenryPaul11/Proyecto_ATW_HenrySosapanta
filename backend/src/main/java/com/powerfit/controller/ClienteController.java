package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.Cliente;
import com.powerfit.entity.Sucursal;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.ClienteRepository;
import com.powerfit.repository.SucursalRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes")
public class ClienteController {

    private final ClienteRepository clienteRepo;
    private final SucursalRepository sucursalRepo;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cliente>>> listar(
            @RequestParam(required = false) Long sucursalId) {
        List<Cliente> lista = (sucursalId != null)
            ? clienteRepo.buscar(null, sucursalId, Pageable.unpaged()).getContent()
            : clienteRepo.findAll();
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @GetMapping("/paginado")
    public ResponseEntity<ApiResponse<Page<Cliente>>> paginado(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)    String q,
            @RequestParam(required = false)    Long sucursalId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombreCompleto").ascending());
        return ResponseEntity.ok(ApiResponse.ok(clienteRepo.buscar(q, sucursalId, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> porId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(clienteRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id))));
    }

    @GetMapping("/documento/{doc}")
    public ResponseEntity<ApiResponse<Cliente>> porDocumento(@PathVariable String doc) {
        return ResponseEntity.ok(ApiResponse.ok(
            clienteRepo.findByDocumentoIdentidadAndEstado(doc, Cliente.EstadoGeneral.ACTIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + doc))));
    }

    @GetMapping("/sin-membresia")
    public ResponseEntity<ApiResponse<List<Cliente>>> sinMembresia(
            @RequestParam(required = false) Long sucursalId) {
        return ResponseEntity.ok(ApiResponse.ok(clienteRepo.sinMembresia(sucursalId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cliente>> crear(@RequestBody Map<String, Object> body) {
        Long sucursalId = body.get("sucursalId") != null ? Long.valueOf(body.get("sucursalId").toString()) : null;
        if (sucursalId == null) throw new BadRequestException("sucursalId es obligatorio");
        Sucursal sucursal = sucursalRepo.findById(sucursalId)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + sucursalId));

        String doc = body.getOrDefault("documentoIdentidad", "").toString();
        if (clienteRepo.existsByDocumentoIdentidad(doc))
            throw new BadRequestException("Documento ya registrado: " + doc);

        Cliente c = Cliente.builder()
            .sucursal(sucursal)
            .nombreCompleto(body.getOrDefault("nombreCompleto", "").toString())
            .documentoIdentidad(doc)
            .email(body.get("email") != null ? body.get("email").toString() : null)
            .telefono(body.get("telefono") != null ? body.get("telefono").toString() : null)
            .genero(body.get("genero") != null ? body.get("genero").toString() : null)
            .estado(Cliente.EstadoGeneral.ACTIVO)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(clienteRepo.save(c), "Cliente registrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> actualizar(@PathVariable Long id,
                                                            @RequestBody Map<String, Object> body) {
        Cliente c = clienteRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
        if (body.get("nombreCompleto") != null) c.setNombreCompleto(body.get("nombreCompleto").toString());
        if (body.get("email")          != null) c.setEmail(body.get("email").toString());
        if (body.get("telefono")       != null) c.setTelefono(body.get("telefono").toString());
        return ResponseEntity.ok(ApiResponse.ok(clienteRepo.save(c), "Cliente actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        Cliente c = clienteRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + id));
        c.setEstado(Cliente.EstadoGeneral.INACTIVO);
        clienteRepo.save(c);
        return ResponseEntity.ok(ApiResponse.ok(null, "Cliente desactivado"));
    }
}
