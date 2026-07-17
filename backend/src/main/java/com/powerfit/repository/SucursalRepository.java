package com.powerfit.repository;

import com.powerfit.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {

    List<Sucursal> findByEstado(Sucursal.EstadoGeneral estado);

    @Query("SELECT u.sucursal FROM Usuario u WHERE u.id = :usuarioId")
    Optional<Sucursal> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    boolean existsByNombre(String nombre);

    boolean existsByCodigo(String codigo);

    boolean existsByNombreAndIdNot(String nombre, Long id);

}
