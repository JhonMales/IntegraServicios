package com.proyectovegeta.seminario.controller;

import com.proyectovegeta.seminario.model.Reserva;
import com.proyectovegeta.seminario.service.BookingManager;
import com.proyectovegeta.seminario.service.JwtUtil;
import com.proyectovegeta.seminario.service.ResourceManager;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class BookingControllerTest {

    @Mock
    private BookingManager bookingManager;

    @Mock
    private ResourceManager resourceManager;

    @Mock
    private ServiceUnitManager serviceUnitManager;

    @Mock
    private JwtUtil jwtUtil;

    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingController = new BookingController(bookingManager, serviceUnitManager, resourceManager, jwtUtil);
    }

    @Test
    void getBookingsWithInvalidJwt() {
        String authHeader = "Bearer jwt";

        when(jwtUtil.decodeToken(anyString())).thenReturn(null);

        ResponseEntity<List<Reserva>> response = bookingController.getBookings(1L, authHeader);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void getBookingsByUserSuccessfully() {
        String authHeader = "Bearer jwt";
        Map<String, String> decodedToken = new HashMap<>();
        decodedToken.put("id", "1");

        when(jwtUtil.decodeToken(anyString())).thenReturn(decodedToken);
        when(bookingManager.getBookingsByUser(anyLong())).thenReturn(List.of());

        ResponseEntity<List<Reserva>> response = bookingController.getBookingsByUser(1L, authHeader);

        assertEquals(200, response.getStatusCodeValue());
    }
}