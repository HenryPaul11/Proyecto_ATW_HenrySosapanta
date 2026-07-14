package com.powerfit.repository;

import com.powerfit.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    @Query("""
        SELECT a FROM Auditoria a
        WHERE (:usuario IS NULL OR a.usuario = :usuario)
          AND (:tabla   IS NULL OR a.tablaAfectada = :tabla)
          AND (:accion  IS NULL OR CAST(a.accion AS string) = :accion)
        ORDER BY a.fechaHora DESC
    """)
    List<Auditoria> findWithFilters(
            @Param("usuario") String usuario,
            @Param("tabla")   String tabla,
            @Param("accion")  String accion
    );
}
