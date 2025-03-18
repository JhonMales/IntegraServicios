package com.proyectovegeta.seminario.services;

import com.proyectovegeta.seminario.model.HorarioDisponibilidad;
import com.proyectovegeta.seminario.model.TipoRecurso;
import com.proyectovegeta.seminario.model.UnidadServicio;
import com.proyectovegeta.seminario.repository.HorarioDisponibilidadRepository;
import com.proyectovegeta.seminario.repository.TipoRecursoRepository;
import com.proyectovegeta.seminario.repository.UnidadServicioRepository;
import com.proyectovegeta.seminario.service.ResourceTypeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Time;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ResourceTypeManagerTest {

    @InjectMocks
    private ResourceTypeManager resourceTypeManager;

    @Mock
    private TipoRecursoRepository tipoRecursoRepository;

    @Mock
    private UnidadServicioRepository unidadServicioRepository;

    @Mock
    private HorarioDisponibilidadRepository horarioDisponibilidadRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnResourceTypesWhenServiceUnitIdIsProvided() {
        TipoRecurso tipoRecurso = new TipoRecurso();
        tipoRecurso.setTipoRecursoId(1L);
        tipoRecurso.setUnidadId(1L);
        tipoRecurso.setNombre("Proyector");
        tipoRecurso.setDescripcion("Proyector de alta definición");
        tipoRecurso.setTiempoMinimoPrestamo(Time.valueOf("02:00:00"));

        when(tipoRecursoRepository.findByUnidadId(1L)).thenReturn(Arrays.asList(tipoRecurso));

        List<TipoRecurso> actual = resourceTypeManager.getServiceUnitResourceTypes(1L);

        assertEquals(1, actual.size());
        assertEquals(tipoRecurso, actual.get(0));
    }

    @Test
    public void shouldCreateResourceTypeWhenValidDataIsProvided() {
        UnidadServicio serviceUnit = new UnidadServicio();
        serviceUnit.setNombre("Biblioteca");

        when(unidadServicioRepository.findById(1L)).thenReturn(java.util.Optional.of(serviceUnit));

        TipoRecurso tipoRecurso = new TipoRecurso();
        tipoRecurso.setUnidadId(serviceUnit.getUnidadId());
        tipoRecurso.setNombre("Proyector");
        tipoRecurso.setDescripcion("Proyector de alta definición");
        tipoRecurso.setTiempoMinimoPrestamo(Time.valueOf("02:00:00"));

        when(tipoRecursoRepository.save(any(TipoRecurso.class))).thenReturn(tipoRecurso);

        TipoRecurso actual = resourceTypeManager.createResourceType(1L, "Proyector", "Proyector de alta definición", Time.valueOf("02:00:00"));

        assertEquals(tipoRecurso, actual);
    }

    @Test
    public void testGetResourceTypeSchedules() {
        Long resourceTypeID = 1L;

        HorarioDisponibilidad horario1 = new HorarioDisponibilidad();
        horario1.setTipoRecursoId(resourceTypeID);
        horario1.setHoraInicio(Time.valueOf("08:00:00"));
        horario1.setHoraFin(Time.valueOf("17:00:00"));

        HorarioDisponibilidad horario2 = new HorarioDisponibilidad();
        horario2.setTipoRecursoId(resourceTypeID);
        horario2.setHoraInicio(Time.valueOf("09:00:00"));
        horario2.setHoraFin(Time.valueOf("18:00:00"));

        when(horarioDisponibilidadRepository.findByTipoRecursoId(resourceTypeID)).thenReturn(Arrays.asList(horario1, horario2));

        List<HorarioDisponibilidad> result = resourceTypeManager.getResourceTypeSchedules(resourceTypeID);

        assertEquals(2, result.size());
        assertEquals(horario1, result.get(0));
        assertEquals(horario2, result.get(1));
    }
}