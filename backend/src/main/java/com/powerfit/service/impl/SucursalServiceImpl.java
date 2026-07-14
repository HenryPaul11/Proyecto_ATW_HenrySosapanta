package com.powerfit.service.impl;

import com.powerfit.dto.request.SucursalRequest;
import com.powerfit.dto.response.SucursalResponse;
import com.powerfit.entity.Sucursal;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.SucursalRepository;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final AuditoriaService   auditoriaService;

    @Override
    public List<SucursalResponse> listarTodas() {
        return sucursalRepository.findByActivoTrue()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public SucursalResponse buscarPorId(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public SucursalResponse crear(SucursalRequest request) {
        if (sucursalRepository.existsByNombre(request.getNombre()))
            throw new BadRequestException("Ya existe una sucursal con el nombre: " + request.getNombre());

        Sucursal s = Sucursal.builder()
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .ciudad(request.getCiudad())
                .fechaApertura(request.getFechaApertura())
                .activo(true)
                .build();

        Sucursal saved = sucursalRepository.save(s);
        auditoriaService.registrar("sucursales", "INSERT", "system",
                null, "id=" + saved.getId() + ",nombre=" + saved.getNombre(), null);
        return toResponse(saved);
    }

    @Override
    public SucursalResponse actualizar(Integer id, SucursalRequest request) {
        Sucursal s = findOrThrow(id);

        if (!s.getNombre().equals(request.getNombre()) &&
                sucursalRepository.existsByNombreAndIdNot(request.getNombre(), id))
            throw new BadRequestException("Ya existe una sucursal con el nombre: " + request.getNombre());

        String datosAnt = "nombre=" + s.getNombre();
        s.setNombre(request.getNombre());
        s.setDireccion(request.getDireccion());
        s.setTelefono(request.getTelefono());
        s.setCiudad(request.getCiudad());
        s.setFechaApertura(request.getFechaApertura());
        if (request.getActivo() != null) s.setActivo(request.getActivo());

        Sucursal saved = sucursalRepository.save(s);
        auditoriaService.registrar("sucursales", "UPDATE", "system",
                datosAnt, "nombre=" + saved.getNombre(), null);
        return toResponse(saved);
    }

    @Override
    public void eliminar(Integer id) {
        Sucursal s = findOrThrow(id);
        String datosAnt = "activo=true,nombre=" + s.getNombre();
        s.setActivo(false);
        sucursalRepository.save(s);
        auditoriaService.registrar("sucursales", "DELETE", "system",
                datosAnt, "activo=false,id=" + id, null);
    }

    private Sucursal findOrThrow(Integer id) {
        Sucursal s = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id: " + id));
        if (!s.getActivo())
            throw new ResourceNotFoundException("Sucursal no encontrada con id: " + id);
        return s;
    }

    private SucursalResponse toResponse(Sucursal s) {
        return SucursalResponse.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .direccion(s.getDireccion())
                .telefono(s.getTelefono())
                .ciudad(s.getCiudad())
                .fechaApertura(s.getFechaApertura())
                .activo(s.getActivo())
                .build();
    }
}
