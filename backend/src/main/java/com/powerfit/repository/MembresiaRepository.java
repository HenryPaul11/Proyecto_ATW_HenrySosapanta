package com.powerfit.repository;

import com.powerfit.entity.Membresia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {

    List<Membresia> findByClienteId(Integer clienteId);

    @Query("SELECT m FROM Membresia m WHERE m.fechaFin >= :hoy")
    List<Membresia> findActivas(@Param("hoy") LocalDate hoy);

    @Query("SELECT m FROM Membresia m WHERE m.fechaFin < :hoy")
    List<Membresia> findVencidas(@Param("hoy") LocalDate hoy);

    // Paginación
    @Query("SELECT m FROM Membresia m WHERE m.fechaFin >= :hoy")
    Page<Membresia> findActivasPaged(@Param("hoy") LocalDate hoy, Pageable pageable);

    @Query("SELECT m FROM Membresia m WHERE m.fechaFin < :hoy")
    Page<Membresia> findVencidasPaged(@Param("hoy") LocalDate hoy, Pageable pageable);

    Page<Membresia> findByClienteId(Integer clienteId, Pageable pageable);
}
