package com.powerfit.util;

import com.powerfit.entity.Rol;
import com.powerfit.entity.Sucursal;
import com.powerfit.entity.Usuario;
import com.powerfit.exception.UnauthorizedException;
import com.powerfit.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utilidad para extraer información del usuario autenticado desde Spring Security.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene el usuario actualmente autenticado.
     *
     * @return Usuario autenticado.
     * @throws UnauthorizedException si no existe un usuario autenticado.
     */
    public Usuario getUsuarioActual() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("No hay usuario autenticado");
        }

        String email = auth.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UnauthorizedException("Usuario no encontrado: " + email));
    }

    /**
     * Obtiene el ID del usuario autenticado.
     */
    public Long getUsuarioId() {
        return getUsuarioActual().getId();
    }

    /**
     * Obtiene el rol del usuario autenticado.
     */
    public Rol getRolActual() {
        return getUsuarioActual().getRol();
    }

    /**
     * Obtiene la sucursal asignada al usuario.
     * Si es administrador matriz devolverá Optional.empty().
     */
    public Optional<Sucursal> getSucursalActual() {
        return Optional.ofNullable(getUsuarioActual().getSucursal());
    }

    /**
     * Obtiene el ID de la sucursal del usuario.
     * Devuelve null si pertenece a la matriz.
     */
    public Long getSucursalIdActual() {
        return getSucursalActual()
                .map(Sucursal::getId)
                .orElse(null);
    }

    /**
     * Indica si el usuario es administrador matriz.
     * Un administrador matriz no tiene sucursal asignada.
     */
    public boolean isAdminMatriz() {
        return getUsuarioActual().getSucursal() == null;
    }

}