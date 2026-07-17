package com.powerfit.repository;

import com.powerfit.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @Query("""
        SELECT a FROM Auditoria a
        WHERE (:sucursalId IS NULL OR a.sucursal.id = :sucursalId)
          AND (:tabla IS NULL     OR a.tablaAfectada = :tabla)
          AND (:accion IS NULL    OR CAST(a.accion AS string) = :accion)
          AND (:usuario IS NULL   OR LOWER(a.usuario.nombreCompleto) LIKE LOWER(CONCAT('%', :usuario, '%')))
        ORDER BY a.fechaHora DESC
    """)
    List<Auditoria> findFiltered(@Param("sucursalId") Long sucursalId,
                                 @Param("tabla")      String tabla,
                                 @Param("accion")     String accion,
                                 @Param("usuario")    String usuario);
}
