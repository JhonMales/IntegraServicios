package com.proyectovegeta.seminario.controller;

import com.proyectovegeta.seminario.dto.ResourceTypeCreationRequest;
import com.proyectovegeta.seminario.model.TipoRecurso;
import com.proyectovegeta.seminario.service.ResourceTypeManager;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.sql.Time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ResourceTypeControllerTest {

    @Mock
    private ResourceTypeManager resourceTypeManager;

    @Mock
    private ServiceUnitManager serviceUnitManager;

    private ResourceTypeController resourceTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceTypeController = new ResourceTypeController(resourceTypeManager, serviceUnitManager);
    }

    @Test
    void createResourceTypeSuccessfully() {
        ResourceTypeCreationRequest request = new ResourceTypeCreationRequest();
        request.setServiceUnitID(1L);
        request.setName("Resource Type Name");
        request.setDescription("Resource Type Description");
        request.setMinLoanTime(Time.valueOf("01:00:00"));
        String authHeader = "Bearer jwt";

        when(serviceUnitManager.validateRequest(anyString(), anyLong())).thenReturn(true);
        when(resourceTypeManager.createResourceType(anyLong(), anyString(), anyString(), any())).thenReturn(new TipoRecurso());

        ResponseEntity<String> response = resourceTypeController.createResourceType(request, authHeader);

        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void createResourceTypeWithInvalidJwt() {
        ResourceTypeCreationRequest request = new ResourceTypeCreationRequest();
        request.setServiceUnitID(1L);
        request.setName("Resource Type Name");
        request.setDescription("Resource Type Description");
        request.setMinLoanTime(Time.valueOf("01:00:00"));
        String authHeader = "Bearer jwt";

        when(serviceUnitManager.validateRequest(anyString(), anyLong())).thenReturn(false);

        ResponseEntity<String> response = resourceTypeController.createResourceType(request, authHeader);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void getResourceTypesSuccessfully() {
        String authHeader = "Bearer jwt";

        ResponseEntity<?> response = resourceTypeController.getResourceTypes(authHeader);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getResourceTypeByIDSuccessfully() {
        String authHeader = "Bearer jwt";

        when(resourceTypeManager.getResourceType(anyLong())).thenReturn(new TipoRecurso());

        ResponseEntity<?> response = resourceTypeController.getResourceTypeByID(authHeader, 1L);

        assertEquals(200, response.getStatusCodeValue());
    }
}