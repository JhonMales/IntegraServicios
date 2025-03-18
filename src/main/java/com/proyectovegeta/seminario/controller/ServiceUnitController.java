package com.proyectovegeta.seminario.controller;

import com.proyectovegeta.seminario.dto.ServiceUnitCreationRequest;
import com.proyectovegeta.seminario.dto.ServiceUnitHireRequest;
import com.proyectovegeta.seminario.model.Empleado;
import com.proyectovegeta.seminario.model.UnidadServicio;
import com.proyectovegeta.seminario.service.JwtUtil;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
public class ServiceUnitController {
    private final ServiceUnitManager serviceUnitManager;
    private final JwtUtil jwtUtil;
    public ServiceUnitController(ServiceUnitManager serviceUnitManager, JwtUtil jwtUtil) {
        this.serviceUnitManager = serviceUnitManager;
        this.jwtUtil = jwtUtil;
    }

    // Create service unit endpoint
    @PostMapping("/serviceunit")
    public ResponseEntity<String> createServiceUnit(@RequestBody ServiceUnitCreationRequest request, @RequestHeader("Authorization") String authHeader){
        try {
            //Decodes jwt
            String jwt = authHeader.replace("Bearer ", "");
            Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
            //Checks if the user is an admin
            if (!decodedToken.get("rol").equals("admin")) {
                return ResponseEntity.badRequest().body("Usuario no autorizado para realizar esta acción");
            }
            UnidadServicio unidadServicio = serviceUnitManager.createServiceUnit(request.getName(), request.getStartWorkingHours(), request.getEndWorkingHours());
            return ResponseEntity.created(new URI("/serviceunit/" + unidadServicio.getUnidadId())).body("Unidad de servicio creada exitosamente!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Hire employee endpoint
    @PostMapping("/serviceunit/{serviceUnitID}/hire")
    public ResponseEntity<String> hireEmployee(@RequestBody ServiceUnitHireRequest request, @RequestHeader("Authorization") String authHeader, @PathVariable Long serviceUnitID){
        try {
            //Decodes jwt
            String jwt = authHeader.replace("Bearer ", "");
            Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
            //Checks if the user is an admin or an employee
            if (!decodedToken.get("rol").equals("admin")) {
                Empleado empleado = serviceUnitManager.checkActiveEmployee(Long.parseLong(decodedToken.get("id")),serviceUnitID).orElseThrow(() -> new RuntimeException("Usuario no autorizado para realizar esta acción"));
                if (!empleado.getCargo().equals("Gerente")) {
                    return ResponseEntity.badRequest().body("Usuario no autorizado para realizar esta acción");
                }
            }
            Empleado nuevoEmpleado = serviceUnitManager.hireEmployee(request.getEmployeeID(), serviceUnitID, request.getPosition());
            return ResponseEntity.created(new URI("/serviceunit/" + serviceUnitID + "/employee/" + nuevoEmpleado.getEmpleadoId())).body("Empleado contratado exitosamente!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Get all employees of a service unit
    @GetMapping("/serviceunit/{serviceUnitID}/employee")
    public ResponseEntity<?> getEmployees(@RequestHeader("Authorization") String authHeader, @PathVariable Long serviceUnitID){
        try {
            //Decodes jwt
            String jwt = authHeader.replace("Bearer ", "");
            Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
            //Checks if the user is an admin or an employee
            if (!decodedToken.get("rol").equals("admin")) {
                Empleado empleado = serviceUnitManager.checkActiveEmployee(Long.parseLong(decodedToken.get("id")),serviceUnitID).orElseThrow(() -> new RuntimeException("Usuario no autorizado para realizar esta acción"));
                if (!empleado.getCargo().equals("gerente")) {
                    return ResponseEntity.badRequest().body("Usuario no autorizado para realizar esta acción");
                }
            }
            return ResponseEntity.ok(serviceUnitManager.getServiceUnitActiveEmployees(serviceUnitID));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Get an employee by ID
    @GetMapping("/serviceunit/{serviceUnitID}/employee/{employeeID}")
    public ResponseEntity<?> getEmployee(@RequestHeader("Authorization") String jwt, @PathVariable Long serviceUnitID, @PathVariable Long employeeID) {
        try {
            //Decodes jwt
            Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
            //Get the employee
            Empleado empleado = serviceUnitManager.getEmployeeById(employeeID);
            //Checks if the user is an admin or manager at the service unit
            if (!decodedToken.get("rol").equals("admin")) {
                if (empleado.getUnidadId().equals(serviceUnitID)) {
                    Empleado manager = serviceUnitManager.checkActiveEmployee(Long.parseLong(decodedToken.get("id")), serviceUnitID).orElseThrow(() -> new RuntimeException("Usuario no autorizado para realizar esta acción"));
                    if (!manager.getCargo().equals("gerente")) {
                        return ResponseEntity.badRequest().body("Usuario no autorizado para realizar esta acción");
                    }
                } else {
                    return ResponseEntity.badRequest().body("Usuario no autorizado para realizar esta acción");
                }
            }
            return ResponseEntity.ok(empleado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //Get all service units
    @GetMapping("/serviceunit")
    public ResponseEntity<List<UnidadServicio>> getServiceUnits(){
        try {
            return ResponseEntity.ok(serviceUnitManager.getAllServiceUnits());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // Get service unit by id
    @GetMapping("/serviceunit/{id}")
    public ResponseEntity<UnidadServicio> getServiceUnit(@PathVariable("id") Long id){
        try {
            return ResponseEntity.ok(serviceUnitManager.getServiceUnit(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
