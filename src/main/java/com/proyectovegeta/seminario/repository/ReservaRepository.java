package com.proyectovegeta.seminario.repository;

import com.proyectovegeta.seminario.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByRecursoId(Long recursoId);
    List<Reserva> findByUsuarioId(Long usuarioId);
}
