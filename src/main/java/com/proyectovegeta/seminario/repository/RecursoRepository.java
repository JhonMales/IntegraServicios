package com.proyectovegeta.seminario.repository;

import com.proyectovegeta.seminario.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long>{
    List<Recurso> findByUnidadId(Long serviceUnitID);

    List<Recurso> findByTipoRecursoId(Long resourceTypeID);
}
