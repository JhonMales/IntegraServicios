package com.proyectovegeta.seminario.controller;

import com.proyectovegeta.seminario.model.Empleado;
import com.proyectovegeta.seminario.model.Prestamo;
import com.proyectovegeta.seminario.service.JwtUtil;
import com.proyectovegeta.seminario.service.LoanService;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.util.Map;

@RestController
public class LoanController {
    private final LoanService loanService;
    private final ServiceUnitManager serviceUnitManager;
    private final JwtUtil jwtUtil;

    public LoanController(LoanService loanService, ServiceUnitManager serviceUnitManager, JwtUtil jwtUtil) {
        this.loanService = loanService;
        this.serviceUnitManager = serviceUnitManager;
        this.jwtUtil = jwtUtil;
    }

    // Check in a book
    @PostMapping("/booking/{id}/checkin")
    public ResponseEntity<Prestamo> checkInBook(@PathVariable("id") Long bookingId, @RequestHeader("Authorization") String authHeader){
        try{
            String jwt = authHeader.replace("Bearer ", "");
            Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
            Long userId = Long.parseLong(decodedToken.get("id"));
            Empleado empleado = serviceUnitManager.getActiveEmployment(userId);
            if(empleado == null){
                return ResponseEntity.badRequest().build();
            }
            Prestamo prestamo = loanService.checkIn(bookingId, empleado.getEmpleadoId(), new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.ok(prestamo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // Check out a book
    @PutMapping("/loan/{id}/checkout")
    public ResponseEntity<Prestamo> checkOutBook(@PathVariable("id") Long bookingId, @RequestHeader("Authorization") String authHeader){
        try{
            String jwt = authHeader.replace("Bearer ", "");
            Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
            Long userId = Long.parseLong(decodedToken.get("id"));
            Empleado empleado = serviceUnitManager.getActiveEmployment(userId);
            if(empleado == null){
                return ResponseEntity.badRequest().build();
            }
            Prestamo prestamo = loanService.checkOut(bookingId, empleado.getEmpleadoId(), new Timestamp(System.currentTimeMillis()));
            return ResponseEntity.ok(prestamo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // Get loan by id
    @GetMapping("/loan/{id}")
    public ResponseEntity<Prestamo> getLoanById(@PathVariable("id") Long bookingId){
        try{
            Prestamo prestamo = loanService.getLoanById(bookingId);
            return ResponseEntity.ok(prestamo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
