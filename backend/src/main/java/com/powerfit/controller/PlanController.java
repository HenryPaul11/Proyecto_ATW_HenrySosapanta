package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.Plan;
import com.powerfit.entity.Sucursal;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.PlanRepository;
import com.powerfit.repository.SucursalRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Planes / Tipos de Membresía")
public class PlanController {

    private final PlanRepository     planRepo;
    private final SucursalRepository sucursalRepo;

    // ── Ruta principal ──────────────────────────────────────────────────────
    @GetMapping("/api/planes")
    public ResponseEntity<ApiResponse<List<Plan>>> listarPlanes(
            @RequestParam(required = false) Long sucursalId) {
        List<Plan> lista = sucursalId != null
            ? planRepo.findBySucursalId(sucursalId)
            : planRepo.findAll();
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    // ── Alias para compatibilidad frontend (aún usa /tipos-membresia) ───────
    @GetMapping("/api/tipos-membresia")
    public ResponseEntity<ApiResponse<List<Plan>>> listarTipos(
            @RequestParam(required = false) Long sucursalId) {
        List<Plan> lista = sucursalId != null
            ? planRepo.findBySucursalId(sucursalId)
            : planRepo.findAll();
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @GetMapping("/api/planes/{id}")
    public ResponseEntity<ApiResponse<Plan>> porId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(planRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado: " + id))));
    }

    @PostMapping("/api/planes")
    public ResponseEntity<ApiResponse<Plan>> crear(@RequestBody Map<String, Object> body) {
        Object sidObj = body.get("sucursalId");
        if (sidObj == null) throw new BadRequestException("sucursalId es obligatorio");
        Sucursal sucursal = sucursalRepo.findById(Long.valueOf(sidObj.toString()))
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

        Plan p = Plan.builder()
            .sucursal(sucursal)
            .nombrePlan(body.get("nombre") != null ? body.get("nombre").toString()
                      : body.get("nombrePlan") != null ? body.get("nombrePlan").toString() : "Plan")
            .descripcion(body.get("descripcion") != null ? body.get("descripcion").toString() : null)
            .duracionDias(body.get("duracionDias") != null ? Integer.valueOf(body.get("duracionDias").toString())
                        : body.get("duracion_dias") != null ? Integer.valueOf(body.get("duracion_dias").toString()) : 30)
            .precio(body.get("precio") != null ? new BigDecimal(body.get("precio").toString()) : BigDecimal.ZERO)
            .estado(Plan.EstadoGeneral.ACTIVO)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(planRepo.save(p), "Plan creado"));
    }

    @PutMapping("/api/planes/{id}")
    public ResponseEntity<ApiResponse<Plan>> actualizar(@PathVariable Long id,
                                                          @RequestBody Map<String, Object> body) {
        Plan p = planRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado: " + id));
        if (body.get("nombrePlan") != null) p.setNombrePlan(body.get("nombrePlan").toString());
        if (body.get("nombre")     != null) p.setNombrePlan(body.get("nombre").toString());
        if (body.get("precio")     != null) p.setPrecio(new BigDecimal(body.get("precio").toString()));
        return ResponseEntity.ok(ApiResponse.ok(planRepo.save(p), "Plan actualizado"));
    }

    @DeleteMapping("/api/planes/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        Plan p = planRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado: " + id));
        p.setEstado(Plan.EstadoGeneral.INACTIVO);
        planRepo.save(p);
        return ResponseEntity.ok(ApiResponse.ok(null, "Plan desactivado"));
    }
}
