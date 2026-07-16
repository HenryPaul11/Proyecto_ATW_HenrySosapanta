package com.powerfit.service.impl;

import com.powerfit.dto.request.SucursalRequest;
import com.powerfit.dto.response.SucursalResponse;
import com.powerfit.entity.Sucursal;
import com.powerfit.entity.UsuarioSistema;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.SucursalRepository;
import com.powerfit.repository.UsuarioSistemaRepository;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository       sucursalRepository;
    private final UsuarioSistemaRepository usuarioRepository;
    private final PasswordEncoder          passwordEncoder;
    private final AuditoriaService         auditoriaService;

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
    @Transactional
    public SucursalResponse crear(SucursalRequest request) {
        if (sucursalRepository.existsByNombre(request.getNombre()))
            throw new BadRequestException("Ya existe una sucursal con el nombre: " + request.getNombre());

        // ── Resolver usuario vinculado ────────────────────────────────────────
        UsuarioSistema usuario = resolverUsuario(request);

        Sucursal s = Sucursal.builder()
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .ciudad(request.getCiudad())
                .fechaApertura(request.getFechaApertura())
                .activo(true)
                .usuarioSistema(usuario)
                .build();

        Sucursal saved = sucursalRepository.save(s);
        auditoriaService.registrar("sucursales", "INSERT", "system",
                null, "id=" + saved.getId() + ",nombre=" + saved.getNombre(), null);
        return toResponse(saved);
    }

    @Override
    @Transactional
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

        // Actualizar usuario si se envían nuevas credenciales
        if (request.getUsuario() != null && !request.getUsuario().isBlank()) {
            UsuarioSistema usuario = resolverUsuario(request);
            s.setUsuarioSistema(usuario);
        }

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

    // ── helpers ──────────────────────────────────────────────────────────────

    /**
     * Si viene usuarioId → busca el usuario existente.
     * Si viene usuario + contrasena → crea uno nuevo con rol admin.
     * Si no viene nada → retorna null.
     */
    private UsuarioSistema resolverUsuario(SucursalRequest request) {
        if (request.getUsuarioId() != null) {
            return usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuario no encontrado con id: " + request.getUsuarioId()));
        }
        if (request.getUsuario() != null && !request.getUsuario().isBlank()
                && request.getContrasena() != null && !request.getContrasena().isBlank()) {

            if (usuarioRepository.existsByUsuario(request.getUsuario()))
                throw new BadRequestException("El nombre de usuario '" + request.getUsuario() + "' ya está en uso.");

            UsuarioSistema nuevoUsuario = UsuarioSistema.builder()
                    .usuario(request.getUsuario())
                    .nombre("Admin " + request.getNombre())
                    .correo(request.getUsuario() + "@powerfit.com")
                    .contrasena(passwordEncoder.encode(request.getContrasena()))
                    .rol(UsuarioSistema.Rol.admin)
                    .activo(true)
                    .build();
            return usuarioRepository.save(nuevoUsuario);
        }
        return null;
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
                .usuarioId(s.getUsuarioSistema() != null ? s.getUsuarioSistema().getId() : null)
                .usuarioNombre(s.getUsuarioSistema() != null ? s.getUsuarioSistema().getUsuario() : null)
                .build();
    }
}
