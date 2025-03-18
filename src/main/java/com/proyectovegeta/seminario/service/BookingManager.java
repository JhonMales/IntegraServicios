package com.proyectovegeta.seminario.service;

import com.proyectovegeta.seminario.model.*;
import com.proyectovegeta.seminario.repository.RecursoRepository;
import com.proyectovegeta.seminario.repository.ReservaRepository;
import com.proyectovegeta.seminario.model.*;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingManager {
    ReservaRepository reservaRepository;
    RecursoRepository recursoRepository;
    ServiceUnitManager serviceUnitManager;
    ResourceTypeManager resourceTypeManager;
    public BookingManager(ReservaRepository reservaRepository, RecursoRepository recursoRepository, ServiceUnitManager serviceUnitManager, ResourceTypeManager resourceTypeManager) {
        this.reservaRepository = reservaRepository;
        this.recursoRepository = recursoRepository;
        this.serviceUnitManager = serviceUnitManager;
        this.resourceTypeManager = resourceTypeManager;
    }
    public Reserva bookResource(Long userId, Long resourceId, Date date, Time start, Time end) {
        //Validates start time is before end time
        if (start.compareTo(end) >= 0) {
            throw new RuntimeException("La hora de inicio debe ser antes de la hora de fin");
        }
        //Check date is today or later
        if (date.toLocalDate().isBefore(Date.valueOf(LocalDate.now()).toLocalDate())) {
            throw new RuntimeException("La fecha de reserva debe ser hoy o después");
        }
        //Check resource exists
        Recurso resource = recursoRepository.findById(resourceId).orElseThrow(()->new RuntimeException("Recurso no encontrado"));
        //Check resource is available
        List<Reserva> bookings = reservaRepository.findByRecursoId(resourceId);
        bookings.removeIf(booking -> booking.getFechaReserva().compareTo(date) != 0);
        for (Reserva booking : bookings) {
            if (start.compareTo(booking.getHoraInicioReserva()) >= 0 && start.compareTo(booking.getHoraFinReserva()) < 0) {
                throw new RuntimeException("Recurso no disponible");
            }
            if (end.compareTo(booking.getHoraInicioReserva()) > 0 && end.compareTo(booking.getHoraFinReserva()) <= 0) {
                throw new RuntimeException("Recurso no disponible");
            }
        }
        //Check user exists and is not an employee
        Optional<Empleado> empleado = serviceUnitManager.checkActiveEmployee(userId, resource.getUnidadId());
        if (empleado.isPresent()) {
            throw new RuntimeException("El usuario es un empleado de la unidad de servicio");
        }
        //Check the booking time is larger than the minimum booking time
        TipoRecurso resourceType = resourceTypeManager.getResourceType(resource.getTipoRecursoId());
        Duration bookingDuration = Duration.between(start.toLocalTime(), end.toLocalTime());
        Duration minimumBookingDuration = Duration.between(LocalTime.MIDNIGHT, resourceType.getTiempoMinimoPrestamo().toLocalTime());
        if (bookingDuration.compareTo(minimumBookingDuration)<0) {
            throw new RuntimeException("El tiempo de reserva es menor al tiempo mínimo de reserva");
        }
        //Check the booking time is within the available schedule
        DiaSemana diaSemana = DiaSemana.values()[date.toLocalDate().getDayOfWeek().getValue()-1];
        HorarioDisponibilidad availability = resourceTypeManager.getScheduleByDay(resource.getTipoRecursoId(),diaSemana);
        if (availability == null) {
            throw new RuntimeException("El recurso no está disponible en el día de la reserva");
        }
        if (start.compareTo(availability.getHoraInicio()) < 0 || end.compareTo(availability.getHoraFin()) > 0) {
            throw new RuntimeException("El recurso no está disponible en el horario de la reserva");
        }
        //Create booking
        Reserva booking = new Reserva();
        booking.setUsuarioId(userId);
        booking.setRecursoId(resourceId);
        booking.setFechaReserva(date);
        booking.setHoraInicioReserva(start);
        booking.setHoraFinReserva(end);
        booking.setEstado(EstadoTransaccion.activa);
        return reservaRepository.save(booking);
    }

    public Reserva getBooking(Long bookingId) {
        return reservaRepository.findById(bookingId).orElseThrow(()-> new RuntimeException("Reserva no encontrada"));
    }
    public List<Reserva> getBookingsByResource(Long resourceId) {
        return reservaRepository.findByRecursoId(resourceId);
    }
    public List<Reserva> getBookingsByUser(Long userId) {
        return reservaRepository.findByUsuarioId(userId);
    }

    public Reserva cancelBooking(Long bookingId) {
        Reserva booking = reservaRepository.findById(bookingId).orElseThrow(()->new RuntimeException("Reserva no encontrada"));
        if (booking.getEstado().equals(EstadoTransaccion.cancelada)) {
            throw new RuntimeException("Reserva ya cancelada");
        }
        booking.setEstado(EstadoTransaccion.cancelada);
        return reservaRepository.save(booking);
    }
}
