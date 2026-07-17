package com.powerfit.config;

import com.powerfit.entity.Rol;
import com.powerfit.entity.Sucursal;
import com.powerfit.entity.Usuario;
import com.powerfit.repository.RolRepository;
import com.powerfit.repository.SucursalRepository;
import com.powerfit.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final RolRepository      rolRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository  usuarioRepository;
    private final PasswordEncoder    passwordEncoder;

    @Value("${app.admin.email:admin@powerfit.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.nombre:Administrador Principal}")
    private String adminNombre;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // ── 1. Roles ─────────────────────────────────────────────────────────
        Rol rolMatriz = upsertRol("ADMIN_MATRIZ",   Rol.AmbitoRol.MATRIZ,   "Administrador general");
        upsertRol("ADMIN_SUCURSAL",  Rol.AmbitoRol.SUCURSAL, "Administrador de sucursal");
        upsertRol("RECEPCIONISTA",   Rol.AmbitoRol.SUCURSAL, "Recepcionista de sucursal");
        upsertRol("ENTRENADOR",      Rol.AmbitoRol.SUCURSAL, "Entrenador de sucursal");
        upsertRol("CLIENTE",         Rol.AmbitoRol.SUCURSAL, "Cliente del gimnasio");

        // ── 2. Sucursal Matriz (la principal, creada por defecto) ─────────────
        Sucursal matriz = sucursalRepository.findByEstado(Sucursal.EstadoGeneral.ACTIVO)
                .stream()
                .filter(s -> "MATRIZ".equalsIgnoreCase(s.getCodigo()))
                .findFirst()
                .orElseGet(() -> {
                    log.info("✅ Creando Sucursal Matriz por defecto");
                    return sucursalRepository.save(Sucursal.builder()
                            .nombre("PowerFit Matriz")
                            .codigo("MATRIZ")
                            .direccion("Sede Principal")
                            .ciudad("Quito")
                            .estado(Sucursal.EstadoGeneral.ACTIVO)
                            .build());
                });

        // ── 3. Usuario Admin Matriz ───────────────────────────────────────────
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            usuarioRepository.save(Usuario.builder()
                    .email(adminEmail)
                    .nombreCompleto(adminNombre)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .rol(rolMatriz)
                    .sucursal(null)          // admin matriz no pertenece a ninguna sucursal
                    .estado(Usuario.EstadoGeneral.ACTIVO)
                    .build());
            log.info("✅ Admin Matriz creado → email: {}  password: {}", adminEmail, adminPassword);
        } else {
            log.info("ℹ️  Admin Matriz ya existe: {}", adminEmail);
        }

        log.info("✅ Sucursal Matriz id={} nombre={}", matriz.getId(), matriz.getNombre());
    }

    private Rol upsertRol(String nombre, Rol.AmbitoRol ambito, String descripcion) {
        return rolRepository.findByNombreRol(nombre).orElseGet(() -> {
            log.info("✅ Creando rol: {}", nombre);
            return rolRepository.save(Rol.builder()
                    .nombreRol(nombre).ambito(ambito).descripcion(descripcion).build());
        });
    }
}
