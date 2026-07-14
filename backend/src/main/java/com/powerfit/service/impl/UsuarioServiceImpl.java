package com.powerfit.service.impl;

import com.powerfit.dto.request.UsuarioRequest;
import com.powerfit.dto.response.UsuarioResponse;
import com.powerfit.entity.UsuarioSistema;
import com.powerfit.exception.BadRequestException;
import com.powerfit.exception.ResourceNotFoundException;
import com.powerfit.repository.UsuarioSistemaRepository;
import com.powerfit.service.AuditoriaService;
import com.powerfit.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioSistemaRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UsuarioResponse> listarPaginado(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse buscarPorId(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsuario(request.getUsuario()))
            throw new BadRequestException("El nombre de usuario '" + request.getUsuario() + "' ya está en uso");
        if (usuarioRepository.existsByCorreo(request.getCorreo()))
            throw new BadRequestException("El correo '" + request.getCorreo() + "' ya está registrado");

        UsuarioSistema u = UsuarioSistema.builder()
                .usuario(request.getUsuario())
                .nombre(request.getNombre())
                .correo(request.getCorreo())
                // BCrypt: la contraseña se almacena como hash irreversible
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .rol(UsuarioSistema.Rol.valueOf(request.getRol()))
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        UsuarioSistema saved = usuarioRepository.save(u);
        log.info("Usuario creado: id={}, usuario={}, rol={}", saved.getId(), saved.getUsuario(), saved.getRol());
        auditoriaService.registrar("usuarios_sistema", "INSERT", saved.getUsuario(),
                null, "id=" + saved.getId(), null);
        return toResponse(saved);
    }

    @Override
    public UsuarioResponse actualizar(Integer id, UsuarioRequest request) {
        UsuarioSistema u = findOrThrow(id);

        if (!u.getUsuario().equals(request.getUsuario()) &&
                usuarioRepository.existsByUsuario(request.getUsuario()))
            throw new BadRequestException("El nombre de usuario '" + request.getUsuario() + "' ya está en uso");

        if (!u.getCorreo().equals(request.getCorreo()) &&
                usuarioRepository.existsByCorreo(request.getCorreo()))
            throw new BadRequestException("El correo '" + request.getCorreo() + "' ya está registrado");

        String datosAnt = "usuario=" + u.getUsuario();
        u.setUsuario(request.getUsuario());
        u.setNombre(request.getNombre());
        u.setCorreo(request.getCorreo());
        // Si se envía nueva contraseña, se hashea nuevamente
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            u.setContrasena(passwordEncoder.encode(request.getContrasena()));
        }
        u.setRol(UsuarioSistema.Rol.valueOf(request.getRol()));
        if (request.getActivo() != null)
            u.setActivo(request.getActivo());

        UsuarioSistema saved = usuarioRepository.save(u);
        log.info("Usuario actualizado: id={}, usuario={}", saved.getId(), saved.getUsuario());
        auditoriaService.registrar("usuarios_sistema", "UPDATE", saved.getUsuario(),
                datosAnt, "usuario=" + saved.getUsuario(), null);
        return toResponse(saved);
    }

    @Override
    public void eliminar(Integer id) {
        UsuarioSistema u = findOrThrow(id);
        log.info("Usuario eliminado: id={}, usuario={}", id, u.getUsuario());
        auditoriaService.registrar("usuarios_sistema", "DELETE", u.getUsuario(),
                "id=" + id, null, null);
        usuarioRepository.deleteById(id);
    }

    private UsuarioSistema findOrThrow(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    private UsuarioResponse toResponse(UsuarioSistema u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .usuario(u.getUsuario())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                // NUNCA se devuelve la contraseña en la respuesta
                .rol(u.getRol().name())
                .activo(u.getActivo())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
