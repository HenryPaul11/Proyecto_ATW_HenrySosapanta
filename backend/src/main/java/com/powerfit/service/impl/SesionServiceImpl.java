package com.powerfit.service.impl;

import com.powerfit.dto.request.SesionRequest;
import com.powerfit.dto.response.SesionResponse;
import com.powerfit.entity.*;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.*;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.SesionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SesionServiceImpl implements SesionService {

    private final SesionRepository     sesionRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final ClienteRepository    clienteRepository;
    private final AuditoriaService     auditoriaService;

    // Paginación FÍSICA + filtro LÓGICO por estado (híbrida)
    @Override
    public Page<SesionResponse> listarPaginado(String estado, Pageable pageable) {
        Sesion.EstadoSesion estadoEnum = (estado != null && !estado.isBlank())
                ? Sesion.EstadoSesion.valueOf(estado)
                : null;
        return sesionRepository.findByEstadoOptional(estadoEnum, pageable)
                .map(this::toResponse);
    }

    @Override
    public List<SesionResponse> listarTodas() {
        return sesionRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public SesionResponse buscarPorId(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<SesionResponse> listarPorEntrenador(Integer entrenadorId) {
        return sesionRepository.findByEntrenadorId(entrenadorId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<SesionResponse> listarPorCliente(Integer clienteId) {
        return sesionRepository.findByClienteId(clienteId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public SesionResponse crear(SesionRequest request) {
        Sesion s = buildFromRequest(new Sesion(), request);
        Sesion saved = sesionRepository.save(s);
        auditoriaService.registrar("sesiones", "INSERT", "system",
                null, "id=" + saved.getId(), null);
        return toResponse(saved);
    }

    @Override
    public SesionResponse actualizar(Integer id, SesionRequest request) {
        Sesion s = findOrThrow(id);
        String datosAnt = "estado=" + s.getEstado();
        buildFromRequest(s, request);
        Sesion saved = sesionRepository.save(s);
        auditoriaService.registrar("sesiones", "UPDATE", "system",
                datosAnt, "estado=" + saved.getEstado(), null);
        return toResponse(saved);
    }

    @Override
    public void eliminar(Integer id) {
        findOrThrow(id);
        auditoriaService.registrar("sesiones", "DELETE", "system", "id=" + id, null, null);
        sesionRepository.deleteById(id);
    }

    private Sesion findOrThrow(Integer id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada con id: " + id));
    }

    private Sesion buildFromRequest(Sesion s, SesionRequest req) {
        Entrenador entrenador = entrenadorRepository.findById(req.getEntrenadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado: " + req.getEntrenadorId()));
        Cliente cliente = clienteRepository.findById(req.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + req.getClienteId()));

        s.setEntrenador(entrenador);
        s.setCliente(cliente);
        s.setFecha(req.getFecha());
        s.setHora(req.getHora());
        s.setTipo(req.getTipo());
        s.setNotas(req.getNotas());
        if (req.getEstado() != null) {
            s.setEstado(Sesion.EstadoSesion.valueOf(req.getEstado()));
        } else if (s.getEstado() == null) {
            s.setEstado(Sesion.EstadoSesion.pendiente);
        }
        return s;
    }

    private SesionResponse toResponse(Sesion s) {
        return SesionResponse.builder()
                .id(s.getId())
                .entrenadorId(s.getEntrenador().getId())
                .entrenadorNombre(s.getEntrenador().getNombre() + " " + s.getEntrenador().getApellido())
                .clienteId(s.getCliente().getId())
                .clienteNombre(s.getCliente().getNombre() + " " + s.getCliente().getApellido())
                .fecha(s.getFecha())
                .hora(s.getHora())
                .tipo(s.getTipo())
                .estado(s.getEstado().name())
                .notas(s.getNotas())
                .build();
    }
}
