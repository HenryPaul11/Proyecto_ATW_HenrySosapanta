package com.powerfit.repository;

import com.powerfit.entity.Plan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    @EntityGraph(attributePaths = {"sucursal"})
    List<Plan> findBySucursalIdAndEstado(Long sucursalId, Plan.EstadoGeneral estado);

    @EntityGraph(attributePaths = {"sucursal"})
    List<Plan> findBySucursalId(Long sucursalId);

    boolean existsBySucursalIdAndNombrePlan(Long sucursalId, String nombrePlan);
}
