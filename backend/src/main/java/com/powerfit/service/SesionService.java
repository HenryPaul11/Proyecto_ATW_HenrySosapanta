package com.powerfit.service;

import com.powerfit.dto.request.SesionRequest;
import com.powerfit.dto.response.SesionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SesionService {
    // Paginación FÍSICA con filtro lógico opcional por estado
    Page<SesionResponse> listarPaginado(String estado, Pageable pageable);
    Page<SesionResponse> listarPaginadoFiltrado(String estado, String entrenadorEmail, Pageable pageable);
    List<SesionResponse> listarTodas();
    SesionResponse buscarPorId(Integer id);
    List<SesionResponse> listarPorEntrenador(Integer entrenadorId);
    List<SesionResponse> listarProximasPorEntrenador(Integer entrenadorId);
    List<SesionResponse> listarPorCliente(Integer clienteId);
    SesionResponse crear(SesionRequest request);
    SesionResponse actualizar(Integer id, SesionRequest request);
    void eliminar(Integer id);
}
