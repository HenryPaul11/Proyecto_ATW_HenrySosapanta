package com.powerfit.repository;

import com.powerfit.entity.ClienteEntrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClienteEntrenadorRepository extends JpaRepository<ClienteEntrenador, Integer> {
    List<ClienteEntrenador> findByEntrenadorId(Integer entrenadorId);
    List<ClienteEntrenador> findByEntrenadorIdAndActivoTrue(Integer entrenadorId);
    List<ClienteEntrenador> findByClienteId(Integer clienteId);
}
