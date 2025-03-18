package com.proyectovegeta.seminario.repository;

import com.proyectovegeta.seminario.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
}
