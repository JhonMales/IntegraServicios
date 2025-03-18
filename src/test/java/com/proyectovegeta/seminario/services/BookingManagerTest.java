package com.proyectovegeta.seminario.services;

import com.proyectovegeta.seminario.model.EstadoTransaccion;
import com.proyectovegeta.seminario.model.HorarioDisponibilidad;
import com.proyectovegeta.seminario.model.Recurso;
import com.proyectovegeta.seminario.model.Reserva;
import com.proyectovegeta.seminario.model.TipoRecurso;
import com.proyectovegeta.seminario.repository.RecursoRepository;
import com.proyectovegeta.seminario.repository.ReservaRepository;

import com.proyectovegeta.seminario.service.BookingManager;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import com.proyectovegeta.seminario.service.ResourceTypeManager;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingManagerTest {

    @Mock
    ReservaRepository reservaRepository;

    @Mock
    RecursoRepository recursoRepository;

    @Mock
    ServiceUnitManager serviceUnitManager;

    @Mock
    ResourceTypeManager resourceTypeManager;

    @InjectMocks
    BookingManager bookingManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void bookResourceSuccessfully() {
        TipoRecurso tipoRecurso = new TipoRecurso();
        tipoRecurso.setTiempoMinimoPrestamo(Time.valueOf("00:30:00"));
        Recurso recurso = new Recurso();
        recurso.setTipoRecursoId(1L);
        HorarioDisponibilidad horarioDisponibilidad = new HorarioDisponibilidad();
        horarioDisponibilidad.setHoraInicio(Time.valueOf(LocalTime.of(8, 0)));
        horarioDisponibilidad.setHoraFin(Time.valueOf(LocalTime.of(20, 0)));

        when(recursoRepository.findById(anyLong())).thenReturn(Optional.of(recurso));
        when(reservaRepository.findByRecursoId(anyLong())).thenReturn(Collections.emptyList());
        when(serviceUnitManager.checkActiveEmployee(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(resourceTypeManager.getResourceType(anyLong())).thenReturn(tipoRecurso);
        when(resourceTypeManager.getScheduleByDay(anyLong(), any())).thenReturn(horarioDisponibilidad);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(new Reserva());

        Reserva result = bookingManager.bookResource(1L, 1L, Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()), Time.valueOf(LocalTime.now().plusHours(1)));

        assertNotNull(result);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void bookResourceFailsWhenStartIsAfterEnd() {
        assertThrows(RuntimeException.class, () -> bookingManager.bookResource(1L, 1L, Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now().plusHours(1)), Time.valueOf(LocalTime.now())));
    }

    @Test
    void bookResourceFailsWhenDateIsBeforeToday() {
        assertThrows(RuntimeException.class, () -> bookingManager.bookResource(1L, 1L, Date.valueOf(LocalDate.now().minusDays(1)), Time.valueOf(LocalTime.now()), Time.valueOf(LocalTime.now().plusHours(1))));
    }

    @Test
    void bookResourceFailsWhenResourceDoesNotExist() {
        when(recursoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookingManager.bookResource(1L, 1L, Date.valueOf(LocalDate.now()), Time.valueOf(LocalTime.now()), Time.valueOf(LocalTime.now().plusHours(1))));
    }

    @Test
    public void testGetBookingsByResource() {
        Long resourceId = 1L;

        Reserva reserva1 = new Reserva();
        reserva1.setRecursoId(resourceId);
        reserva1.setEstado(EstadoTransaccion.activa);
        reserva1.setFechaReserva(Date.valueOf(LocalDate.now()));
        reserva1.setHoraInicioReserva(Time.valueOf(LocalTime.now()));
        reserva1.setHoraFinReserva(Time.valueOf(LocalTime.now().plusHours(1)));

        Reserva reserva2 = new Reserva();
        reserva2.setRecursoId(resourceId);
        reserva2.setEstado(EstadoTransaccion.activa);
        reserva2.setFechaReserva(Date.valueOf(LocalDate.now()));
        reserva2.setHoraInicioReserva(Time.valueOf(LocalTime.now()));
        reserva2.setHoraFinReserva(Time.valueOf(LocalTime.now().plusHours(1)));

        when(reservaRepository.findByRecursoId(resourceId)).thenReturn(Arrays.asList(reserva1, reserva2));

        List<Reserva> result = bookingManager.getBookingsByResource(resourceId);

        assertEquals(2, result.size());
        assertEquals(reserva1, result.get(0));
        assertEquals(reserva2, result.get(1));
    }
}