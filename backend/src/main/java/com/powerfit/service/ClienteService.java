package com.powerfit.service;

import com.powerfit.dto.request.ClienteRequest;
import com.powerfit.dto.response.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ClienteService {
    Page<ClienteResponse> listarPaginado(String busqueda, Integer sucursalId, Pageable pageable);
    List<ClienteResponse> listarTodos();
    ClienteResponse buscarPorId(Integer id);
    ClienteResponse buscarPorCedula(String cedula);
    List<ClienteResponse> sinMembresia(Integer sucursalId);
    ClienteResponse crear(ClienteRequest request);
    ClienteResponse actualizar(Integer id, ClienteRequest request);
    void eliminar(Integer id);
}
