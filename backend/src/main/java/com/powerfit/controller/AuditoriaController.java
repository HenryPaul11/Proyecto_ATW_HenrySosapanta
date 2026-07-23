package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.Auditoria;
import com.powerfit.repository.AuditoriaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
@Tag(name = "Auditorías")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepo;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Listar auditorías paginadas",
               description = "Parametros: page (0-based), size, sucursalId, tabla, accion, usuario")
    public ResponseEntity<ApiResponse<Page<Auditoria>>> listar(
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "10") int    size,
            @RequestParam(required = false)    Long   sucursalId,
            @RequestParam(required = false)    String tabla,
            @RequestParam(required = false)    String accion,
            @RequestParam(required = false)    String usuario) {

        log.info("Listando auditorías: page={}, size={}, sucursalId={}, tabla={}, accion={}, usuario={}",
                 page, size, sucursalId, tabla, accion, usuario);

        Page<Auditoria> resultado = auditoriaRepo.findPaginated(
            sucursalId,
            tabla,
            accion,
            usuario,
            PageRequest.of(page, size)
        );

        for (Auditoria a : resultado.getContent()) {
            if (a.getUsuario() != null) a.getUsuario().getNombreCompleto();
            if (a.getSucursal() != null) a.getSucursal().getNombre();
        }

        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }
}
