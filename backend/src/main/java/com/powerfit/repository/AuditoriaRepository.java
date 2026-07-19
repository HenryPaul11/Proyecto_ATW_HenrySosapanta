package com.powerfit.repository;

import com.powerfit.entity.Auditoria;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @Query(value = """
        SELECT a.* FROM auditoria a
        LEFT JOIN sucursales s ON s.sucursal_id = a.sucursal_id
        JOIN usuarios u ON u.usuario_id = a.usuario_id
        WHERE (:sucursalId IS NULL OR a.sucursal_id = :sucursalId)
          AND (:tabla IS NULL OR a.tabla_afectada = :tabla)
          AND (:accion IS NULL OR UPPER(a.accion) = UPPER(:accion))
          AND (:usuario IS NULL OR LOWER(u.nombre_completo) LIKE LOWER(CONCAT('%', :usuario, '%')))
        ORDER BY a.fecha_hora DESC
        """, nativeQuery = true)
    List<Auditoria> findFiltered(@Param("sucursalId") Long sucursalId,
                                 @Param("tabla")      String tabla,
                                 @Param("accion")     String accion,
                                 @Param("usuario")    String usuario);
}
