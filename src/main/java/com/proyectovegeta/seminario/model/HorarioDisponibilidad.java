package com.proyectovegeta.seminario.model;

import jakarta.persistence.*;

import java.sql.Time;

@Entity
@Table(name = "\"HorariosDisponibilidad\"")
public class HorarioDisponibilidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "horario_disponibilidad_id")
    private Long horarioDisponibilidadId;

    @Column(name = "tipo_recurso_id", nullable = false)
    private Long tipoRecursoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private Time horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private Time horaFin;

    // Getters and setters
    public Long getHorarioDisponibilidadId() {
        return horarioDisponibilidadId;
    }

    public void setHorarioDisponibilidadId(Long horarioDisponibilidadId) {
        this.horarioDisponibilidadId = horarioDisponibilidadId;
    }

    public Long getTipoRecursoId() {
        return tipoRecursoId;
    }

    public void setTipoRecursoId(Long tipoRecursoId) {
        this.tipoRecursoId = tipoRecursoId;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Time getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Time horaFin) {
        this.horaFin = horaFin;
    }
}