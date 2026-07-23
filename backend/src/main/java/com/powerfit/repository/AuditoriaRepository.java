package com.powerfit.repository;

import com.powerfit.entity.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @Query(value = """
        SELECT a.* FROM auditoria a
        LEFT JOIN usuarios u ON u.usuario_id = a.usuario_id
        WHERE (:sucursalId IS NULL OR a.sucursal_id = :sucursalId)
          AND (:tabla IS NULL OR LOWER(a.tabla_afectada) = LOWER(:tabla))
          AND (:accion IS NULL OR a.accion = :accion::accion_auditoria)
          AND (:usuario IS NULL OR LOWER(u.nombre_completo) LIKE LOWER(CONCAT('%', :usuario, '%')))
        ORDER BY a.fecha_hora DESC
        """,
        countQuery = """
        SELECT COUNT(*) FROM auditoria a
        LEFT JOIN usuarios u ON u.usuario_id = a.usuario_id
        WHERE (:sucursalId IS NULL OR a.sucursal_id = :sucursalId)
          AND (:tabla IS NULL OR LOWER(a.tabla_afectada) = LOWER(:tabla))
          AND (:accion IS NULL OR a.accion = :accion::accion_auditoria)
          AND (:usuario IS NULL OR LOWER(u.nombre_completo) LIKE LOWER(CONCAT('%', :usuario, '%')))
        """,
        nativeQuery = true)
    Page<Auditoria> findPaginated(
        @Param("sucursalId") Long sucursalId,
        @Param("tabla") String tabla,
        @Param("accion") String accion,
        @Param("usuario") String usuario,
        Pageable pageable
    );
}
