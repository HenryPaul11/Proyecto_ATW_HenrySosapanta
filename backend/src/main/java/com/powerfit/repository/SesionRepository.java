package com.powerfit.repository;

import com.powerfit.entity.Sesion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, Long> {

    @EntityGraph(attributePaths = {"entrenador", "sucursal", "cliente"})
    @Query("SELECT s FROM Sesion s WHERE :sucursalId IS NULL OR s.sucursal.id = :sucursalId")
    List<Sesion> findBySucursal(@Param("sucursalId") Long sucursalId);

    @EntityGraph(attributePaths = {"entrenador", "sucursal", "cliente"})
    List<Sesion> findByEntrenadorId(Long entrenadorId);

    @EntityGraph(attributePaths = {"entrenador", "sucursal", "cliente"})
    @Query("SELECT s FROM Sesion s WHERE :estado IS NULL OR s.estado = :estado")
    Page<Sesion> findByEstadoOptional(@Param("estado") Sesion.EstadoGeneral estado, Pageable pageable);

    @EntityGraph(attributePaths = {"entrenador", "sucursal", "cliente"})
    List<Sesion> findByEntrenadorIdAndEstado(Long entrenadorId, Sesion.EstadoGeneral estado);

    @Query("SELECT s FROM Sesion s LEFT JOIN FETCH s.entrenador LEFT JOIN FETCH s.sucursal LEFT JOIN FETCH s.cliente WHERE s.estado = :estado")
    Page<Sesion> findByEstadoWithJoins(@Param("estado") Sesion.EstadoGeneral estado, Pageable pageable);

    @Query("SELECT s FROM Sesion s LEFT JOIN FETCH s.entrenador LEFT JOIN FETCH s.sucursal LEFT JOIN FETCH s.cliente")
    Page<Sesion> findAllWithJoins(Pageable pageable);

    Optional<Sesion> findFirstByEntrenadorIdAndFechaGreaterThanEqualOrderByFechaAsc(Long entrenadorId, java.time.LocalDate fecha);

    @EntityGraph(attributePaths = {"entrenador", "sucursal", "cliente"})
    @Query("SELECT s FROM Sesion s WHERE s.entrenador.id = :entrenadorId AND s.fecha >= :fecha ORDER BY s.fecha ASC")
    List<Sesion> findProximasByEntrenadorId(@Param("entrenadorId") Long entrenadorId, @Param("fecha") java.time.LocalDate fecha);

    @EntityGraph(attributePaths = {"entrenador", "sucursal", "cliente"})
    Page<Sesion> findByEntrenadorId(Long entrenadorId, Pageable pageable);

    @Query("SELECT s FROM Sesion s LEFT JOIN FETCH s.entrenador LEFT JOIN FETCH s.sucursal LEFT JOIN FETCH s.cliente WHERE s.entrenador.id = :entrenadorId")
    Page<Sesion> findByEntrenadorIdWithJoins(@Param("entrenadorId") Long entrenadorId, Pageable pageable);

    @Query("SELECT s FROM Sesion s LEFT JOIN FETCH s.entrenador LEFT JOIN FETCH s.sucursal LEFT JOIN FETCH s.cliente WHERE s.entrenador.id = :entrenadorId AND s.estado = :estado")
    Page<Sesion> findByEntrenadorIdAndEstado(@Param("entrenadorId") Long entrenadorId,
                                              @Param("estado") Sesion.EstadoGeneral estado,
                                              Pageable pageable);
}
