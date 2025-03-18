package com.proyectovegeta.seminario.model;

import jakarta.persistence.*;

import java.sql.Time;

@Entity
@Table(name = "\"Unidad_Servicio\"")
public class UnidadServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unidad_id")
    private Long unidadId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "horario_laboral_inicio")
    private Time horarioLaboralInicio;

    @Column(name = "horario_laboral_fin")
    private Time horarioLaboralFin;

    // Getters and setters
    public Long getUnidadId() {
        return unidadId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Time getHorarioLaboralInicio() {
        return horarioLaboralInicio;
    }

    public void setHorarioLaboralInicio(Time horarioLaboralInicio) {
        this.horarioLaboralInicio = horarioLaboralInicio;
    }

    public Time getHorarioLaboralFin() {
        return horarioLaboralFin;
    }

    public void setHorarioLaboralFin(Time horarioLaboralFin) {
        this.horarioLaboralFin = horarioLaboralFin;
    }
}