package com.powerfit.service.impl;

import com.powerfit.config.JwtUtil;
import com.powerfit.dto.request.LoginRequest;
import com.powerfit.dto.response.LoginResponse;
import com.powerfit.entity.Sucursal;
import com.powerfit.entity.UsuarioSistema;
import com.powerfit.exception.BadRequestException;
import com.powerfit.repository.SucursalRepository;
import com.powerfit.repository.UsuarioSistemaRepository;
import com.powerfit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioSistemaRepository usuarioRepository;
    private final SucursalRepository       sucursalRepository;
    private final JwtUtil                  jwtUtil;
    private final PasswordEncoder          passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getUsuario());

        UsuarioSistema usuario = usuarioRepository
                .findByUsuario(request.getUsuario())
                .orElseThrow(() -> {
                    log.warn("Login fallido: usuario '{}' no encontrado", request.getUsuario());
                    return new BadRequestException("Credenciales incorrectas");
                });

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            log.warn("Login fallido: contraseña incorrecta para '{}'", request.getUsuario());
            throw new BadRequestException("Credenciales incorrectas");
        }

        if (!usuario.getActivo()) {
            throw new BadRequestException("El usuario está inactivo");
        }

        // Buscar si este usuario está asignado a alguna sucursal
        Optional<Sucursal> sucursalOpt = sucursalRepository.findByUsuarioSistemaId(usuario.getId());
        Integer sucursalId     = sucursalOpt.map(Sucursal::getId).orElse(null);
        String  sucursalNombre = sucursalOpt.map(Sucursal::getNombre).orElse(null);

        String token = jwtUtil.generateToken(usuario.getUsuario(), usuario.getRol().name());
        log.info("Login exitoso para '{}' con rol '{}', sucursal={}", usuario.getUsuario(), usuario.getRol(), sucursalId);

        return LoginResponse.builder()
                .id(usuario.getId())
                .usuario(usuario.getUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol().name())
                .token(token)
                .sucursalId(sucursalId)
                .sucursalNombre(sucursalNombre)
                .build();
    }
}
