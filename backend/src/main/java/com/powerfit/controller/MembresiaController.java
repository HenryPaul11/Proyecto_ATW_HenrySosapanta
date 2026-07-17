package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.*;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membresias")
@RequiredArgsConstructor
@Tag(name = "Membresías")
public class MembresiaController {

    private final MembresiaRepository membresiaRepo;
    private final ClienteRepository   clienteRepo;
    private final PlanRepository      planRepo;
    private final SucursalRepository  sucursalRepo;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Membresia>>> listar(
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "10") int    size,
            @RequestParam(required = false)    Long   sucursalId,
            @RequestParam(required = false)    String estado) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(membresiaRepo.findFiltered(sucursalId, estado, pageable)));
    }

    @GetMapping("/activas")
    public ResponseEntity<ApiResponse<Page<Membresia>>> activas(
            @RequestParam(defaultValue = "0")  int  page,
            @RequestParam(defaultValue = "10") int  size,
            @RequestParam(required = false)    Long sucursalId) {
        return ResponseEntity.ok(ApiResponse.ok(
            membresiaRepo.findFiltered(sucursalId, "ACTIVA", PageRequest.of(page, size))));
    }

    @GetMapping("/vencidas")
    public ResponseEntity<ApiResponse<Page<Membresia>>> vencidas(
            @RequestParam(defaultValue = "0")  int  page,
            @RequestParam(defaultValue = "10") int  size,
            @RequestParam(required = false)    Long sucursalId) {
        return ResponseEntity.ok(ApiResponse.ok(
            membresiaRepo.findFiltered(sucursalId, "VENCIDA", PageRequest.of(page, size))));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<Membresia>>> porCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ApiResponse.ok(membresiaRepo.findByClienteId(clienteId)));
    }

    @PostMapping("/asignar")
    public ResponseEntity<ApiResponse<Membresia>> asignar(@RequestBody Map<String, Object> body) {
        Long clienteId = Long.valueOf(body.get("clienteId").toString());
        Long planId    = Long.valueOf(body.get("planId").toString());

        Cliente cliente = clienteRepo.findById(clienteId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        Plan plan = planRepo.findById(planId)
            .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        LocalDate inicio = LocalDate.now();
        LocalDate fin    = inicio.plusDays(plan.getDuracionDias());

        Membresia m = Membresia.builder()
            .cliente(cliente).plan(plan)
            .sucursal(cliente.getSucursal())
            .fechaInicio(inicio).fechaFin(fin)
            .estado(Membresia.EstadoMembresia.ACTIVA)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(membresiaRepo.save(m), "Membresía asignada"));
    }

    @PutMapping("/{id}/renovar")
    public ResponseEntity<ApiResponse<Membresia>> renovar(@PathVariable Long id,
                                                           @RequestBody Map<String, Object> body) {
        Membresia m = membresiaRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada: " + id));
        Long planId = body.get("planId") != null ? Long.valueOf(body.get("planId").toString()) : m.getPlan().getId();
        Plan plan   = planRepo.findById(planId).orElse(m.getPlan());
        LocalDate inicio = LocalDate.now().isAfter(m.getFechaFin()) ? LocalDate.now() : m.getFechaFin().plusDays(1);
        m.setPlan(plan);
        m.setFechaInicio(inicio);
        m.setFechaFin(inicio.plusDays(plan.getDuracionDias()));
        m.setEstado(Membresia.EstadoMembresia.ACTIVA);
        return ResponseEntity.ok(ApiResponse.ok(membresiaRepo.save(m), "Membresía renovada"));
    }
}
