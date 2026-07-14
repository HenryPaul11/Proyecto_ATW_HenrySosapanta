package com.powerfit.config;

import com.powerfit.entity.UsuarioSistema;
import com.powerfit.repository.UsuarioSistemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea automáticamente el usuario administrador al iniciar la aplicación
 * si no existe en la base de datos.
 * Las credenciales se leen desde variables de entorno / application.properties.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UsuarioSistemaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.nombre}")
    private String adminNombre;

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByUsuario(adminUsername)) {
            log.info("✅ Usuario administrador '{}' ya existe — no se crea de nuevo.", adminUsername);
            return;
        }

        UsuarioSistema admin = UsuarioSistema.builder()
                .usuario(adminUsername)
                .nombre(adminNombre)
                .correo(adminEmail)
                // BCrypt: la contraseña se almacena como hash, nunca en texto plano
                .contrasena(passwordEncoder.encode(adminPassword))
                .rol(UsuarioSistema.Rol.admin)
                .activo(true)
                .build();

        usuarioRepository.save(admin);
        log.info("🚀 Usuario administrador '{}' creado automáticamente con hash BCrypt.", adminUsername);
    }
}
