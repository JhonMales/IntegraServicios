package com.proyectovegeta.seminario.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "\"Prestamos\"")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prestamo_id")
    private Long prestamoId;

    @Column(name = "reserva_id", nullable = false, unique = true)
    private Long reservaId;

    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Column(name = "hora_entrega", nullable = false)
    private Timestamp horaEntrega;

    @Column(name = "hora_devolucion")
    private Timestamp horaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoTransaccion estado;

    // Getters and setters
    public Long getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(Long prestamoId) {
        this.prestamoId = prestamoId;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public Timestamp getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(Timestamp horaEntrega) {
        this.horaEntrega = horaEntrega;
    }

    public Timestamp getHoraDevolucion() {
        return horaDevolucion;
    }

    public void setHoraDevolucion(Timestamp horaDevolucion) {
        this.horaDevolucion = horaDevolucion;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    public void setEstado(EstadoTransaccion estado) {
        this.estado = estado;
    }
}