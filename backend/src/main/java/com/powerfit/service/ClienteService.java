package com.powerfit.service;

import com.powerfit.dto.request.ClienteRequest;
import com.powerfit.dto.response.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ClienteService {
    // Con paginación
    Page<ClienteResponse> listarPaginado(String busqueda, Pageable pageable);

    // Sin paginación (compatibilidad)
    List<ClienteResponse> listarTodos();

    ClienteResponse buscarPorId(Integer id);
    ClienteResponse buscarPorCedula(String cedula);
    List<ClienteResponse> sinMembresia();
    ClienteResponse crear(ClienteRequest request);
    ClienteResponse actualizar(Integer id, ClienteRequest request);

    // Eliminación lógica (activo = false)
    void eliminar(Integer id);
}
