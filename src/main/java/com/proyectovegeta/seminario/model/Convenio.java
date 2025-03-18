package com.proyectovegeta.seminario.model;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Convenios\"")
public class Convenio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "convenio_id")
    private Long convenioId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    public Long getConvenioId() {
        return convenioId;
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
}
