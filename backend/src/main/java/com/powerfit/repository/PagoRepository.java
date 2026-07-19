package com.powerfit.repository;

import com.powerfit.entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    @EntityGraph(attributePaths = {"cliente", "membresia", "membresia.plan"})
    List<Pago> findByClienteId(Long clienteId);

    @EntityGraph(attributePaths = {"cliente", "membresia", "membresia.plan"})
    @Query("SELECT p FROM Pago p WHERE :sucursalId IS NULL OR p.sucursal.id = :sucursalId ORDER BY p.fechaPago DESC")
    Page<Pago> findBySucursal(@Param("sucursalId") Long sucursalId, Pageable pageable);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.estado = 'COMPLETADO' AND (:sid IS NULL OR p.sucursal.id = :sid)")
    BigDecimal sumIngresos(@Param("sid") Long sucursalId);

    @Query("SELECT AVG(p.monto) FROM Pago p WHERE p.estado = 'COMPLETADO'")
    BigDecimal avgMonto();
}
