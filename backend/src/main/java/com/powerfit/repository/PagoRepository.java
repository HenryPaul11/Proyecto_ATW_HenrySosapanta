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
    @Query("SELECT p FROM Pago p WHERE p.estado <> 'ANULADO' AND (:sucursalId IS NULL OR p.sucursal.id = :sucursalId) ORDER BY p.fechaPago DESC")
    Page<Pago> findBySucursal(@Param("sucursalId") Long sucursalId, Pageable pageable);

    @EntityGraph(attributePaths = {"cliente", "membresia", "membresia.plan"})
    @Query("SELECT p FROM Pago p WHERE p.estado <> 'ANULADO' AND p.cliente.id = :clienteId ORDER BY p.fechaPago DESC")
    List<Pago> findActivosByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.estado = 'COMPLETADO' AND (:sid IS NULL OR p.sucursal.id = :sid)")
    BigDecimal sumIngresos(@Param("sid") Long sucursalId);

    @Query("SELECT AVG(p.monto) FROM Pago p WHERE p.estado = 'COMPLETADO'")
    BigDecimal avgMonto();

    @Query("SELECT COUNT(p) FROM Pago p WHERE (:sucursalId IS NULL OR p.sucursal.id = :sucursalId)")
    long countBySucursalId(@Param("sucursalId") Long sucursalId);
}
