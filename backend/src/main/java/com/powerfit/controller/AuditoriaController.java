package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.dto.response.AuditoriaResponse;
import com.powerfit.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
@Tag(name = "Auditorías", description = "Registro de auditoría del sistema")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    @Operation(summary = "Listar auditorías con filtros opcionales (usuario, tabla, accion)")
    public ResponseEntity<ApiResponse<List<AuditoriaResponse>>> listar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String accion) {
        return ResponseEntity.ok(ApiResponse.ok(auditoriaService.listar(usuario, tabla, accion)));
    }
}
