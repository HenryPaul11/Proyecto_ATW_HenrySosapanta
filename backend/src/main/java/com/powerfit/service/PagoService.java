package com.powerfit.service;

import com.powerfit.dto.request.PagoRequest;
import com.powerfit.dto.response.PagoEstadisticasResponse;
import com.powerfit.dto.response.PagoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PagoService {
    Page<PagoResponse> listarPaginado(Pageable pageable);
    List<PagoResponse> listarTodos();
    PagoResponse buscarPorId(Integer id);
    List<PagoResponse> listarPorCliente(Integer clienteId);
    PagoEstadisticasResponse estadisticas();
    PagoResponse registrar(PagoRequest request);
    void eliminar(Integer id);
}
