package com.powerfit.service;

import com.powerfit.dto.request.MembresiaRequest;
import com.powerfit.dto.response.MembresiaResponse;
import com.powerfit.dto.response.PagedResponse;
import java.util.List;

public interface MembresiaService {
    List<MembresiaResponse> listarTodas();
    PagedResponse<MembresiaResponse> listarActivasPaginado(int pagina, int tamanio);
    PagedResponse<MembresiaResponse> listarVencidasPaginado(int pagina, int tamanio);
    List<MembresiaResponse> listarActivas();
    List<MembresiaResponse> listarVencidas();
    List<MembresiaResponse> listarPorCliente(Integer clienteId);
    MembresiaResponse asignar(MembresiaRequest request);
    MembresiaResponse renovar(Integer id);
    void eliminar(Integer id);
}
