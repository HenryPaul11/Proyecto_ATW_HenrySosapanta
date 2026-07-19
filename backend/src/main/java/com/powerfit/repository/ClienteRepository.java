package com.powerfit.repository;

import com.powerfit.entity.Cliente;
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
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @EntityGraph(attributePaths = {"sucursal"})
    Optional<Cliente> findByDocumentoIdentidadAndEstado(String doc, Cliente.EstadoGeneral estado);

    boolean existsByDocumentoIdentidad(String doc);
    boolean existsByDocumentoIdentidadAndIdNot(String doc, Long id);

    @EntityGraph(attributePaths = {"sucursal"})
    @Query("""
        SELECT c FROM Cliente c
        WHERE c.estado = 'ACTIVO'
          AND (:sucursalId IS NULL OR c.sucursal.id = :sucursalId)
          AND (:q IS NULL
               OR LOWER(c.nombreCompleto) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))
               OR c.documentoIdentidad    LIKE CONCAT('%', CAST(:q AS string), '%'))
    """)
    Page<Cliente> buscar(@Param("q") String q,
                         @Param("sucursalId") Long sucursalId,
                         Pageable pageable);

    @EntityGraph(attributePaths = {"sucursal"})
    @Query("""
        SELECT c FROM Cliente c
        WHERE c.estado = 'ACTIVO'
          AND (:sucursalId IS NULL OR c.sucursal.id = :sucursalId)
          AND c.id NOT IN (
              SELECT m.cliente.id FROM Membresia m
              WHERE m.fechaFin >= CURRENT_DATE AND m.estado = 'ACTIVA'
          )
    """)
    List<Cliente> sinMembresia(@Param("sucursalId") Long sucursalId);

    @EntityGraph(attributePaths = {"sucursal"})
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.email) = LOWER(:email)")
    Optional<Cliente> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {"sucursal"})
    List<Cliente> findBySucursalId(Long sucursalId);

    long countByEstado(Cliente.EstadoGeneral estado);
}
