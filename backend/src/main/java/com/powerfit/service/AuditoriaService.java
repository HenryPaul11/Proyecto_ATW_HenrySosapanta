package com.powerfit.service;

import com.powerfit.dto.response.AuditoriaResponse;
import java.util.List;

public interface AuditoriaService {
    void registrar(String tabla, String accion, String usuario, String datosAnt, String datosNuevos, String ip);
    List<AuditoriaResponse> listar(String usuario, String tabla, String accion);
}
