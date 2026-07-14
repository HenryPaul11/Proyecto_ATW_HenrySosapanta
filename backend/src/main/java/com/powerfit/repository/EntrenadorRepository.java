package com.powerfit.repository;

import com.powerfit.entity.Entrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EntrenadorRepository extends JpaRepository<Entrenador, Integer> {
    Optional<Entrenador> findByCedula(String cedula);
    boolean existsByCedula(String cedula);
    boolean existsByCedulaAndIdNot(String cedula, Integer id);
    Optional<Entrenador> findByUsuarioSistemaUsuario(String username);
}
