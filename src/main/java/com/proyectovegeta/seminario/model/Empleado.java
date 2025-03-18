package com.proyectovegeta.seminario.model;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Empleados\"")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empleado_id")
    private Long empleadoId;

    @Column(name = "usuario_id", nullable = false)
    private  Long usuarioId;

    @Column(name = "unidad_id", nullable = false)
    private Long unidadId;

    @Column(name = "cargo", nullable = false, length = 50)
    private String cargo;

    @Column(name = "status", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean status;

    // Getters and setters
    public Long getEmpleadoId() {
        return empleadoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getUnidadId() {
        return unidadId;
    }

    public void setUnidadId(Long unidadId) {
        this.unidadId = unidadId;
    }

    public String getCargo() {
        return cargo;
    }
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
}