package com.proyectovegeta.seminario.controller;

import com.proyectovegeta.seminario.dto.ServiceUnitCreationRequest;
import com.proyectovegeta.seminario.dto.ServiceUnitHireRequest;
import com.proyectovegeta.seminario.model.UnidadServicio;
import com.proyectovegeta.seminario.service.JwtUtil;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ServiceUnitControllerTest {

    @Mock
    private ServiceUnitManager serviceUnitManager;

    @Mock
    private JwtUtil jwtUtil;

    private ServiceUnitController serviceUnitController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serviceUnitController = new ServiceUnitController(serviceUnitManager, jwtUtil);
    }


    @Test
    void createServiceUnitWithInvalidJwt() {
        ServiceUnitCreationRequest request = new ServiceUnitCreationRequest();
        request.setName("Service Unit Name");
        String authHeader = "Bearer jwt";

        when(serviceUnitManager.createServiceUnit(any(), any(), any())).thenReturn(new UnidadServicio());

        ResponseEntity<String> response = serviceUnitController.createServiceUnit(request, authHeader);

        assertEquals(400, response.getStatusCodeValue());
    }


    @Test
    void hireEmployeeWithInvalidJwt() {
        ServiceUnitHireRequest request = ServiceUnitHireRequest.builder()
                .employeeID(1L).build();
        String authHeader = "Bearer jwt";

        when(jwtUtil.decodeToken(anyString())).thenReturn(null);

        ResponseEntity<String> response = serviceUnitController.hireEmployee(request, authHeader, 1L);

        assertEquals(400, response.getStatusCodeValue());
    }
}