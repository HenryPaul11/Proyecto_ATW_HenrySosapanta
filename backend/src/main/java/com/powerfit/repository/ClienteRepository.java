package com.powerfit.repository;

import com.powerfit.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // ── Búsquedas simples ────────────────────────────────────────────────────
    Optional<Cliente> findByCedulaAndActivoTrue(String cedula);
    Optional<Cliente> findByEmailAndActivoTrue(String email);
    boolean existsByCedula(String cedula);
    boolean existsByEmail(String email);
    boolean existsByCedulaAndIdNot(String cedula, Integer id);
    boolean existsByEmailAndIdNot(String email, Integer id);

    // ── Solo activos ─────────────────────────────────────────────────────────
    Page<Cliente> findByActivoTrue(Pageable pageable);
    List<Cliente> findByActivoTrue();

    // ── Paginación con búsqueda por nombre/apellido/cédula ───────────────────
    @Query("""
        SELECT c FROM Cliente c
        WHERE c.activo = true
          AND (:busqueda IS NULL
               OR LOWER(c.nombre)   LIKE LOWER(CONCAT('%', CAST(:busqueda AS string), '%'))
               OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', CAST(:busqueda AS string), '%'))
               OR c.cedula          LIKE CONCAT('%', CAST(:busqueda AS string), '%'))
    """)
    Page<Cliente> buscarActivos(@Param("busqueda") String busqueda, Pageable pageable);

    // ── Sin membresía vigente ────────────────────────────────────────────────
    @Query("""
        SELECT c FROM Cliente c
        WHERE c.activo = true
          AND c.id NOT IN (
              SELECT m.cliente.id FROM Membresia m
              WHERE m.fechaFin >= CURRENT_DATE
          )
    """)
    List<Cliente> findClientesSinMembresia();

    // ── Conteo de activos ────────────────────────────────────────────────────
    long countByActivoTrue();
}
