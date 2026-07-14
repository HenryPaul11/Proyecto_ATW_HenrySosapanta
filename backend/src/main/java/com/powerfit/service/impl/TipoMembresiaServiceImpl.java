package com.powerfit.service.impl;

import com.powerfit.dto.request.TipoMembresiaRequest;
import com.powerfit.dto.response.TipoMembresiaResponse;
import com.powerfit.entity.TipoMembresia;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.TipoMembresiaRepository;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.TipoMembresiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoMembresiaServiceImpl implements TipoMembresiaService {

    private final TipoMembresiaRepository tipoRepository;
    private final AuditoriaService         auditoriaService;

    @Override
    public List<TipoMembresiaResponse> listarTodos() {
        return tipoRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public TipoMembresiaResponse buscarPorId(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public TipoMembresiaResponse crear(TipoMembresiaRequest req) {
        TipoMembresia t = TipoMembresia.builder()
                .nombre(req.getNombre())
                .descripcion(req.getDescripcion())
                .duracionDias(req.getDuracionDias())
                .precio(req.getPrecio())
                .activo(req.getActivo() != null ? req.getActivo() : true)
                .build();
        TipoMembresia saved = tipoRepository.save(t);
        auditoriaService.registrar("tipos_membresia", "INSERT", "system",
                null, "id=" + saved.getId(), null);
        return toResponse(saved);
    }

    @Override
    public TipoMembresiaResponse actualizar(Integer id, TipoMembresiaRequest req) {
        TipoMembresia t = findOrThrow(id);
        String datosAnt = "nombre=" + t.getNombre();
        t.setNombre(req.getNombre());
        t.setDescripcion(req.getDescripcion());
        t.setDuracionDias(req.getDuracionDias());
        t.setPrecio(req.getPrecio());
        if (req.getActivo() != null) t.setActivo(req.getActivo());
        TipoMembresia saved = tipoRepository.save(t);
        auditoriaService.registrar("tipos_membresia", "UPDATE", "system",
                datosAnt, "nombre=" + saved.getNombre(), null);
        return toResponse(saved);
    }

    @Override
    public void eliminar(Integer id) {
        findOrThrow(id);
        auditoriaService.registrar("tipos_membresia", "DELETE", "system",
                "id=" + id, null, null);
        tipoRepository.deleteById(id);
    }

    private TipoMembresia findOrThrow(Integer id) {
        return tipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de membresía no encontrado con id: " + id));
    }

    private TipoMembresiaResponse toResponse(TipoMembresia t) {
        return TipoMembresiaResponse.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .duracionDias(t.getDuracionDias())
                .precio(t.getPrecio())
                .activo(t.getActivo())
                .build();
    }
}
