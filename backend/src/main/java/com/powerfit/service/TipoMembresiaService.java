package com.powerfit.service;

import com.powerfit.dto.request.TipoMembresiaRequest;
import com.powerfit.dto.response.TipoMembresiaResponse;
import java.util.List;

public interface TipoMembresiaService {
    List<TipoMembresiaResponse> listarTodos();
    TipoMembresiaResponse buscarPorId(Integer id);
    TipoMembresiaResponse crear(TipoMembresiaRequest request);
    TipoMembresiaResponse actualizar(Integer id, TipoMembresiaRequest request);
    void eliminar(Integer id);
}
