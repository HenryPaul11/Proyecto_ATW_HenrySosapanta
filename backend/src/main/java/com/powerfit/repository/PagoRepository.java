package com.powerfit.repository;

import com.powerfit.entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByClienteId(Integer clienteId);

    Page<Pago> findAll(Pageable pageable);
    Page<Pago> findByClienteId(Integer clienteId, Pageable pageable);

    @Query("SELECT p FROM Pago p WHERE :sucursalId IS NULL OR p.sucursal.id = :sucursalId ORDER BY p.fechaPago DESC")
    Page<Pago> findBySucursal(@org.springframework.data.repository.query.Param("sucursalId") Integer sucursalId, Pageable pageable);

    @Query("SELECT SUM(p.monto) FROM Pago p")
    BigDecimal sumTotalIngresos();

    @Query("SELECT AVG(p.monto) FROM Pago p")
    BigDecimal avgMonto();

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.metodoPago = 'efectivo'")
    Long countEfectivo();

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.metodoPago = 'tarjeta'")
    Long countTarjeta();

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.metodoPago = 'transferencia'")
    Long countTransferencia();

    @Query("""
        SELECT SUM(p.monto) FROM Pago p
        WHERE MONTH(p.fechaPago) = MONTH(CURRENT_DATE)
          AND YEAR(p.fechaPago)  = YEAR(CURRENT_DATE)
    """)
    BigDecimal sumIngresosMesActual();
}
