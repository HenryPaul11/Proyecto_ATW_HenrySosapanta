package com.powerfit.repository;

import com.powerfit.entity.CategoriaEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoriaEquipoRepository extends JpaRepository<CategoriaEquipo, Long> {
    Optional<CategoriaEquipo> findByNombre(String nombre);
}
