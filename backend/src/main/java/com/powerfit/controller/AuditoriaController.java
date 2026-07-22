package com.powerfit.controller;

import com.powerfit.dto.ApiResponse;
import com.powerfit.entity.Auditoria;
import com.powerfit.repository.AuditoriaRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
@Tag(name = "Auditorías")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepo;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Auditoria>>> listar(
            @RequestParam(required = false) Long   sucursalId,
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String usuario) {

        log.info("Listando auditorías: sucursalId={}, tabla={}, accion={}, usuario={}", sucursalId, tabla, accion, usuario);

        List<Auditoria> lista = auditoriaRepo.findAllOrdered();

        if (sucursalId != null) {
            lista = lista.stream()
                .filter(a -> a.getSucursal() != null && sucursalId.equals(a.getSucursal().getId()))
                .toList();
        }
        if (tabla != null && !tabla.isBlank()) {
            lista = lista.stream()
                .filter(a -> tabla.equalsIgnoreCase(a.getTablaAfectada()))
                .toList();
        }
        if (accion != null && !accion.isBlank()) {
            lista = lista.stream()
                .filter(a -> accion.equalsIgnoreCase(String.valueOf(a.getAccion())))
                .toList();
        }
        if (usuario != null && !usuario.isBlank()) {
            lista = lista.stream()
                .filter(a -> a.getUsuario() != null
                    && a.getUsuario().getNombreCompleto() != null
                    && a.getUsuario().getNombreCompleto().toLowerCase().contains(usuario.toLowerCase()))
                .toList();
        }

        for (Auditoria a : lista) {
            if (a.getUsuario() != null) a.getUsuario().getNombreCompleto();
            if (a.getSucursal() != null) a.getSucursal().getNombre();
        }
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }
}
