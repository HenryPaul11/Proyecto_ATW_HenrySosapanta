package com.powerfit.repository;

import com.powerfit.entity.Sesion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, Integer> {
    List<Sesion> findByEntrenadorId(Integer entrenadorId);
    List<Sesion> findByClienteId(Integer clienteId);

    // Paginación FÍSICA con filtro LÓGICO opcional por estado
    @Query("SELECT s FROM Sesion s WHERE (:estado IS NULL OR s.estado = :estado)")
    Page<Sesion> findByEstadoOptional(
            @Param("estado") Sesion.EstadoSesion estado, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Sesion s WHERE s.estado = 'completada'")
    Long countCompletadas();

    @Query("SELECT COUNT(s) FROM Sesion s WHERE s.estado = 'pendiente'")
    Long countPendientes();

    @Query("SELECT COUNT(s) FROM Sesion s WHERE s.estado = 'cancelada'")
    Long countCanceladas();
}
