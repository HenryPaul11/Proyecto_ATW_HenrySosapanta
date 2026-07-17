package com.powerfit.service.impl;

import com.powerfit.dto.request.EquipoRequest;
import com.powerfit.dto.response.EquipoResponse;
import com.powerfit.entity.Equipo;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.EquipoRepository;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.EquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final AuditoriaService  auditoriaService;

    @Override
    public List<EquipoResponse> listarTodos(Integer sucursalId) {
        List<Equipo> lista = sucursalId != null
                ? equipoRepository.findBySucursal(sucursalId)
                : equipoRepository.findAll();
        return lista.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public EquipoResponse buscarPorId(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<EquipoResponse> listarPorCategoria(String categoria) {
        Equipo.Categoria cat = Equipo.Categoria.valueOf(categoria);
        return equipoRepository.findByCategoria(cat).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public EquipoResponse crear(EquipoRequest request) {
        Equipo e = buildFromRequest(new Equipo(), request);
        Equipo saved = equipoRepository.save(e);
        auditoriaService.registrar("equipos", "INSERT", "system",
                null, "id=" + saved.getId(), null);
        return toResponse(saved);
    }

    @Override
    public EquipoResponse actualizar(Integer id, EquipoRequest request) {
        Equipo e = findOrThrow(id);
        String datosAnt = "nombre=" + e.getNombre();
        buildFromRequest(e, request);
        Equipo saved = equipoRepository.save(e);
        auditoriaService.registrar("equipos", "UPDATE", "system",
                datosAnt, "nombre=" + saved.getNombre(), null);
        return toResponse(saved);
    }

    @Override
    public void eliminar(Integer id) {
        findOrThrow(id);
        auditoriaService.registrar("equipos", "DELETE", "system", "id=" + id, null, null);
        equipoRepository.deleteById(id);
    }

    private Equipo findOrThrow(Integer id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con id: " + id));
    }

    private Equipo buildFromRequest(Equipo e, EquipoRequest req) {
        e.setNombre(req.getNombre());
        e.setDescripcion(req.getDescripcion());
        e.setImagenUrl(req.getImagenUrl());
        if (req.getCategoria() != null)
            e.setCategoria(Equipo.Categoria.valueOf(req.getCategoria()));
        if (req.getEstado() != null)
            e.setEstado(Equipo.EstadoEquipo.valueOf(req.getEstado()));
        return e;
    }

    private EquipoResponse toResponse(Equipo e) {
        return EquipoResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .categoria(e.getCategoria() != null ? e.getCategoria().name() : null)
                .descripcion(e.getDescripcion())
                .estado(e.getEstado().name())
                .imagenUrl(e.getImagenUrl())
                .build();
    }
}
