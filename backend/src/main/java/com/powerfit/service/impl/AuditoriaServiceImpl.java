package com.powerfit.service.impl;

import com.powerfit.entity.Auditoria;
import com.powerfit.repository.AuditoriaRepository;
import com.powerfit.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    @Override
    public void registrar(String tabla, String accion, String usuario, String datosAnteriores, String datosNuevos, String ip) {
        Auditoria auditoria = Auditoria.builder()
                .tablaAfectada(tabla)
                .accion(Auditoria.AccionAuditoria.valueOf(accion.toUpperCase()))
                .datosAnteriores(datosAnteriores)
                .datosNuevos(datosNuevos)
                .ipOrigen(ip)
                .build();
        auditoriaRepository.save(auditoria);
    }
}
