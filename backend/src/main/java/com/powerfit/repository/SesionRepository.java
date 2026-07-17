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
public interface SesionRepository extends JpaRepository<Sesion, Long> {

    @Query("SELECT s FROM Sesion s WHERE :sucursalId IS NULL OR s.sucursal.id = :sucursalId")
    List<Sesion> findBySucursal(@Param("sucursalId") Long sucursalId);

    List<Sesion> findByEntrenadorId(Long entrenadorId);

    @Query("SELECT s FROM Sesion s WHERE :estado IS NULL OR s.estado = :estado")
    Page<Sesion> findByEstadoOptional(@Param("estado") Sesion.EstadoGeneral estado, Pageable pageable);
}
