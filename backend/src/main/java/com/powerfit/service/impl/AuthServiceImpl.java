package com.powerfit.service.impl;

import com.powerfit.config.JwtUtil;
import com.powerfit.dto.request.LoginRequest;
import com.powerfit.dto.response.LoginResponse;
import com.powerfit.entity.Usuario;
import com.powerfit.exception.BadRequestException;
import com.powerfit.repository.UsuarioRepository;
import com.powerfit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository  usuarioRepository;
    private final JwtUtil            jwtUtil;
    private final PasswordEncoder    passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.email:admin@powerfit.com}")
    private String adminEmail;

    @Override
    public LoginResponse login(LoginRequest request) {
        String login = resolveLogin(request.getEmail());
        Usuario usuario = usuarioRepository.findByEmail(login)
                .orElseThrow(() -> new BadRequestException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash()))
            throw new BadRequestException("Credenciales incorrectas");

        if (usuario.getEstado() == Usuario.EstadoGeneral.INACTIVO)
            throw new BadRequestException("Usuario inactivo");

        String rolNombre = usuario.getRol().getNombreRol();
        String token = jwtUtil.generateToken(usuario.getEmail(), rolNombre);

        Long   sucursalId     = usuario.getSucursal() != null ? usuario.getSucursal().getId()     : null;
        String sucursalNombre = usuario.getSucursal() != null ? usuario.getSucursal().getNombre() : null;

        log.info("Login exitoso: {} rol={} sucursal={}", usuario.getEmail(), rolNombre, sucursalId);

        return LoginResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(rolNombre)
                .token(token)
                .sucursalId(sucursalId)
                .sucursalNombre(sucursalNombre)
                .build();
    }

    private String resolveLogin(String login) {
        if (login != null && login.equalsIgnoreCase(adminUsername)) {
            return adminEmail;
        }
        return login;
    }
}
