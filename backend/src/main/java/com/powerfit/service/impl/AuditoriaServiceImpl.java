package com.powerfit.service.impl;

import com.powerfit.entity.Auditoria;
import com.powerfit.entity.Sucursal;
import com.powerfit.entity.Usuario;
import com.powerfit.repository.AuditoriaRepository;
import com.powerfit.repository.SucursalRepository;
import com.powerfit.repository.UsuarioRepository;
import com.powerfit.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;

    @Override
    public void registrar(String tabla, String accion, String usuarioEmail, String datosAnteriores, String datosNuevos, String ip) {
        Auditoria auditoria = Auditoria.builder()
                .tablaAfectada(tabla)
                .accion(Auditoria.AccionAuditoria.valueOf(accion.toUpperCase()))
                .datosAnteriores(datosAnteriores)
                .datosNuevos(datosNuevos)
                .ipOrigen(ip)
                .build();

        if (usuarioEmail != null && !usuarioEmail.isBlank()) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(usuarioEmail);
            usuarioOpt.ifPresent(auditoria::setUsuario);
        }

        auditoriaRepository.save(auditoria);
    }

    @Override
    public void registrarConUsuario(String tabla, String accion, Long usuarioId, Long sucursalId, String datosAnteriores, String datosNuevos, String ip) {
        Auditoria auditoria = Auditoria.builder()
                .tablaAfectada(tabla)
                .accion(Auditoria.AccionAuditoria.valueOf(accion.toUpperCase()))
                .datosAnteriores(datosAnteriores)
                .datosNuevos(datosNuevos)
                .ipOrigen(ip)
                .build();

        if (usuarioId != null) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            usuarioOpt.ifPresent(auditoria::setUsuario);
        }

        if (sucursalId != null) {
            Optional<Sucursal> sucursalOpt = sucursalRepository.findById(sucursalId);
            sucursalOpt.ifPresent(auditoria::setSucursal);
        }

        auditoriaRepository.save(auditoria);
    }
}
