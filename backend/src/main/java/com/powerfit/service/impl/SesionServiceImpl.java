package com.powerfit.service.impl;

import com.powerfit.dto.request.SesionRequest;
import com.powerfit.dto.response.SesionResponse;
import com.powerfit.entity.Entrenador;
import com.powerfit.entity.Sesion;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.EntrenadorRepository;
import com.powerfit.repository.SesionRepository;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.SesionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SesionServiceImpl implements SesionService {

    private final SesionRepository sesionRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final AuditoriaService auditoriaService;

    @Override
    public Page<SesionResponse> listarPaginado(String estado, Pageable pageable) {
        Sesion.EstadoGeneral estadoEnum = estado != null && !estado.isBlank()
                ? Sesion.EstadoGeneral.valueOf(estado.toUpperCase())
                : null;
        return sesionRepository.findByEstadoOptional(estadoEnum, pageable).map(this::toResponse);
    }

    @Override
    public List<SesionResponse> listarTodas() {
        return sesionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public SesionResponse buscarPorId(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<SesionResponse> listarPorEntrenador(Integer entrenadorId) {
        return sesionRepository.findByEntrenadorId(entrenadorId.longValue()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<SesionResponse> listarPorCliente(Integer clienteId) {
        return List.of();
    }

    @Override
    public SesionResponse crear(SesionRequest request) {
        Sesion sesion = buildFromRequest(new Sesion(), request);
        Sesion saved = sesionRepository.save(sesion);
        auditoriaService.registrar("sesiones", "INSERT", "system", null, "{\"id\":" + saved.getId() + "}", null);
        return toResponse(saved);
    }

    @Override
    public SesionResponse actualizar(Integer id, SesionRequest request) {
        Sesion sesion = findOrThrow(id);
        String datosAnteriores = "{\"estado\":\"" + sesion.getEstado() + "\"}";
        Sesion saved = sesionRepository.save(buildFromRequest(sesion, request));
        auditoriaService.registrar("sesiones", "UPDATE", "system", datosAnteriores, "{\"estado\":\"" + saved.getEstado() + "\"}", null);
        return toResponse(saved);
    }

    @Override
    public void eliminar(Integer id) {
        Sesion sesion = findOrThrow(id);
        sesion.setEstado(Sesion.EstadoGeneral.INACTIVO);
        sesionRepository.save(sesion);
        auditoriaService.registrar("sesiones", "DELETE", "system", "{\"id\":" + id + "}", null, null);
    }

    private Sesion findOrThrow(Integer id) {
        return sesionRepository.findById(id.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Sesion no encontrada con id: " + id));
    }

    private Sesion buildFromRequest(Sesion sesion, SesionRequest request) {
        Entrenador entrenador = entrenadorRepository.findById(request.getEntrenadorId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado: " + request.getEntrenadorId()));

        sesion.setEntrenador(entrenador);
        sesion.setSucursal(entrenador.getSucursal());
        sesion.setNombre(request.getTipo() != null && !request.getTipo().isBlank() ? request.getTipo() : "Sesion");
        sesion.setTipo(toTipo(request.getTipo()));
        sesion.setFecha(request.getFecha());
        sesion.setHoraInicio(request.getHora());
        sesion.setHoraFin(request.getHora().plusHours(1));
        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            sesion.setEstado(toEstado(request.getEstado()));
        } else if (sesion.getEstado() == null) {
            sesion.setEstado(Sesion.EstadoGeneral.ACTIVO);
        }
        return sesion;
    }

    private Sesion.TipoSesion toTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return Sesion.TipoSesion.CLASE_GRUPAL;
        }
        return Sesion.TipoSesion.valueOf(tipo.toUpperCase());
    }

    private Sesion.EstadoGeneral toEstado(String estado) {
        return switch (estado.toLowerCase()) {
            case "cancelada", "inactivo", "inactiva" -> Sesion.EstadoGeneral.INACTIVO;
            default -> Sesion.EstadoGeneral.ACTIVO;
        };
    }

    private SesionResponse toResponse(Sesion sesion) {
        return SesionResponse.builder()
                .id(sesion.getId())
                .entrenadorId(sesion.getEntrenador().getId())
                .entrenadorNombre(sesion.getEntrenador().getNombreCompleto())
                .fecha(sesion.getFecha())
                .hora(sesion.getHoraInicio())
                .tipo(sesion.getTipo().name())
                .estado(sesion.getEstado().name())
                .build();
    }
}
