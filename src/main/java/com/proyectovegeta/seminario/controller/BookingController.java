package com.proyectovegeta.seminario.controller;

import com.proyectovegeta.seminario.dto.BookResourceRequest;
import com.proyectovegeta.seminario.model.Recurso;
import com.proyectovegeta.seminario.model.Reserva;
import com.proyectovegeta.seminario.model.TipoRecurso;
import com.proyectovegeta.seminario.model.UnidadServicio;
import com.proyectovegeta.seminario.repository.RecursoRepository;
import com.proyectovegeta.seminario.repository.ReservaRepository;
import com.proyectovegeta.seminario.repository.TipoRecursoRepository;
import com.proyectovegeta.seminario.service.BookingManager;
import com.proyectovegeta.seminario.service.JwtUtil;
import com.proyectovegeta.seminario.service.ResourceManager;
import com.proyectovegeta.seminario.service.ServiceUnitManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
public class BookingController {

    private final BookingManager bookingManager;
    private final ResourceManager resourceManager;
    private final ServiceUnitManager serviceUnitManager;
    private final JwtUtil jwtUtil;
    private final RecursoRepository recursoRepository;
    private final ReservaRepository reservaRepository;
    private final TipoRecursoRepository tipoRecursoRepository;

    // Constructor con @Autowired implícito
    public BookingController(BookingManager bookingManager, ServiceUnitManager serviceUnitManager, ResourceManager resourceManager, JwtUtil jwtUtil,
                             RecursoRepository recursoRepository, ReservaRepository reservaRepository, TipoRecursoRepository tipoRecursoRepository) {
        this.bookingManager = bookingManager;
        this.serviceUnitManager = serviceUnitManager;
        this.resourceManager = resourceManager;
        this.jwtUtil = jwtUtil;
        this.recursoRepository = recursoRepository;
        this.reservaRepository = reservaRepository;
        this.tipoRecursoRepository = tipoRecursoRepository;
    }


    @PostMapping("/resource/{id}/book")
    public ResponseEntity<String> bookResource(@RequestBody BookResourceRequest request, @PathVariable("id") Long resourceId, @RequestHeader("Authorization") String authHeader){
        try {
            System.out.println(request.getDate());
            String jwt = authHeader.replace("Bearer ", "");
            //Validates request
            Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
            if (!decodedToken.get("id").equals(request.getUserId().toString())) {
                Recurso resource = resourceManager.getResource(resourceId);
                if (serviceUnitManager.checkActiveEmployee(request.getUserId(), resource.getUnidadId()).isEmpty()){
                    return ResponseEntity.badRequest().body("El usuario no tiene permisos para reservar este recurso");
                }
            }
            Reserva reserva = bookingManager.bookResource(request.getUserId(), resourceId, request.getDate(), request.getStart(), request.getEnd());
            System.out.println(reserva.toString());
            return ResponseEntity.ok().body("Reserva creada con éxito. ID: " + reserva.getReservaId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/resource/{id}/bookings")
    public ResponseEntity<List<Reserva>> getBookings(@PathVariable("id") Long resourceId, @RequestHeader("Authorization") String authHeader){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            //Validates request
            Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
            Recurso resource = resourceManager.getResource(resourceId);
            if (serviceUnitManager.checkActiveEmployee(Long.parseLong(decodedToken.get("id")), resource.getUnidadId()).isEmpty()){
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().body(bookingManager.getBookingsByResource(resourceId));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{id}/bookings")
    public ResponseEntity<List<Reserva>> getBookingsByUser(@PathVariable("id") Long userId, @RequestHeader("Authorization") String authHeader){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            //Validates request
            Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
            if (!decodedToken.get("id").equals(userId.toString())) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().body(bookingManager.getBookingsByUser(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/booking/{id}")
    public ResponseEntity<Reserva> getBooking(@PathVariable("id") Long bookingId, @RequestHeader("Authorization") String authHeader){
        try {
            String jwt = authHeader.replace("Bearer ", "");
            //Validates request
            Map<String, String> decodedToken = jwtUtil.decodeToken(jwt);
            Reserva reserva = bookingManager.getBooking(bookingId);
            if (!decodedToken.get("id").equals(reserva.getUsuarioId().toString())) {
                Recurso resource = resourceManager.getResource(reserva.getRecursoId());
                if (serviceUnitManager.checkActiveEmployee(Long.parseLong(decodedToken.get("id")), resource.getUnidadId()).isEmpty()){
                    return ResponseEntity.badRequest().build();
                }
            }
            return ResponseEntity.ok().body(reserva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/resources/available")
    public ResponseEntity<List<Map<String, Object>>> getAvailableResources() {
        try {
            LocalDate currentDate = LocalDate.now();
            Time currentTime = Time.valueOf(LocalTime.now());
            List<Recurso> allResources = recursoRepository.findAll();
            List<Map<String, Object>> availableResources = new ArrayList<>();

            for (Recurso resource : allResources) {
                UnidadServicio unidadServicio = serviceUnitManager.getServiceUnit(resource.getUnidadId());
                if (unidadServicio == null) {
                    continue;
                }
                Map<String, Time> workingHours = serviceUnitManager.getWorkingHoursForServiceUnit(resource.getUnidadId());
                Time unitStartTime = workingHours.get("start");
                Time unitEndTime = workingHours.get("end");
                List<Reserva> bookings = reservaRepository.findByRecursoId(resource.getRecursoId());
                boolean isAvailable = true;
                for (Reserva booking : bookings) {
                    if (booking.getFechaReserva().compareTo(java.sql.Date.valueOf(currentDate)) == 0) {
                        if (currentTime.compareTo(booking.getHoraInicioReserva()) >= 0 && currentTime.compareTo(booking.getHoraFinReserva()) < 0) {
                            isAvailable = false;
                            break;
                        }
                    }
                }
                if (isAvailable) {
                    Map<String, Object> resourceMap = new LinkedHashMap<>();
                    resourceMap.put("Id_recurso", resource.getRecursoId());
                    resourceMap.put("nombre", resource.getNombre());
                    TipoRecurso tipoRecurso = tipoRecursoRepository.findByTipoRecursoId(resource.getTipoRecursoId());
                    resourceMap.put("tipo_recurso", tipoRecurso != null ? tipoRecurso.getNombre() : "Desconocido");
                    Map<String, String> availability = new LinkedHashMap<>();
                    availability.put("start", unitStartTime.toString());
                    availability.put("end", unitEndTime.toString());
                    resourceMap.put("horario_disponibilidad", availability);

                    availableResources.add(resourceMap);
                }
            }
            return ResponseEntity.ok().body(availableResources);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
}
