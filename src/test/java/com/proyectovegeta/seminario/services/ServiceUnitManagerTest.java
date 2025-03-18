package com.proyectovegeta.seminario.services;

import com.proyectovegeta.seminario.model.Empleado;
import com.proyectovegeta.seminario.model.UnidadServicio;
import com.proyectovegeta.seminario.model.Usuario;
import com.proyectovegeta.seminario.repository.EmpleadoRepository;
import com.proyectovegeta.seminario.repository.UnidadServicioRepository;
import com.proyectovegeta.seminario.repository.UsuarioRepository;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Time;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceUnitManagerTest {

    @Mock
    UnidadServicioRepository unidadServicioRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    EmpleadoRepository empleadoRepository;

    @InjectMocks
    ServiceUnitManager serviceUnitManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createServiceUnitSuccessfully() {
        when(unidadServicioRepository.save(any(UnidadServicio.class))).thenReturn(new UnidadServicio());

        UnidadServicio result = serviceUnitManager.createServiceUnit("name", Time.valueOf("08:00:00"), Time.valueOf("17:00:00"));

        assertNotNull(result);
        verify(unidadServicioRepository, times(1)).save(any(UnidadServicio.class));
    }

    @Test
void hireEmployeeSuccessfully() {
    Usuario usuario = new Usuario();
    usuario.setRol("employee");

    when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
    when(unidadServicioRepository.findById(anyLong())).thenReturn(Optional.of(new UnidadServicio()));
    when(empleadoRepository.findByUnidadId(anyLong())).thenReturn(Collections.emptyList());
    when(empleadoRepository.save(any(Empleado.class))).thenReturn(new Empleado());

    Empleado result = serviceUnitManager.hireEmployee(1L, 1L, "cargo");

    assertNotNull(result);
    verify(empleadoRepository, times(1)).save(any(Empleado.class));
}

    @Test
    void checkActiveEmployeeSuccessfully() {
        Empleado empleado = new Empleado();
        empleado.setUsuarioId(1L);
        empleado.setStatus(true);

        when(empleadoRepository.findByUnidadId(anyLong())).thenReturn(new ArrayList<>(Collections.singletonList(empleado)));

        Optional<Empleado> result = serviceUnitManager.checkActiveEmployee(1L, 1L);

        assertTrue(result.isPresent());
    }

      @Test
      void getServiceUnitActiveEmployeesSuccessfully() {
          Empleado empleado = new Empleado();
          empleado.setUsuarioId(1L);
          empleado.setStatus(true);
            when(empleadoRepository.findByUnidadId(anyLong())).thenReturn(new ArrayList<>(Collections.singletonList(empleado)));

            var result = serviceUnitManager.getServiceUnitActiveEmployees(1L);

            assertFalse(result.isEmpty());
        }
}