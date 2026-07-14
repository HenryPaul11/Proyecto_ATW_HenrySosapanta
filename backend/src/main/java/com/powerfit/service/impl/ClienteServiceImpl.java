package com.powerfit.service.impl;

import com.powerfit.dto.request.ClienteRequest;
import com.powerfit.dto.response.ClienteResponse;
import com.powerfit.entity.Cliente;
import com.powerfit.entity.UsuarioSistema;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.ClienteRepository;
import com.powerfit.repository.UsuarioSistemaRepository;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository        clienteRepository;
    private final UsuarioSistemaRepository usuarioRepository;
    private final AuditoriaService         auditoriaService;

    // ── Paginación ───────────────────────────────────────────────────────────
    @Override
    public Page<ClienteResponse> listarPaginado(String busqueda, Pageable pageable) {
        return clienteRepository.buscarActivos(busqueda, pageable)
                .map(this::toResponse);
    }

    // ── Lista completa (sin paginación) ──────────────────────────────────────
    @Override
    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findByActivoTrue().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ClienteResponse buscarPorId(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public ClienteResponse buscarPorCedula(String cedula) {
        return toResponse(clienteRepository.findByCedulaAndActivoTrue(cedula)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con cédula: " + cedula)));
    }

    @Override
    public List<ClienteResponse> sinMembresia() {
        return clienteRepository.findClientesSinMembresia()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.existsByCedula(request.getCedula()))
            throw new BadRequestException("La cédula " + request.getCedula() + " ya está registrada");
        if (request.getEmail() != null && clienteRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("El email " + request.getEmail() + " ya está registrado");

        Cliente c = buildFromRequest(new Cliente(), request);
        c.setActivo(true);
        Cliente saved = clienteRepository.save(c);
        auditoriaService.registrar("clientes", "INSERT", "system",
                null, "id=" + saved.getId() + ",cedula=" + saved.getCedula(), null);
        return toResponse(saved);
    }

    @Override
    public ClienteResponse actualizar(Integer id, ClienteRequest request) {
        Cliente c = findOrThrow(id);

        if (!c.getCedula().equals(request.getCedula()) &&
                clienteRepository.existsByCedulaAndIdNot(request.getCedula(), id))
            throw new BadRequestException("La cédula " + request.getCedula() + " ya está registrada");

        if (request.getEmail() != null && !request.getEmail().equals(c.getEmail()) &&
                clienteRepository.existsByEmailAndIdNot(request.getEmail(), id))
            throw new BadRequestException("El email " + request.getEmail() + " ya está registrado");

        String datosAnt = "cedula=" + c.getCedula();
        buildFromRequest(c, request);
        Cliente saved = clienteRepository.save(c);
        auditoriaService.registrar("clientes", "UPDATE", "system",
                datosAnt, "id=" + saved.getId(), null);
        return toResponse(saved);
    }

    // ── Eliminación LÓGICA (activo = false) ──────────────────────────────────
    @Override
    public void eliminar(Integer id) {
        Cliente c = findOrThrow(id);
        String datosAnt = "activo=true,cedula=" + c.getCedula();
        c.setActivo(false);                          // no se borra, solo se desactiva
        clienteRepository.save(c);
        auditoriaService.registrar("clientes", "DELETE", "system",
                datosAnt, "activo=false,id=" + id, null);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Cliente findOrThrow(Integer id) {
        Cliente c = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        if (!c.getActivo())
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        return c;
    }

    private Cliente buildFromRequest(Cliente c, ClienteRequest req) {
        c.setNombre(req.getNombre());
        c.setApellido(req.getApellido());
        c.setCedula(req.getCedula());
        c.setTelefono(req.getTelefono());
        c.setEmail(req.getEmail());
        if (req.getUsuarioId() != null) {
            UsuarioSistema u = usuarioRepository.findById(req.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + req.getUsuarioId()));
            c.setUsuarioSistema(u);
        } else {
            c.setUsuarioSistema(null);
        }
        return c;
    }

    private ClienteResponse toResponse(Cliente c) {
        return ClienteResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .apellido(c.getApellido())
                .cedula(c.getCedula())
                .telefono(c.getTelefono())
                .email(c.getEmail())
                .fechaRegistro(c.getFechaRegistro())
                .usuarioId(c.getUsuarioSistema() != null ? c.getUsuarioSistema().getId() : null)
                .usuarioNombre(c.getUsuarioSistema() != null ? c.getUsuarioSistema().getNombre() : null)
                .build();
    }
}
