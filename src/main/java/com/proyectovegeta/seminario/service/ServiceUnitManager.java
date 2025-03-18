package com.proyectovegeta.seminario.service;

import com.proyectovegeta.seminario.model.Empleado;
import com.proyectovegeta.seminario.model.UnidadServicio;
import com.proyectovegeta.seminario.model.Usuario;
import com.proyectovegeta.seminario.repository.EmpleadoRepository;
import com.proyectovegeta.seminario.repository.UnidadServicioRepository;
import com.proyectovegeta.seminario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceUnitManager {

    private final UnidadServicioRepository unidadServicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final JwtUtil jwtUtil;

    public ServiceUnitManager(UnidadServicioRepository unidadServicioRepository, UsuarioRepository usuarioRepository, EmpleadoRepository empleadoRepository, JwtUtil jwtUtil) {
        this.unidadServicioRepository = unidadServicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.jwtUtil = jwtUtil;
    }

    //Creates a new service unit
    public UnidadServicio createServiceUnit(String name, Time startWorkingHours, Time endWorkingHours) {
        //Checks that the working hours are valid
        if (startWorkingHours.after(endWorkingHours)) {
            throw new RuntimeException("El horario de inicio no puede ser después del horario de fin");
        }
        UnidadServicio unidadServicio = new UnidadServicio();
        unidadServicio.setNombre(name);
        unidadServicio.setHorarioLaboralInicio(startWorkingHours);
        unidadServicio.setHorarioLaboralFin(endWorkingHours);
        return unidadServicioRepository.save(unidadServicio);
    }

    //Hires an employee for a service unit
    public Empleado hireEmployee(Long userId, Long serviceUnitId, String cargo) {
        Usuario usuario = usuarioRepository.findById(userId).orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        UnidadServicio serviceUnit = unidadServicioRepository.findById(serviceUnitId).orElseThrow(() -> new RuntimeException("Unidad de servicio no encontrada"));
        //Checks if the user is already an active employee of the service unit
        List<Empleado> serviceUnitEmployees = empleadoRepository.findByUnidadId(serviceUnitId);
        //filter by user id
        serviceUnitEmployees.removeIf(e -> !e.getUsuarioId().equals(userId));
        //filter by status
        serviceUnitEmployees.removeIf(e -> !e.getStatus());
        if (!serviceUnitEmployees.isEmpty()) {
            throw new RuntimeException("El usuario ya es un empleado");
        }
        if(usuario.getRol().equals("invitado")||usuario.getRol().equals("externo")){
            throw new RuntimeException("El usuario no puede ser contratado");
        }
        Empleado empleado = new Empleado();
        empleado.setUsuarioId(usuario.getUsuarioId());
        empleado.setUnidadId(serviceUnit.getUnidadId());
        empleado.setCargo(cargo);
        return empleadoRepository.save(empleado);
    }


    //Checks if the user is an active employee of the service unit
    public Optional<Empleado> checkActiveEmployee(Long userId, Long serviceUnitId) {
        List<Empleado> serviceUnitEmployees = empleadoRepository.findByUnidadId(serviceUnitId);
        //filter by user_id
        serviceUnitEmployees.removeIf(e -> !e.getUsuarioId().equals(userId));
        //filter by status
        serviceUnitEmployees.removeIf(e -> !e.getStatus());
        if (serviceUnitEmployees.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(serviceUnitEmployees.get(0));
    }

    //Gets all active employees of a service unit
    public List<Empleado> getServiceUnitActiveEmployees(Long serviceUnitID) {
        List<Empleado> serviceUnitEmployees = empleadoRepository.findByUnidadId(serviceUnitID);
        //Filter the active ones
        serviceUnitEmployees.removeIf(e -> !e.getStatus());
        return serviceUnitEmployees;
    }

    //Gets the active employment of a user
    public Empleado getActiveEmployment(Long userId) {
        List<Empleado> employees = empleadoRepository.findByUsuarioId(userId);
        //Filter the active ones
        employees.removeIf(e -> !e.getStatus());
        if (employees.isEmpty()){
            return null;
        }
        return employees.get(0);
    }

    //Get employee by id
    public Empleado getEmployeeById(Long employeeId) {
        return empleadoRepository.findById(employeeId).orElseThrow(()-> new RuntimeException("Empleado no encontrado"));
    }

    //Validate manager request
    public boolean validateRequest(String token, Long serviceUnitID){
        Map<String, String> decodedToken = jwtUtil.decodeToken(token);
        //Checks if the user is an admin or is an employee of the service unit
        if (!decodedToken.get("rol").equals("admin")) {
            Empleado empleado = checkActiveEmployee(Long.parseLong(decodedToken.get("id")), serviceUnitID).orElse(null);
            return empleado != null && empleado.getCargo().equals("gerente");
        }
        return true;
    }

    //Get current employer
    public Empleado getCurrentEmployer(Long userId) {
        List<Empleado> employees = empleadoRepository.findByUsuarioId(userId);
        //Filter the active ones
        employees.removeIf(e -> !e.getStatus());
        if (employees.isEmpty()){
            return null;
        }
        return employees.get(0);
    }

    public Map<String, Time> getWorkingHoursForServiceUnit(Long unidadId) {
        UnidadServicio unidadServicio = getServiceUnit(unidadId);  // Obtiene la unidad de servicio
        Map<String, Time> workingHours = new HashMap<>();
        workingHours.put("start", unidadServicio.getHorarioLaboralInicio());
        workingHours.put("end", unidadServicio.getHorarioLaboralFin());
        return workingHours;
    }

    public List<UnidadServicio> getAllServiceUnits() {
        return unidadServicioRepository.findAll();
    }

    public UnidadServicio getServiceUnit(Long id) {
        return unidadServicioRepository.findById(id).orElseThrow(() -> new RuntimeException("Unidad de servicio no encontrada"));
    }
}