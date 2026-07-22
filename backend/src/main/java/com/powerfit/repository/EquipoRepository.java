package com.powerfit.repository;

import com.powerfit.entity.Equipo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    boolean existsByNumeroSerie(String serie);

    @EntityGraph(attributePaths = {"sucursal", "categoria"})
    @Query("SELECT e FROM Equipo e WHERE :sucursalId IS NULL OR e.sucursal.id = :sucursalId")
    List<Equipo> findBySucursal(@Param("sucursalId") Long sucursalId);

    @Query("SELECT COUNT(e) FROM Equipo e WHERE e.estado = 'OPERATIVO'")
    Long countOperativos();

    @Query("SELECT COALESCE(SUM(e.valorAdquisicion), 0) FROM Equipo e WHERE e.estado != 'DADO_DE_BAJA' AND YEAR(e.fechaActualizacion) = YEAR(CURRENT_DATE()) AND MONTH(e.fechaActualizacion) = MONTH(CURRENT_DATE()) AND (:sid IS NULL OR e.sucursal.id = :sid)")
    BigDecimal sumValorAdquisicionMesActual(@Param("sid") Long sucursalId);
}
