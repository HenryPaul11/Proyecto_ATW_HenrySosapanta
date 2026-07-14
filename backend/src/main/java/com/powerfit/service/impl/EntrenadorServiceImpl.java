package com.powerfit.service.impl;

import com.powerfit.dto.request.EntrenadorRequest;
import com.powerfit.dto.response.ClienteResponse;
import com.powerfit.dto.response.EntrenadorResponse;
import com.powerfit.dto.response.SesionResponse;
import com.powerfit.entity.*;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.*;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.EntrenadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntrenadorServiceImpl implements EntrenadorService {

    private final EntrenadorRepository         entrenadorRepository;
    private final UsuarioSistemaRepository     usuarioRepository;
    private final ClienteEntrenadorRepository  ceRepository;
    private final SesionRepository             sesionRepository;
    private final AuditoriaService             auditoriaService;

    @Override
    public List<EntrenadorResponse> listarTodos() {
        return entrenadorRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public EntrenadorResponse buscarPorId(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public EntrenadorResponse buscarPorUsername(String username) {
        return toResponse(entrenadorRepository.findByUsuarioSistemaUsuario(username)
                .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado con usuario: " + username)));
    }

    @Override
    public List<ClienteResponse> clientesDeEntrenador(Integer id) {
        findOrThrow(id);
        return ceRepository.findByEntrenadorIdAndActivoTrue(id).stream()
                .map(ce -> clienteToResponse(ce.getCliente()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SesionResponse> sesionesDeEntrenador(Integer id) {
        findOrThrow(id);
        return sesionRepository.findByEntrenadorId(id).stream()
                .map(this::sesionToResponse).collect(Collectors.toList());
    }

    @Override
    public EntrenadorResponse crear(EntrenadorRequest request) {
        if (entrenadorRepository.existsByCedula(request.getCedula()))
            throw new BadRequestException("La cédula " + request.getCedula() + " ya está registrada");

        Entrenador e = buildFromRequest(new Entrenador(), request);
        Entrenador saved = entrenadorRepository.save(e);
        auditoriaService.registrar("entrenadores", "INSERT", "system",
                null, "id=" + saved.getId(), null);
        return toResponse(saved);
    }

    @Override
    public EntrenadorResponse actualizar(Integer id, EntrenadorRequest request) {
        Entrenador e = findOrThrow(id);
        if (!e.getCedula().equals(request.getCedula()) &&
                entrenadorRepository.existsByCedulaAndIdNot(request.getCedula(), id))
            throw new BadRequestException("La cédula " + request.getCedula() + " ya está registrada");

        String datosAnt = "cedula=" + e.getCedula();
        buildFromRequest(e, request);
        Entrenador saved = entrenadorRepository.save(e);
        auditoriaService.registrar("entrenadores", "UPDATE", "system",
                datosAnt, "id=" + saved.getId(), null);
        return toResponse(saved);
    }

    @Override
    public void eliminar(Integer id) {
        findOrThrow(id);
        auditoriaService.registrar("entrenadores", "DELETE", "system", "id=" + id, null, null);
        entrenadorRepository.deleteById(id);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Entrenador findOrThrow(Integer id) {
        return entrenadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado con id: " + id));
    }

    private Entrenador buildFromRequest(Entrenador e, EntrenadorRequest req) {
        e.setNombre(req.getNombre());
        e.setApellido(req.getApellido());
        e.setCedula(req.getCedula());
        e.setTelefono(req.getTelefono());
        e.setEmail(req.getEmail());
        e.setEspecialidad(req.getEspecialidad());
        e.setHorario(req.getHorario());
        e.setFechaIngreso(req.getFechaIngreso());
        if (req.getUsuarioId() != null) {
            UsuarioSistema u = usuarioRepository.findById(req.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + req.getUsuarioId()));
            e.setUsuarioSistema(u);
        }
        return e;
    }

    private EntrenadorResponse toResponse(Entrenador e) {
        return EntrenadorResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .cedula(e.getCedula())
                .telefono(e.getTelefono())
                .email(e.getEmail())
                .especialidad(e.getEspecialidad())
                .horario(e.getHorario())
                .fechaIngreso(e.getFechaIngreso())
                .usuarioId(e.getUsuarioSistema() != null ? e.getUsuarioSistema().getId() : null)
                .usuarioNombre(e.getUsuarioSistema() != null ? e.getUsuarioSistema().getNombre() : null)
                .build();
    }

    private ClienteResponse clienteToResponse(Cliente c) {
        return ClienteResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .apellido(c.getApellido())
                .cedula(c.getCedula())
                .telefono(c.getTelefono())
                .email(c.getEmail())
                .fechaRegistro(c.getFechaRegistro())
                .build();
    }

    private SesionResponse sesionToResponse(Sesion s) {
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
