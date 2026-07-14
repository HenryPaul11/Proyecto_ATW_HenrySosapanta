package com.powerfit.service;

import com.powerfit.dto.request.SucursalRequest;
import com.powerfit.dto.response.SucursalResponse;
import java.util.List;

public interface SucursalService {
    List<SucursalResponse> listarTodas();
    SucursalResponse buscarPorId(Integer id);
    SucursalResponse crear(SucursalRequest request);
    SucursalResponse actualizar(Integer id, SucursalRequest request);
    void eliminar(Integer id);   // lógico
}
