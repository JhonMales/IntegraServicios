package com.proyectovegeta.seminario.model;

import jakarta.persistence.*;

import java.sql.Time;
import java.time.Duration;

@Entity
@Table(name = "\"Tipo_Recurso\"")
public class TipoRecurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_recurso_id")
    private Long tipoRecursoId;

    @Column(name = "unidad_id", nullable = false)
    private Long unidadId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "tiempo_minimo_prestamo", nullable = false)
    private Time tiempoMinimoPrestamo;

    // Getters and setters
    public Long getTipoRecursoId() {
        return tipoRecursoId;
    }

    public void setTipoRecursoId(Long tipoRecursoId) {
        this.tipoRecursoId = tipoRecursoId;
    }

    public Long getUnidadId() {
        return unidadId;
    }

    public void setUnidadId(Long unidadId) {
        this.unidadId = unidadId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Time getTiempoMinimoPrestamo() {
        return tiempoMinimoPrestamo;
    }

    public void setTiempoMinimoPrestamo(Time tiempoMinimoPrestamo) {
        this.tiempoMinimoPrestamo = tiempoMinimoPrestamo;
    }
}