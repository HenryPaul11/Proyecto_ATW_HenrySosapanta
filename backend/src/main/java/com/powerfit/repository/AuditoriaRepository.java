package com.powerfit.repository;

import com.powerfit.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @Query(value = """
        SELECT a.* FROM auditoria a
        LEFT JOIN usuarios u ON u.usuario_id = a.usuario_id
        ORDER BY a.fecha_hora DESC
        """, nativeQuery = true)
    List<Auditoria> findAllOrdered();
}
