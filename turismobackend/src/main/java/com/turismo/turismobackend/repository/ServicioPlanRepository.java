package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.ServicioTuristico;
import com.turismo.turismobackend.model.ServicioPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ServicioPlanRepository extends JpaRepository<ServicioPlan, Long> {
    
    List<ServicioPlan> findByPlanId(Long planId);
    
    @Modifying
    @Transactional
    void deleteByPlanId(Long planId);
    
    List<ServicioPlan> findByServicioId(Long servicioId);
    
    List<ServicioPlan> findByPlanIdOrderByDiaDelPlanAscOrdenEnElDiaAsc(Long planId);
    
    List<ServicioPlan> findByPlanIdAndDiaDelPlan(Long planId, Integer dia);
    
    List<ServicioPlan> findByPlanIdAndEsOpcional(Long planId, Boolean esOpcional);
    
    List<ServicioPlan> findByPlanIdAndEsPersonalizable(Long planId, Boolean esPersonalizable);
    
    @Query("SELECT sp FROM ServicioPlan sp WHERE sp.plan.id = :planId AND sp.servicio.emprendedor.id = :emprendedorId")
    List<ServicioPlan> findByPlanIdAndEmprendedorId(@Param("planId") Long planId, 
                                                    @Param("emprendedorId") Long emprendedorId);
    
    @Query("SELECT sp FROM ServicioPlan sp WHERE sp.plan.id = :planId AND sp.servicio.tipo = :tipoServicio")
    List<ServicioPlan> findByPlanIdAndTipoServicio(@Param("planId") Long planId, 
                                                   @Param("tipoServicio") ServicioTuristico.TipoServicio tipoServicio);
}