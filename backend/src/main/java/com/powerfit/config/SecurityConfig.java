package com.powerfit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.powerfit.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * BCryptPasswordEncoder: transforma la contraseña en un hash irreversible.
     * Se usa al registrar usuarios y al verificar credenciales en el login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 401 – No autenticado
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.getWriter().write(
                        mapper.writeValueAsString(
                            ApiResponse.error("Token inválido o no proporcionado", 401)));
                })
                // 403 – Sin permisos
                .accessDeniedHandler((req, res, e) -> {
                    res.setStatus(403);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.getWriter().write(
                        mapper.writeValueAsString(
                            ApiResponse.error("No tienes permisos para realizar esta operación", 403)));
                })
            )
            .authorizeHttpRequests(auth -> auth

                // ── Público ────────────────────────────────────────────────
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/api-docs/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // ── Solo ADMIN ─────────────────────────────────────────────
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/auditorias/**").hasRole("ADMIN")
                .requestMatchers("/api/sucursales/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                // ── ADMIN y ENTRENADOR ─────────────────────────────────────
                .requestMatchers("/api/sesiones/**").hasAnyRole("ADMIN","ENTRENADOR")
                .requestMatchers("/api/entrenadores/**").hasAnyRole("ADMIN","ENTRENADOR")
                .requestMatchers(HttpMethod.POST, "/api/clientes/**").hasAnyRole("ADMIN","ENTRENADOR")
                .requestMatchers(HttpMethod.PUT,  "/api/clientes/**").hasAnyRole("ADMIN","ENTRENADOR")
                .requestMatchers(HttpMethod.POST, "/api/membresias/**").hasAnyRole("ADMIN","ENTRENADOR")
                .requestMatchers(HttpMethod.PUT,  "/api/membresias/**").hasAnyRole("ADMIN","ENTRENADOR")
                .requestMatchers(HttpMethod.POST, "/api/pagos/**").hasAnyRole("ADMIN","ENTRENADOR")

                // ── Todos los roles autenticados (USER = cliente) ──────────
                .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasAnyRole("ADMIN","ENTRENADOR","CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/membresias/**").hasAnyRole("ADMIN","ENTRENADOR","CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/pagos/**").hasAnyRole("ADMIN","ENTRENADOR","CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/equipos/**").hasAnyRole("ADMIN","ENTRENADOR","CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/tipos-membresia/**").hasAnyRole("ADMIN","ENTRENADOR","CLIENTE")

                // ── Resto requiere autenticación ───────────────────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
