package com.proyectovegeta.seminario.controller;

import com.proyectovegeta.seminario.model.Empleado;
import com.proyectovegeta.seminario.model.Prestamo;
import com.proyectovegeta.seminario.service.JwtUtil;
import com.proyectovegeta.seminario.service.LoanService;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @Mock
    private ServiceUnitManager serviceUnitManager;

    @Mock
    private JwtUtil jwtUtil;

    private LoanController loanController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanController = new LoanController(loanService, serviceUnitManager, jwtUtil);
    }

    @Test
    void checkInBookSuccessfully() {
        String jwt = "Bearer jwt";
        Map<String, String> decodedToken = new HashMap<>();
        decodedToken.put("id", "1");
        Empleado empleado = new Empleado();
        Prestamo prestamo = new Prestamo();

        when(jwtUtil.decodeToken(anyString())).thenReturn(decodedToken);
        when(serviceUnitManager.getActiveEmployment(anyLong())).thenReturn(empleado);
        when(loanService.checkIn(anyLong(), anyLong(), any())).thenReturn(prestamo);

        ResponseEntity<Prestamo> response = loanController.checkInBook(1L, jwt);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void checkInBookWithInvalidJwt() {
        String jwt = "Bearer jwt";

        when(jwtUtil.decodeToken(anyString())).thenReturn(null);

        ResponseEntity<Prestamo> response = loanController.checkInBook(1L, jwt);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void checkOutBookSuccessfully() {
        String jwt = "Bearer jwt";
        Map<String, String> decodedToken = new HashMap<>();
        decodedToken.put("id", "1");
        Empleado empleado = new Empleado();
        Prestamo prestamo = new Prestamo();

        when(jwtUtil.decodeToken(anyString())).thenReturn(decodedToken);
        when(serviceUnitManager.getActiveEmployment(anyLong())).thenReturn(empleado);
        when(loanService.checkOut(anyLong(), anyLong(), any())).thenReturn(prestamo);

        ResponseEntity<Prestamo> response = loanController.checkOutBook(1L, jwt);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void checkOutBookWithInvalidJwt() {
        String jwt = "Bearer jwt";

        when(jwtUtil.decodeToken(anyString())).thenReturn(null);

        ResponseEntity<Prestamo> response = loanController.checkOutBook(1L, jwt);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void getLoanByIdSuccessfully() {
        Prestamo prestamo = new Prestamo();

        when(loanService.getLoanById(anyLong())).thenReturn(prestamo);

        ResponseEntity<Prestamo> response = loanController.getLoanById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(prestamo, response.getBody());
    }
}