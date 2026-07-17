package com.powerfit.service;

import com.powerfit.dto.request.UsuarioRequest;
import com.powerfit.dto.response.UsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {
    Page<UsuarioResponse> listarPaginado(Pageable pageable);
    UsuarioResponse buscarPorId(Integer id);
    UsuarioResponse crear(UsuarioRequest request);
    UsuarioResponse actualizar(Integer id, UsuarioRequest request);
    void eliminar(Integer id);
}
