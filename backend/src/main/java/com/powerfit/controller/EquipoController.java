package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.*;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ForbiddenException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.*;
import com.powerfit.util.SecurityUtil;
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
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Equipo>>> listar() {
        Long sucursalId = securityUtil.getSucursalIdEfectiva();
        return ResponseEntity.ok(ApiResponse.ok(equipoRepo.findBySucursal(sucursalId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Equipo>> porId(@PathVariable Long id) {
        Equipo equipo = equipoRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + id));
        Long sucursalId = securityUtil.getSucursalIdEfectiva();
        if (sucursalId != null && !equipo.getSucursal().getId().equals(sucursalId)) {
            throw new ForbiddenException("No tienes acceso a equipos de otra sucursal");
        }
        return ResponseEntity.ok(ApiResponse.ok(equipo));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Equipo>> crear(@RequestBody Map<String, Object> body) {
        Long sucursalId = securityUtil.getSucursalIdEfectiva();

        Sucursal sucursal;
        if (sucursalId != null) {
            sucursal = sucursalRepo.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));
        } else {
            Object sidObj = body.get("sucursalId");
            if (sidObj == null) throw new BadRequestException("sucursalId es obligatorio");
            Long requestedId = Long.valueOf(sidObj.toString());
            sucursal = sucursalRepo.findById(requestedId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));
        }

        CategoriaEquipo cat;
        if (body.get("categoriaId") != null) {
            Long categoriaId = Long.valueOf(body.get("categoriaId").toString());
            cat = categoriaRepo.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        } else {
            String catNombre = body.get("categoria") != null ? body.get("categoria").toString() : "General";
            cat = categoriaRepo.findByNombre(catNombre).orElseGet(() ->
                categoriaRepo.save(CategoriaEquipo.builder().nombre(catNombre).build()));
        }

        String serie = body.get("numeroSerie") != null ? body.get("numeroSerie").toString() : "SN-" + System.currentTimeMillis();

        Equipo e = Equipo.builder()
            .sucursal(sucursal).categoria(cat)
            .nombre(body.get("nombre") != null ? body.get("nombre").toString() : "Sin nombre")
            .marca(body.get("marca")   != null ? body.get("marca").toString()  : null)
            .modelo(body.get("modelo") != null ? body.get("modelo").toString() : null)
            .descripcion(body.get("descripcion") != null ? body.get("descripcion").toString() : null)
            .imagenUrl(body.get("imagenUrl") != null ? body.get("imagenUrl").toString() : null)
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
        Long sucursalId = securityUtil.getSucursalIdEfectiva();
        if (sucursalId != null && !e.getSucursal().getId().equals(sucursalId)) {
            throw new ForbiddenException("No tienes acceso a equipos de otra sucursal");
        }
        if (body.get("nombre") != null) e.setNombre(body.get("nombre").toString());
        if (body.get("estado") != null) {
            String estFrontend = body.get("estado").toString().toLowerCase();
            e.setEstado(switch (estFrontend) {
                case "disponible" -> Equipo.EstadoEquipo.OPERATIVO;
                case "mantenimiento" -> Equipo.EstadoEquipo.MANTENIMIENTO;
                case "fuera_de_servicio" -> Equipo.EstadoEquipo.DADO_DE_BAJA;
                default -> Equipo.EstadoEquipo.valueOf(estFrontend.toUpperCase());
            });
        }
        if (body.get("descripcion") != null) e.setDescripcion(body.get("descripcion").toString());
        if (body.get("imagenUrl") != null) e.setImagenUrl(body.get("imagenUrl").toString());
        if (body.get("marca") != null) e.setMarca(body.get("marca").toString());
        if (body.get("modelo") != null) e.setModelo(body.get("modelo").toString());
        if (body.get("categoria") != null) {
            String catNombre = body.get("categoria").toString();
            CategoriaEquipo cat = categoriaRepo.findByNombre(catNombre).orElseGet(() ->
                categoriaRepo.save(CategoriaEquipo.builder().nombre(catNombre).build()));
            e.setCategoria(cat);
        }
        return ResponseEntity.ok(ApiResponse.ok(equipoRepo.save(e), "Equipo actualizado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        Equipo e = equipoRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado: " + id));
        Long sucursalId = securityUtil.getSucursalIdEfectiva();
        if (sucursalId != null && !e.getSucursal().getId().equals(sucursalId)) {
            throw new ForbiddenException("No tienes acceso a equipos de otra sucursal");
        }
        e.setEstado(Equipo.EstadoEquipo.DADO_DE_BAJA);
        equipoRepo.save(e);
        return ResponseEntity.ok(ApiResponse.ok(null, "Equipo dado de baja"));
    }
}
