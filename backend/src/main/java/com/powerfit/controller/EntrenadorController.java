package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.*;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entrenadores")
@RequiredArgsConstructor
@Tag(name = "Entrenadores")
public class EntrenadorController {

    private final EntrenadorRepository entrenadorRepo;
    private final SucursalRepository   sucursalRepo;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Entrenador>>> listar(
            @RequestParam(required = false) Long sucursalId) {
        return ResponseEntity.ok(ApiResponse.ok(entrenadorRepo.findBySucursal(sucursalId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Entrenador>> porId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(entrenadorRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado: " + id))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Entrenador>> crear(@RequestBody Map<String, Object> body) {
        Long sucursalId = Long.valueOf(body.get("sucursalId").toString());
        Sucursal sucursal = sucursalRepo.findById(sucursalId)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        String doc = body.getOrDefault("documentoIdentidad", "").toString();
        if (entrenadorRepo.existsByDocumentoIdentidad(doc))
            throw new BadRequestException("Documento ya registrado: " + doc);

        Entrenador e = Entrenador.builder()
            .sucursal(sucursal)
            .nombreCompleto(body.get("nombreCompleto").toString())
            .documentoIdentidad(doc)
            .especialidad(body.get("especialidad") != null ? body.get("especialidad").toString() : null)
            .telefono(body.get("telefono") != null ? body.get("telefono").toString() : null)
            .email(body.get("email") != null ? body.get("email").toString() : null)
            .fechaContratacion(body.get("fechaContratacion") != null ? LocalDate.parse(body.get("fechaContratacion").toString()) : null)
            .estado(Entrenador.EstadoGeneral.ACTIVO)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(entrenadorRepo.save(e), "Entrenador registrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Entrenador>> actualizar(@PathVariable Long id,
                                                               @RequestBody Map<String, Object> body) {
        Entrenador e = entrenadorRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado: " + id));
        if (body.get("nombreCompleto") != null) e.setNombreCompleto(body.get("nombreCompleto").toString());
        if (body.get("especialidad")   != null) e.setEspecialidad(body.get("especialidad").toString());
        if (body.get("telefono")       != null) e.setTelefono(body.get("telefono").toString());
        return ResponseEntity.ok(ApiResponse.ok(entrenadorRepo.save(e), "Entrenador actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        Entrenador e = entrenadorRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado: " + id));
        e.setEstado(Entrenador.EstadoGeneral.INACTIVO);
        entrenadorRepo.save(e);
        return ResponseEntity.ok(ApiResponse.ok(null, "Entrenador desactivado"));
    }
}
