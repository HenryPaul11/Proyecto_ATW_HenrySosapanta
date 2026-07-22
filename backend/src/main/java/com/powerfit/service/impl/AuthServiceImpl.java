package com.powerfit.service.impl;

import com.powerfit.config.JwtUtil;
import com.powerfit.dto.request.LoginRequest;
import com.powerfit.dto.response.LoginResponse;
import com.powerfit.entity.Cliente;
import com.powerfit.entity.Usuario;
import com.powerfit.exception.BadRequestException;
import com.powerfit.repository.ClienteRepository;
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
    private final ClienteRepository  clienteRepository;
    private final JwtUtil            jwtUtil;
    private final PasswordEncoder    passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.email:admin@powerfit.com}")
    private String adminEmail;

    @Override
    public LoginResponse login(LoginRequest request) {
        String login = resolveLogin(request.getEmail());

        // 1. Buscar en usuarios (admin, entrenador)
        Usuario usuario = usuarioRepository.findByEmail(login).orElse(null);
        if (usuario != null) {
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

        // 2. Fallback: buscar en clientes
        Cliente cliente = clienteRepository.findByEmail(login)
                .orElseThrow(() -> new BadRequestException("Credenciales incorrectas"));

        if (cliente.getPasswordHash() == null ||
            !passwordEncoder.matches(request.getPassword(), cliente.getPasswordHash()))
            throw new BadRequestException("Credenciales incorrectas");

        if (cliente.getEstado() == Cliente.EstadoGeneral.INACTIVO)
            throw new BadRequestException("Cliente inactivo");

        String token = jwtUtil.generateToken(cliente.getEmail(), "CLIENTE");
        Long   sucursalId     = cliente.getSucursal() != null ? cliente.getSucursal().getId()     : null;
        String sucursalNombre = cliente.getSucursal() != null ? cliente.getSucursal().getNombre() : null;

        log.info("Login exitoso (cliente): {} sucursal={}", cliente.getEmail(), sucursalId);

        return LoginResponse.builder()
                .id(cliente.getId())
                .email(cliente.getEmail())
                .nombreCompleto(cliente.getNombreCompleto())
                .rol("CLIENTE")
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
