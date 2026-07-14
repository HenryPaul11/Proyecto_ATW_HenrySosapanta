package com.powerfit.repository;

import com.powerfit.entity.TipoMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TipoMembresiaRepository extends JpaRepository<TipoMembresia, Integer> {
    List<TipoMembresia> findByActivoTrue();
}
