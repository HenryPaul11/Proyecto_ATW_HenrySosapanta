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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
@Tag(name = "Equipos")
public class EquipoController {

    private final EquipoRepository    equipoRepo;
    private final SucursalRepository  sucursalRepo;
    private final CategoriaEquipoRepository categoriaRepo;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Equipo>>> listar(
            @RequestParam(required = false) Long sucursalId) {
        return ResponseEntity.ok(ApiResponse.ok(equipoRepo.findBySucursal(sucursalId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Equipo>> porId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(equipoRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + id))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Equipo>> crear(@RequestBody Map<String, Object> body) {
        Long sucursalId  = Long.valueOf(body.get("sucursalId").toString());
        Long categoriaId = Long.valueOf(body.get("categoriaId").toString());
        Sucursal sucursal   = sucursalRepo.findById(sucursalId)
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));
        CategoriaEquipo cat = categoriaRepo.findById(categoriaId)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        String serie = body.getOrDefault("numeroSerie", "").toString();
        if (equipoRepo.existsByNumeroSerie(serie))
            throw new BadRequestException("Número de serie ya registrado: " + serie);

        Equipo e = Equipo.builder()
            .sucursal(sucursal).categoria(cat)
            .nombre(body.get("nombre").toString())
            .marca(body.get("marca") != null ? body.get("marca").toString() : null)
            .modelo(body.get("modelo") != null ? body.get("modelo").toString() : null)
            .numeroSerie(serie)
            .valorAdquisicion(body.get("valorAdquisicion") != null ? new BigDecimal(body.get("valorAdquisicion").toString()) : null)
            .estado(Equipo.EstadoEquipo.OPERATIVO)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(equipoRepo.save(e), "Equipo registrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Equipo>> actualizar(@PathVariable Long id,
                                                           @RequestBody Map<String, Object> body) {
        Equipo e = equipoRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + id));
        if (body.get("nombre") != null) e.setNombre(body.get("nombre").toString());
        if (body.get("estado") != null) e.setEstado(Equipo.EstadoEquipo.valueOf(body.get("estado").toString()));
        return ResponseEntity.ok(ApiResponse.ok(equipoRepo.save(e), "Equipo actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        Equipo e = equipoRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + id));
        e.setEstado(Equipo.EstadoEquipo.DADO_DE_BAJA);
        equipoRepo.save(e);
        return ResponseEntity.ok(ApiResponse.ok(null, "Equipo dado de baja"));
    }
}
