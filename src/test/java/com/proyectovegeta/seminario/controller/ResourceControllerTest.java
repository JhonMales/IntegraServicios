package com.proyectovegeta.seminario.controller;

import com.proyectovegeta.seminario.dto.ResourceCreationRequest;
import com.proyectovegeta.seminario.model.Recurso;
import com.proyectovegeta.seminario.service.JwtUtil;
import com.proyectovegeta.seminario.service.ResourceManager;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ResourceControllerTest {

    @Mock
    private ResourceManager resourceManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ServiceUnitManager serviceUnitManager;

    private ResourceController resourceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceController = new ResourceController(resourceManager, jwtUtil, serviceUnitManager);
    }

    @Test
    void createResourceSuccessfully() {
        ResourceCreationRequest request = new ResourceCreationRequest();
        request.setServiceUnitID(1L);
        request.setResourceTypeID(1L);
        request.setName("Resource Name");
        request.setDescription("Resource Description");
        String authHeader = "Bearer jwt";

        when(serviceUnitManager.validateRequest(anyString(), anyLong())).thenReturn(true);
        when(resourceManager.createResource(anyLong(), anyLong(), anyString(), anyString())).thenReturn(new Recurso());

        ResponseEntity<String> response = resourceController.createResource(request, authHeader);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Recurso creado exitosamente!", response.getBody());
    }

    @Test
    void createResourceWithInvalidJwt() {
        ResourceCreationRequest request = new ResourceCreationRequest();
        request.setServiceUnitID(1L);
        request.setResourceTypeID(1L);
        request.setName("Resource Name");
        request.setDescription("Resource Description");
        String authHeader = "Bearer jwt";

        when(serviceUnitManager.validateRequest(anyString(), anyLong())).thenReturn(false);

        ResponseEntity<String> response = resourceController.createResource(request, authHeader);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void getResourcesSuccessfully() {
        String authHeader = "Bearer jwt";
        Map<String, String> decodedToken = new HashMap<>();
        decodedToken.put("id", "1");

        when(jwtUtil.decodeToken(anyString())).thenReturn(decodedToken);
        when(resourceManager.getAllResources()).thenReturn(List.of());

        ResponseEntity<?> response = resourceController.getResources(authHeader);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getResourceByIDSuccessfully() {
        String authHeader = "Bearer jwt";

        when(jwtUtil.decodeToken(anyString())).thenReturn(new HashMap<>());
        when(resourceManager.getResource(anyLong())).thenReturn(new Recurso());

        ResponseEntity<?> response = resourceController.getResourceByID(authHeader, 1L);

        assertEquals(200, response.getStatusCodeValue());
    }
}