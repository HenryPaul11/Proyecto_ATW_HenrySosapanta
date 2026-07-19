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

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401); res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.getWriter().write(mapper.writeValueAsString(ApiResponse.error("Token inválido o no proporcionado", 401)));
                })
                .accessDeniedHandler((req, res, e) -> {
                    res.setStatus(403); res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.getWriter().write(mapper.writeValueAsString(ApiResponse.error("Sin permisos", 403)));
                })
            )
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sin autenticación)
                .requestMatchers("/api/auth/**","/swagger-ui.html","/swagger-ui/**","/api-docs/**","/v3/api-docs/**").permitAll()
                
                // ENDPOINTS PÚBLICOS DE CONSULTA (AGREGAR AQUÍ)
                .requestMatchers(HttpMethod.GET, "/api/clientes/paginado").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/clientes/**").permitAll()  // Si quieres que todos los GET de clientes sean públicos
                // O específicamente:
                // .requestMatchers(HttpMethod.GET, "/api/clientes").permitAll()
                // .requestMatchers(HttpMethod.GET, "/api/clientes/{id}").permitAll()
                // .requestMatchers(HttpMethod.GET, "/api/clientes/documento/{doc}").permitAll()
                
                // Roles: ADMIN_MATRIZ, ADMIN_SUCURSAL, ENTRENADOR, CLIENTE
                .requestMatchers(HttpMethod.GET, "/api/sucursales/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers("/api/sucursales/**").hasRole("ADMIN_MATRIZ")
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers("/api/usuarios/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers("/api/auditorias/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers("/api/sesiones/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL","ENTRENADOR")
                .requestMatchers("/api/entrenadores/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL","ENTRENADOR")
                .requestMatchers(HttpMethod.GET, "/api/equipos/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL","ENTRENADOR")
                .requestMatchers(HttpMethod.POST, "/api/equipos/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers(HttpMethod.PUT,  "/api/equipos/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers(HttpMethod.DELETE, "/api/equipos/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers(HttpMethod.POST, "/api/clientes/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers(HttpMethod.PUT,  "/api/clientes/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers(HttpMethod.POST, "/api/membresias/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers(HttpMethod.PUT,  "/api/membresias/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                .requestMatchers(HttpMethod.POST, "/api/pagos/**").hasAnyRole("ADMIN_MATRIZ","ADMIN_SUCURSAL")
                
                // El resto de GET requieren autenticación
                .requestMatchers(HttpMethod.GET,  "/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}