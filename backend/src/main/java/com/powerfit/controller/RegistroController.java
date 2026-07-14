package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.request.AsignarMembresiaConPagoRequest;
import com.powerfit.dto.response.AsignarMembresiaConPagoResponse;
import com.powerfit.service.RegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registro")
@RequiredArgsConstructor
@Tag(name = "Registro Transaccional",
     description = "Procesos que afectan múltiples tablas con @Transactional")
public class RegistroController {

    private final RegistroService registroService;

    @PostMapping("/membresia-con-pago")
    @Operation(
        summary = "Asignar membresía y registrar pago en una sola transacción",
        description = """
            Proceso @Transactional que:
            1. Valida cliente y tipo de membresía
            2. Crea la membresía (tabla membresias)
            3. Registra el pago (tabla pagos)
            4. Guarda auditoría (tabla auditoria)
            Si cualquier paso falla → rollback completo, ningún dato queda guardado.
            """
    )
    public ResponseEntity<ApiResponse<AsignarMembresiaConPagoResponse>> asignarMembresiaConPago(
            @Valid @RequestBody AsignarMembresiaConPagoRequest request) {

        AsignarMembresiaConPagoResponse response =
                registroService.asignarMembresiaConPago(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response,
                        "Membresía asignada y pago registrado correctamente"));
    }
}
