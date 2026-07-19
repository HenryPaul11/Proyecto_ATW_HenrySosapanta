package com.powerfit.repository;

import com.powerfit.entity.Entrenador;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntrenadorRepository extends JpaRepository<Entrenador, Long> {
    Optional<Entrenador> findByDocumentoIdentidad(String doc);
    boolean existsByDocumentoIdentidad(String doc);
    boolean existsByDocumentoIdentidadAndIdNot(String doc, Long id);
    @EntityGraph(attributePaths = {"sucursal"})
    @Query("SELECT e FROM Entrenador e WHERE LOWER(e.email) = LOWER(:email)")
    Optional<Entrenador> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {"sucursal"})
    @Query("SELECT e FROM Entrenador e WHERE :sucursalId IS NULL OR e.sucursal.id = :sucursalId")
    List<Entrenador> findBySucursal(@Param("sucursalId") Long sucursalId);
}
