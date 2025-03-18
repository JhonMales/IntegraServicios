package com.proyectovegeta.seminario.controller;

import com.proyectovegeta.seminario.dto.ResourceTypeCreationRequest;
import com.proyectovegeta.seminario.dto.ScheduleCreationRequest;
import com.proyectovegeta.seminario.model.DiaSemana;
import com.proyectovegeta.seminario.model.HorarioDisponibilidad;
import com.proyectovegeta.seminario.model.TipoRecurso;
import com.proyectovegeta.seminario.service.ResourceTypeManager;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.Time;

@RestController
public class ResourceTypeController {
    private final ResourceTypeManager resourceTypeManager;
    private final ServiceUnitManager serviceUnitManager;
    public ResourceTypeController(ResourceTypeManager resourceTypeManager, ServiceUnitManager serviceUnitManager){
        this.resourceTypeManager = resourceTypeManager;
        this.serviceUnitManager = serviceUnitManager;
    }
    //POST method for resource type creation
    @PostMapping("/resourcetype")
    public ResponseEntity<String> createResourceType(@RequestBody ResourceTypeCreationRequest request, @RequestHeader("Authorization") String authHeader){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            //Validates request
            if(!serviceUnitManager.validateRequest(jwt, request.getServiceUnitID())){
                return ResponseEntity.badRequest().body("Usuario no autorizado para realizar esta acción");
            }
            Long serviceUnitID = request.getServiceUnitID();
            String name = request.getName();
            String description = request.getDescription();
            Time minLoanTime = request.getMinLoanTime();
            TipoRecurso tipoRecurso = resourceTypeManager.createResourceType(serviceUnitID, name, description, minLoanTime);
            return ResponseEntity.created(new URI("/resourcetype/" + tipoRecurso.getTipoRecursoId())).body("Tipo de recurso creado exitosamente!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //GET method for all resource types
    @GetMapping("/resourcetype")
    public ResponseEntity<?> getResourceTypes(@RequestHeader("Authorization") String authHeader){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(resourceTypeManager.getResourceTypes());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //GET method for resource type by ID
    @GetMapping("/resourcetype/{id}")
    public ResponseEntity<?> getResourceTypeByID(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(resourceTypeManager.getResourceType(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //GET method for resource types by service unit
    @GetMapping("/serviceunit/{service-unit-id}/resourcetype")
    public ResponseEntity<?> getServiceUnitResourceTypes(@RequestHeader("Authorization") String authHeader, @PathVariable("service-unit-id") Long serviceUnitID){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(resourceTypeManager.getServiceUnitResourceTypes(serviceUnitID));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    //POST method for assigning a schedule to a resource type
    @PostMapping("/resourcetype/{id}/schedule")
    public ResponseEntity<String> assignSchedule(@PathVariable Long id, @RequestBody ScheduleCreationRequest request, @RequestHeader("Authorization") String authHeader){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            TipoRecurso tipoRecurso = resourceTypeManager.getResourceType(id);

            //Validates request
            if(!serviceUnitManager.validateRequest(jwt, tipoRecurso.getUnidadId())) {
                return ResponseEntity.badRequest().body("Usuario no autorizado para realizar esta acción");
            }

            HorarioDisponibilidad horarioDisponibilidad = resourceTypeManager.assignSchedule(id, request.getDayOfWeek(), request.getStartTime(), request.getEndTime());
            return ResponseEntity.created(new URI("/resourcetype/" + id + "/schedule/" + horarioDisponibilidad.getHorarioDisponibilidadId())).body("Horario asignado exitosamente!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //GET method for all schedules of a resource type
    @GetMapping("/resourcetype/{id}/schedule")
    public ResponseEntity<?> getResourceTypeSchedules(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(resourceTypeManager.getResourceTypeSchedules(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    //GET method for a schedule by ID
    @GetMapping("/resourcetype/{id}/schedule/{schedule-id}")
    public ResponseEntity<?> getSchedule(@PathVariable Long id, @PathVariable("schedule-id") Long scheduleID, @RequestHeader("Authorization") String authHeader){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            TipoRecurso tipoRecurso = resourceTypeManager.getResourceType(id);
            //Validates request
            if(!serviceUnitManager.validateRequest(jwt, tipoRecurso.getUnidadId())) {
                return ResponseEntity.badRequest().body("Usuario no autorizado para realizar esta acción");
            }
            return ResponseEntity.ok(resourceTypeManager.getSchedule(scheduleID));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    //GET method for a schedule by day and resource type
    @GetMapping("/resourcetype/{id}/schedule/day/{day}")
    public ResponseEntity<?> getScheduleByDay(@PathVariable Long id, @PathVariable DiaSemana day, @RequestHeader("Authorization") String authHeader){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            TipoRecurso tipoRecurso = resourceTypeManager.getResourceType(id);
            //Validates request
            if(!serviceUnitManager.validateRequest(jwt, tipoRecurso.getUnidadId())) {
                return ResponseEntity.badRequest().body("Usuario no autorizado para realizar esta acción");
            }
            return ResponseEntity.ok(resourceTypeManager.getScheduleByDay(id, day));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
