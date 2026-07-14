package com.powerfit.service;

import com.powerfit.dto.request.EquipoRequest;
import com.powerfit.dto.response.EquipoResponse;
import java.util.List;

public interface EquipoService {
    List<EquipoResponse> listarTodos();
    EquipoResponse buscarPorId(Integer id);
    List<EquipoResponse> listarPorCategoria(String categoria);
    EquipoResponse crear(EquipoRequest request);
    EquipoResponse actualizar(Integer id, EquipoRequest request);
    void eliminar(Integer id);
}
