package com.powerfit.repository;

import com.powerfit.entity.Membresia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findByClienteId(Long clienteId);

    @Query("""
        SELECT m FROM Membresia m
        WHERE (:sucursalId IS NULL OR m.sucursal.id = :sucursalId)
          AND (:estado IS NULL OR CAST(m.estado AS string) = :estado)
        ORDER BY m.fechaFin ASC
    """)
    Page<Membresia> findFiltered(@Param("sucursalId") Long sucursalId,
                                 @Param("estado") String estado,
                                 Pageable pageable);

    @Query("SELECT COUNT(m) FROM Membresia m WHERE m.estado = 'ACTIVA' AND (:sid IS NULL OR m.sucursal.id = :sid)")
    Long countActivas(@Param("sid") Long sucursalId);
}
