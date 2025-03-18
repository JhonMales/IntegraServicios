package com.proyectovegeta.seminario.repository;

import com.proyectovegeta.seminario.model.HorarioDisponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioDisponibilidadRepository extends JpaRepository<HorarioDisponibilidad, Long>{
    //Get all schedules by resource type
    List<HorarioDisponibilidad> findByTipoRecursoId(Long resourceTypeId);
}
