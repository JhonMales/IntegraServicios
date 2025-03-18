package com.proyectovegeta.seminario.repository;

import com.proyectovegeta.seminario.model.TipoRecurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoRecursoRepository extends JpaRepository<TipoRecurso, Long> {
    List<TipoRecurso> findByUnidadId(Long serviceUnitID);
    TipoRecurso findByTipoRecursoId(Long tipoRecursoId);
}
