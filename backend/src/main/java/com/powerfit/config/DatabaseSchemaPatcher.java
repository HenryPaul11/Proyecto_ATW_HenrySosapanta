package com.powerfit.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Aplica ajustes de esquema que Hibernate ddl-auto=update no modifica
 * (p. ej. ampliar columnas varchar existentes en PostgreSQL).
 */
@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class DatabaseSchemaPatcher implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        ampliarColumnaImagenUrl();
    }

    private void ampliarColumnaImagenUrl() {
        try {
            jdbcTemplate.execute(
                "ALTER TABLE equipos ALTER COLUMN imagen_url TYPE TEXT"
            );
            log.info("Columna equipos.imagen_url actualizada a TEXT.");
        } catch (Exception ex) {
            log.warn("No se pudo ampliar equipos.imagen_url (puede que ya esté actualizada): {}", ex.getMessage());
        }
    }
}
