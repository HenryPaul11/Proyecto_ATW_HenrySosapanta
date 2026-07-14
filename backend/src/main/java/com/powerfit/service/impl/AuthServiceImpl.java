package com.powerfit.service.impl;

import com.powerfit.config.JwtUtil;
import com.powerfit.dto.request.LoginRequest;
import com.powerfit.dto.response.LoginResponse;
import com.powerfit.entity.UsuarioSistema;
import com.powerfit.exception.BadRequestException;
import com.powerfit.repository.UsuarioSistemaRepository;
import com.powerfit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioSistemaRepository usuarioRepository;
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

        // BCrypt.matches() compara contraseña ingresada con el hash guardado
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            log.warn("Login fallido: contraseña incorrecta para '{}'", request.getUsuario());
            throw new BadRequestException("Credenciales incorrectas");
        }

        if (!usuario.getActivo()) {
            throw new BadRequestException("El usuario está inactivo");
        }

        String token = jwtUtil.generateToken(usuario.getUsuario(), usuario.getRol().name());
        log.info("Login exitoso para '{}' con rol '{}'", usuario.getUsuario(), usuario.getRol());

        return LoginResponse.builder()
                .id(usuario.getId())
                .usuario(usuario.getUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol().name())
                .token(token)
                .build();
    }
}
