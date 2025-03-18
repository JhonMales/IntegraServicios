package com.proyectovegeta.seminario.service;

import com.proyectovegeta.seminario.model.Empleado;
import com.proyectovegeta.seminario.model.EstadoTransaccion;
import com.proyectovegeta.seminario.model.Prestamo;
import com.proyectovegeta.seminario.model.Reserva;
import com.proyectovegeta.seminario.repository.EmpleadoRepository;
import com.proyectovegeta.seminario.repository.PrestamoRepository;
import com.proyectovegeta.seminario.repository.ReservaRepository;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;

@Service
public class LoanService {
    private final PrestamoRepository prestamoRepository;
    private final ReservaRepository reservaRepository;
    private final ServiceUnitManager serviceUnitManager;
    private final EmpleadoRepository empleadoRepository;
    public LoanService(PrestamoRepository prestamoRepository, ReservaRepository reservaRepository, ServiceUnitManager serviceUnitManager, EmpleadoRepository empleadoRepository) {
        this.prestamoRepository = prestamoRepository;
        this.reservaRepository = reservaRepository;
        this.serviceUnitManager = serviceUnitManager;
        this.empleadoRepository = empleadoRepository;
    }

    public Prestamo checkIn(Long reservaId, Long empleadoId, Timestamp horaEntrega) {
        // Retrieve the Reserva from the repository
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        if (reserva.getEstado().equals(EstadoTransaccion.cancelada)) {
            throw new RuntimeException("Reserva cancelada");
        }
        // Retrieve the Empleado from the repository
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        if (serviceUnitManager.checkActiveEmployee(empleado.getEmpleadoId(), empleado.getUnidadId()).isEmpty()) {
            throw new RuntimeException("Empleado no activo");
        }

        // Set booking as completed
        reserva.setEstado(EstadoTransaccion.completada);

        // Create a new Prestamo
        Prestamo prestamo = new Prestamo();
        prestamo.setReservaId(reserva.getReservaId());
        prestamo.setEmpleadoId(empleado.getEmpleadoId());
        prestamo.setHoraEntrega(horaEntrega);
        prestamo.setEstado(EstadoTransaccion.activa);

        // Save the Prestamo to the repository
        return prestamoRepository.save(prestamo);
    }

    public Prestamo checkOut(Long prestamoId, Long empleadoId, Timestamp horaDevolucion) {
        // Retrieve the Prestamo from the repository
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));

        // Retrieve the Empleado from the repository
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        if (serviceUnitManager.checkActiveEmployee(empleado.getEmpleadoId(), empleado.getUnidadId()).isEmpty()) {
            throw new RuntimeException("Empleado no activo");
        }
        // Update the Prestamo
        prestamo.setHoraDevolucion(horaDevolucion);
        prestamo.setEstado(EstadoTransaccion.completada);

        // Save the updated Prestamo to the repository
        return prestamoRepository.save(prestamo);
    }

    public Prestamo getLoanById(Long bookingId) {
        return prestamoRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));
    }
}
