package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.Auditoria;
import com.powerfit.repository.AuditoriaRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
@Tag(name = "Auditorías")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepo;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Auditoria>>> listar(
            @RequestParam(required = false) Long   sucursalId,
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String usuario) {
        return ResponseEntity.ok(ApiResponse.ok(
            auditoriaRepo.findFiltered(sucursalId, tabla, accion, usuario)));
    }
}
