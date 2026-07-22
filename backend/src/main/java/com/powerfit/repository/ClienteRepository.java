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

    @Query(value = """
        SELECT c.cliente_id, c.documento_identidad, c.email, c.estado,
               c.fecha_actualizacion, c.fecha_registro, c.genero,
               c.nombre_completo, c.password_hash, c.sucursal_id,
               c.telefono, c.usuario_id
        FROM clientes c
        WHERE c.estado = 'ACTIVO'
          AND (:sucursalId IS NULL OR c.sucursal_id = :sucursalId)
        ORDER BY c.nombre_completo
    """, countQuery = """
        SELECT COUNT(*)
        FROM clientes c
        WHERE c.estado = 'ACTIVO'
          AND (:sucursalId IS NULL OR c.sucursal_id = :sucursalId)
    """, nativeQuery = true)
    Page<Cliente> buscar(@Param("sucursalId") Long sucursalId, Pageable pageable);

    @Query(value = """
        SELECT c.cliente_id, c.documento_identidad, c.email, c.estado,
               c.fecha_actualizacion, c.fecha_registro, c.genero,
               c.nombre_completo, c.password_hash, c.sucursal_id,
               c.telefono, c.usuario_id
        FROM clientes c
        WHERE c.estado = 'ACTIVO'
          AND (:sucursalId IS NULL OR c.sucursal_id = :sucursalId)
          AND (LOWER(c.nombre_completo) LIKE LOWER(('%' || :q || '%'))
               OR c.documento_identidad LIKE ('%' || :q || '%'))
        ORDER BY c.nombre_completo
    """, countQuery = """
        SELECT COUNT(*)
        FROM clientes c
        WHERE c.estado = 'ACTIVO'
          AND (:sucursalId IS NULL OR c.sucursal_id = :sucursalId)
          AND (LOWER(c.nombre_completo) LIKE LOWER(('%' || :q || '%'))
               OR c.documento_identidad LIKE ('%' || :q || '%'))
    """, nativeQuery = true)
    Page<Cliente> buscarConBusqueda(@Param("q") String q,
                                    @Param("sucursalId") Long sucursalId,
                                    Pageable pageable);

    @Query(value = """
        SELECT c.cliente_id, c.documento_identidad, c.email, c.estado,
               c.fecha_actualizacion, c.fecha_registro, c.genero,
               c.nombre_completo, c.password_hash, c.sucursal_id,
               c.telefono, c.usuario_id
        FROM clientes c
        LEFT JOIN membresias m
          ON m.cliente_id = c.cliente_id AND m.fecha_fin >= CURRENT_DATE AND m.estado = 'ACTIVA'
        WHERE c.estado = 'ACTIVO'
          AND m.membresia_id IS NULL
          AND (:sucursalId IS NULL OR c.sucursal_id = :sucursalId)
        ORDER BY c.nombre_completo
    """, countQuery = """
        SELECT COUNT(*)
        FROM clientes c
        LEFT JOIN membresias m
          ON m.cliente_id = c.cliente_id AND m.fecha_fin >= CURRENT_DATE AND m.estado = 'ACTIVA'
        WHERE c.estado = 'ACTIVO'
          AND m.membresia_id IS NULL
          AND (:sucursalId IS NULL OR c.sucursal_id = :sucursalId)
    """, nativeQuery = true)
    Page<Cliente> sinMembresia(@Param("sucursalId") Long sucursalId, Pageable pageable);

    @EntityGraph(attributePaths = {"sucursal"})
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.email) = LOWER(:email)")
    Optional<Cliente> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {"sucursal"})
    List<Cliente> findBySucursalId(Long sucursalId);

    long countByEstado(Cliente.EstadoGeneral estado);
}
