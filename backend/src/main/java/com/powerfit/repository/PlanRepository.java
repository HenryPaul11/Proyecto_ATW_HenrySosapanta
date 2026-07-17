package com.powerfit.repository;

import com.powerfit.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findBySucursalIdAndEstado(Long sucursalId, Plan.EstadoGeneral estado);
    List<Plan> findBySucursalId(Long sucursalId);
    boolean existsBySucursalIdAndNombrePlan(Long sucursalId, String nombrePlan);
}
