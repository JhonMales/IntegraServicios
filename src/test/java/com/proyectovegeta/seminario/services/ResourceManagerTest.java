package com.proyectovegeta.seminario.services;

import com.proyectovegeta.seminario.model.Recurso;
import com.proyectovegeta.seminario.model.TipoRecurso;
import com.proyectovegeta.seminario.model.UnidadServicio;
import com.proyectovegeta.seminario.repository.RecursoRepository;
import com.proyectovegeta.seminario.repository.TipoRecursoRepository;
import com.proyectovegeta.seminario.repository.UnidadServicioRepository;
import com.proyectovegeta.seminario.service.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResourceManagerTest {

    @Mock
    TipoRecursoRepository tipoRecursoRepository;

    @Mock
    RecursoRepository recursoRepository;

    @Mock
    UnidadServicioRepository unidadServicioRepository;

    @InjectMocks
    ResourceManager resourceManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createResourceSuccessfully() {
        when(unidadServicioRepository.findById(anyLong())).thenReturn(Optional.of(new UnidadServicio()));
        when(tipoRecursoRepository.findById(anyLong())).thenReturn(Optional.of(new TipoRecurso()));
        when(recursoRepository.save(any(Recurso.class))).thenReturn(new Recurso());

        Recurso result = resourceManager.createResource(1L, 1L, "name", "description");

        assertNotNull(result);
        verify(recursoRepository, times(1)).save(any(Recurso.class));
    }

    @Test
    void getResourceSuccessfully() {
        when(recursoRepository.findById(anyLong())).thenReturn(Optional.of(new Recurso()));

        Recurso result = resourceManager.getResource(1L);

        assertNotNull(result);
    }

    @Test
    void getResourceByServiceUnitSuccessfully() {
        when(recursoRepository.findByUnidadId(anyLong())).thenReturn(Collections.singletonList(new Recurso()));

        var result = resourceManager.getResourceByServiceUnit(1L);

        assertFalse(result.isEmpty());
    }

    @Test
    void getResourceByTypeSuccessfully() {
        when(recursoRepository.findByTipoRecursoId(anyLong())).thenReturn(Collections.singletonList(new Recurso()));

        var result = resourceManager.getResourceByType(1L);

        assertFalse(result.isEmpty());
    }

    @Test
    void getAllResourcesSuccessfully() {
        when(recursoRepository.findAll()).thenReturn(Collections.singletonList(new Recurso()));

        var result = resourceManager.getAllResources();

        assertFalse(result.isEmpty());
    }
}