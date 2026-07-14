package com.powerfit.service;

import com.powerfit.dto.request.EntrenadorRequest;
import com.powerfit.dto.response.ClienteResponse;
import com.powerfit.dto.response.EntrenadorResponse;
import com.powerfit.dto.response.SesionResponse;
import java.util.List;

public interface EntrenadorService {
    List<EntrenadorResponse> listarTodos();
    EntrenadorResponse buscarPorId(Integer id);
    EntrenadorResponse buscarPorUsername(String username);
    List<ClienteResponse> clientesDeEntrenador(Integer id);
    List<SesionResponse> sesionesDeEntrenador(Integer id);
    EntrenadorResponse crear(EntrenadorRequest request);
    EntrenadorResponse actualizar(Integer id, EntrenadorRequest request);
    void eliminar(Integer id);
}
