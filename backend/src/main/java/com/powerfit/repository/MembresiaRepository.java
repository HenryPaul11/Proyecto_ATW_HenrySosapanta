package com.powerfit.repository;

import com.powerfit.entity.Membresia;
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
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    @EntityGraph(attributePaths = {"plan", "cliente", "sucursal"})
    List<Membresia> findByClienteId(Long clienteId);

    @EntityGraph(attributePaths = {"plan", "cliente", "sucursal"})
    Optional<Membresia> findFirstByClienteIdAndEstadoOrderByFechaFinDesc(Long clienteId, Membresia.EstadoMembresia estado);

    default Optional<Membresia> findPrimeraActivaByClienteId(Long clienteId) {
        return findFirstByClienteIdAndEstadoOrderByFechaFinDesc(clienteId, Membresia.EstadoMembresia.ACTIVA);
    }

    // Sin filtro de sucursal
    @EntityGraph(attributePaths = {"plan", "cliente"})
    Page<Membresia> findByEstado(Membresia.EstadoMembresia estado, Pageable pageable);

    // Con filtro de sucursal
    @EntityGraph(attributePaths = {"plan", "cliente"})
    Page<Membresia> findBySucursalIdAndEstado(Long sucursalId,
                                              Membresia.EstadoMembresia estado,
                                              Pageable pageable);

    // Sin estado ni sucursal
    @EntityGraph(attributePaths = {"plan", "cliente"})
    Page<Membresia> findAll(Pageable pageable);

    // Solo por sucursal
    @EntityGraph(attributePaths = {"plan", "cliente"})
    Page<Membresia> findBySucursalId(Long sucursalId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Membresia m WHERE m.estado = 'ACTIVA' AND (:sid IS NULL OR m.sucursal.id = :sid)")
    Long countActivas(@Param("sid") Long sucursalId);
}
