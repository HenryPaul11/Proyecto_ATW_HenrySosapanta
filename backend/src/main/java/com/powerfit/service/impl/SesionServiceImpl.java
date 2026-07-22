package com.powerfit.service.impl;

import com.powerfit.dto.request.SesionRequest;
import com.powerfit.dto.response.SesionResponse;
import com.powerfit.entity.Cliente;
import com.powerfit.entity.Entrenador;
import com.powerfit.entity.Sesion;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.ClienteRepository;
import com.powerfit.repository.EntrenadorRepository;
import com.powerfit.repository.SesionRepository;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.SesionService;
import com.powerfit.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SesionServiceImpl implements SesionService {

    private final SesionRepository sesionRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;
    private final SecurityUtil securityUtil;

    @Override
    public Page<SesionResponse> listarPaginado(String estado, Pageable pageable) {
        Sesion.EstadoGeneral estadoEnum = estado != null && !estado.isBlank()
                ? Sesion.EstadoGeneral.valueOf(estado.toUpperCase())
                : null;
        Page<Sesion> page = estadoEnum != null
                ? sesionRepository.findByEstadoWithJoins(estadoEnum, pageable)
                : sesionRepository.findAllWithJoins(pageable);
        return page.map(this::toResponse);
    }

    @Override
    public Page<SesionResponse> listarPaginadoFiltrado(String estado, String entrenadorEmail, Pageable pageable) {
        Sesion.EstadoGeneral estadoEnum = estado != null && !estado.isBlank()
                ? Sesion.EstadoGeneral.valueOf(estado.toUpperCase())
                : null;

        if (entrenadorEmail != null && !entrenadorEmail.isBlank()) {
            java.util.Optional<com.powerfit.entity.Entrenador> entrenadorOpt =
                    entrenadorRepository.findByEmail(entrenadorEmail);
            if (entrenadorOpt.isPresent()) {
                Long entrenadorId = entrenadorOpt.get().getId();
                Page<Sesion> page = estadoEnum != null
                        ? sesionRepository.findByEntrenadorIdAndEstado(entrenadorId, estadoEnum, pageable)
                        : sesionRepository.findByEntrenadorIdWithJoins(entrenadorId, pageable);
                return page.map(this::toResponse);
            }
            // Si no se encuentra el entrenador, devolver página vacía
            return Page.empty(pageable);
        }

        // Sin filtro de entrenador, usar la lógica original
        return listarPaginado(estado, pageable);
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
    public List<SesionResponse> listarProximasPorEntrenador(Integer entrenadorId) {
        return sesionRepository.findProximasByEntrenadorId(entrenadorId.longValue(), java.time.LocalDate.now()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<SesionResponse> listarPorCliente(Integer clienteId) {
        return List.of();
    }

    @Override
    public SesionResponse crear(SesionRequest request) {
        log.info("Registrando nueva Sesion");
        Sesion sesion = buildFromRequest(new Sesion(), request);
        Sesion saved = sesionRepository.save(sesion);
        try {
            auditoriaService.registrarConUsuario("sesiones", "INSERT",
                    securityUtil.getUsuarioId(), securityUtil.getSucursalIdEfectiva(),
                    null, "{\"id\":" + saved.getId() + "}", null);
        } catch (Exception ignored) {}
        return toResponse(saved);
    }

    @Override
    public SesionResponse actualizar(Integer id, SesionRequest request) {
        log.info("Actualizando Sesion id={}", id);
        Sesion sesion = findOrThrow(id);
        String datosAnteriores = "{\"estado\":\"" + sesion.getEstado() + "\"}";
        Sesion saved = sesionRepository.save(buildFromRequest(sesion, request));
        try {
            auditoriaService.registrarConUsuario("sesiones", "UPDATE",
                    securityUtil.getUsuarioId(), securityUtil.getSucursalIdEfectiva(),
                    datosAnteriores, "{\"estado\":\"" + saved.getEstado() + "\"}", null);
        } catch (Exception ignored) {}
        return toResponse(saved);
    }

    @Override
    public void eliminar(Integer id) {
        log.info("Eliminando Sesion id={}", id);
        Sesion sesion = findOrThrow(id);
        sesion.setEstado(Sesion.EstadoGeneral.INACTIVO);
        sesionRepository.save(sesion);
        try {
            auditoriaService.registrarConUsuario("sesiones", "DELETE",
                    securityUtil.getUsuarioId(), securityUtil.getSucursalIdEfectiva(),
                    "{\"id\":" + id + "}", null, null);
        } catch (Exception ignored) {}
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

        if (request.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(request.getClienteId().longValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + request.getClienteId()));
            sesion.setCliente(cliente);
        }

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
        SesionResponse.SesionResponseBuilder builder = SesionResponse.builder()
                .id(sesion.getId())
                .entrenadorId(sesion.getEntrenador().getId())
                .entrenadorNombre(sesion.getEntrenador().getNombreCompleto())
                .fecha(sesion.getFecha())
                .hora(sesion.getHoraInicio())
                .tipo(sesion.getTipo().name())
                .estado(sesion.getEstado().name());

        if (sesion.getCliente() != null) {
            builder.clienteId(sesion.getCliente().getId())
                   .clienteNombre(sesion.getCliente().getNombreCompleto());
        }

        return builder.build();
    }
}
