package com.powerfit.service.impl;

import com.powerfit.dto.response.AuditoriaResponse;
import com.powerfit.entity.Auditoria;
import com.powerfit.repository.AuditoriaRepository;
import com.powerfit.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    @Override
    public void registrar(String tabla, String accion, String usuario,
                          String datosAnt, String datosNuevos, String ip) {
        Auditoria a = Auditoria.builder()
                .tablaAfectada(tabla)
                .accion(Auditoria.AccionAuditoria.valueOf(accion))
                .usuario(usuario != null ? usuario : "system")
                .fechaHora(LocalDateTime.now())
                .datosAnteriores(datosAnt)
                .datosNuevos(datosNuevos)
                .ipAddress(ip)
                .build();
        auditoriaRepository.save(a);
    }

    @Override
    public List<AuditoriaResponse> listar(String usuario, String tabla, String accion) {
        return auditoriaRepository.findWithFilters(usuario, tabla, accion)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AuditoriaResponse toResponse(Auditoria a) {
        return AuditoriaResponse.builder()
                .id(a.getId())
                .tablaAfectada(a.getTablaAfectada())
                .accion(a.getAccion().name())
                .usuario(a.getUsuario())
                .fechaHora(a.getFechaHora())
                .datosAnteriores(a.getDatosAnteriores())
                .datosNuevos(a.getDatosNuevos())
                .ipAddress(a.getIpAddress())
                .build();
    }
}
