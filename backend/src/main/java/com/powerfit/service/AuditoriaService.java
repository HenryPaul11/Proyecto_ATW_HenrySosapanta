package com.powerfit.service;

public interface AuditoriaService {
    void registrar(String tabla, String accion, String usuario, String datosAnteriores, String datosNuevos, String ip);
}
