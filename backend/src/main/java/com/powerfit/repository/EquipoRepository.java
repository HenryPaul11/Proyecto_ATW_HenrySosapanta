package com.powerfit.repository;

import com.powerfit.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Integer> {

    List<Equipo> findByCategoria(Equipo.Categoria categoria);

    @Query("SELECT e FROM Equipo e WHERE :sucursalId IS NULL OR e.sucursal.id = :sucursalId")
    List<Equipo> findBySucursal(@Param("sucursalId") Integer sucursalId);

    @Query("SELECT COUNT(e) FROM Equipo e WHERE e.estado = 'disponible'")
    Long countDisponibles();

    @Query("SELECT COUNT(e) FROM Equipo e WHERE e.estado = 'mantenimiento'")
    Long countMantenimiento();
}
