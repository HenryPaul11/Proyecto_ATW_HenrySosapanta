package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.*;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos")
public class PagoController {

    private final PagoRepository      pagoRepo;
    private final MembresiaRepository membresiaRepo;
    private final ClienteRepository   clienteRepo;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Pago>>> listar(
            @RequestParam(defaultValue = "0")  int  page,
            @RequestParam(defaultValue = "20") int  size,
            @RequestParam(required = false)    Long sucursalId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaPago").descending());
        return ResponseEntity.ok(ApiResponse.ok(pagoRepo.findBySucursal(sucursalId, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Pago>> porId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(pagoRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id))));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<Pago>>> porCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ApiResponse.ok(pagoRepo.findByClienteId(clienteId)));
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> estadisticas(
            @RequestParam(required = false) Long sucursalId) {
        BigDecimal total = pagoRepo.sumIngresos(sucursalId);
        BigDecimal prom  = pagoRepo.avgMonto();
        long count       = pagoRepo.count();
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
            "totalIngresos", total != null ? total : BigDecimal.ZERO,
            "totalPagos",    count,
            "promedio",      prom  != null ? prom  : BigDecimal.ZERO
        )));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Pago>> registrar(@RequestBody Map<String, Object> body) {
        Long membresiaId = Long.valueOf(body.get("membresiaId").toString());
        Membresia memb   = membresiaRepo.findById(membresiaId)
            .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada"));

        Pago p = Pago.builder()
            .membresia(memb)
            .cliente(memb.getCliente())
            .sucursal(memb.getSucursal())
            .monto(new BigDecimal(body.get("monto").toString()))
            .metodoPago(Pago.MetodoPago.valueOf(body.get("metodoPago").toString().toUpperCase()))
            .estado(Pago.EstadoPago.COMPLETADO)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(pagoRepo.save(p), "Pago registrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        pagoRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
        pagoRepo.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Pago eliminado"));
    }
}
