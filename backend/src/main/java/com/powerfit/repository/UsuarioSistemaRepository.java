package com.powerfit.repository;

import com.powerfit.entity.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Integer> {
    Optional<UsuarioSistema> findByUsuario(String usuario);
    Optional<UsuarioSistema> findByCorreo(String correo);
    boolean existsByUsuario(String usuario);
    boolean existsByCorreo(String correo);
    Optional<UsuarioSistema> findByUsuarioAndContrasena(String usuario, String contrasena);
}
