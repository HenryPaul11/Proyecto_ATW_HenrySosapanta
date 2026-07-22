package com.powerfit.service;

public interface AuditoriaService {
    void registrar(String tabla, String accion, String usuarioEmail, String datosAnteriores, String datosNuevos, String ip);
    void registrarConUsuario(String tabla, String accion, Long usuarioId, Long sucursalId, String datosAnteriores, String datosNuevos, String ip);
}
