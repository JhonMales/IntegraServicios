package com.proyectovegeta.seminario.repository;

import com.proyectovegeta.seminario.model.UnidadServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadServicioRepository extends JpaRepository<UnidadServicio, Long> {
}