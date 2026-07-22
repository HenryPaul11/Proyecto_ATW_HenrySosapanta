package com.powerfit.service.impl;

import com.powerfit.dto.request.UsuarioRequest;
import com.powerfit.dto.response.UsuarioResponse;
import com.powerfit.entity.Rol;
import com.powerfit.entity.Sucursal;
import com.powerfit.entity.Usuario;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.RolRepository;
import com.powerfit.repository.SucursalRepository;
import com.powerfit.repository.UsuarioRepository;
import com.powerfit.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UsuarioResponse> listarPaginado(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public UsuarioResponse buscarPorId(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        log.info("Registrando nuevo Usuario email={}", request.getCorreo());
        if (usuarioRepository.existsByEmail(request.getCorreo())) {
            throw new BadRequestException("Ya existe un usuario con ese correo");
        }

        Rol rol = findRol(request.getRol());

        Usuario.UsuarioBuilder builder = Usuario.builder()
                .nombreCompleto(request.getNombre())
                .email(request.getCorreo())
                .passwordHash(passwordEncoder.encode(request.getContrasena()))
                .rol(rol)
                .estado(toEstado(request.getActivo()));

        if (rol.getAmbito() == Rol.AmbitoRol.SUCURSAL && request.getSucursalId() != null) {
            Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + request.getSucursalId()));
            builder.sucursal(sucursal);
        } else if (rol.getAmbito() == Rol.AmbitoRol.SUCURSAL && request.getSucursalId() == null) {
            throw new BadRequestException("sucursalId es obligatorio para el rol " + rol.getNombreRol());
        }

        return toResponse(usuarioRepository.save(builder.build()));
    }

    @Override
    public UsuarioResponse actualizar(Integer id, UsuarioRequest request) {
        log.info("Actualizando Usuario id={}", id);
        Usuario usuario = findOrThrow(id);
        if (usuarioRepository.existsByEmailAndIdNot(request.getCorreo(), id.longValue())) {
            throw new BadRequestException("Ya existe un usuario con ese correo");
        }

        Rol rol = findRol(request.getRol());

        usuario.setNombreCompleto(request.getNombre());
        usuario.setEmail(request.getCorreo());
        usuario.setRol(rol);
        usuario.setEstado(toEstado(request.getActivo()));
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getContrasena()));
        }

        if (rol.getAmbito() == Rol.AmbitoRol.SUCURSAL) {
            usuario.setSucursal(resolverSucursal(request.getSucursalId(), rol));
        } else {
            usuario.setSucursal(null);
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(Integer id) {
        log.info("Eliminando Usuario id={}", id);
        Usuario usuario = findOrThrow(id);
        usuario.setEstado(Usuario.EstadoGeneral.INACTIVO);
        usuarioRepository.save(usuario);
    }

    private Sucursal resolverSucursal(Long sucursalId, Rol rol) {
        if (sucursalId == null) {
            throw new BadRequestException("sucursalId es obligatorio para el rol " + rol.getNombreRol());
        }
        return sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + sucursalId));
    }

    private Usuario findOrThrow(Integer id) {
        return usuarioRepository.findById(id.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    private Rol findRol(String rol) {
        return rolRepository.findByNombreRol(mapRolName(rol))
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + rol));
    }

    private String mapRolName(String rol) {
        if (rol == null) return "CLIENTE";
        return switch (rol.toLowerCase()) {
            case "admin", "admin_matriz"   -> "ADMIN_MATRIZ";
            case "admin_sucursal"          -> "ADMIN_SUCURSAL";
            case "entrenador"              -> "ENTRENADOR";
            case "cliente"                 -> "CLIENTE";
            default                        -> rol.toUpperCase();
        };
    }

    private Usuario.EstadoGeneral toEstado(Boolean activo) {
        return Boolean.FALSE.equals(activo) ? Usuario.EstadoGeneral.INACTIVO : Usuario.EstadoGeneral.ACTIVO;
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .usuario(usuario.getEmail())
                .nombre(usuario.getNombreCompleto())
                .correo(usuario.getEmail())
                .rol(usuario.getRol().getNombreRol())
                .activo(usuario.getEstado() == Usuario.EstadoGeneral.ACTIVO)
                .createdAt(usuario.getFechaCreacion())
                .build();
    }
}