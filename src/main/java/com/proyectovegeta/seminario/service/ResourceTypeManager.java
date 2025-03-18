package com.proyectovegeta.seminario.service;

import com.proyectovegeta.seminario.model.DiaSemana;
import com.proyectovegeta.seminario.model.HorarioDisponibilidad;
import com.proyectovegeta.seminario.model.TipoRecurso;
import com.proyectovegeta.seminario.model.UnidadServicio;
import com.proyectovegeta.seminario.repository.HorarioDisponibilidadRepository;
import com.proyectovegeta.seminario.repository.TipoRecursoRepository;
import com.proyectovegeta.seminario.repository.UnidadServicioRepository;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;

@Service
public class ResourceTypeManager {
    private final TipoRecursoRepository tipoRecursoRepository;
    private final UnidadServicioRepository unidadServicioRepository;
    private final HorarioDisponibilidadRepository horarioDisponibilidadRepository;
    public ResourceTypeManager(TipoRecursoRepository tipoRecursoRepository, UnidadServicioRepository unidadServicioRepository, HorarioDisponibilidadRepository horarioDisponibilidadRepository ) {
        this.tipoRecursoRepository = tipoRecursoRepository;
        this.unidadServicioRepository = unidadServicioRepository;
        this.horarioDisponibilidadRepository = horarioDisponibilidadRepository;
    }
    public TipoRecurso createResourceType(Long serviceUnitID, String name, String description, Time minLoanTime) {
        UnidadServicio serviceUnit = unidadServicioRepository.findById(serviceUnitID).orElseThrow(() -> new RuntimeException("Unidad de servicio no encontrada"));
        TipoRecurso tipoRecurso = new TipoRecurso();
        tipoRecurso.setUnidadId(serviceUnit.getUnidadId());
        tipoRecurso.setNombre(name);
        tipoRecurso.setDescripcion(description);
        tipoRecurso.setTiempoMinimoPrestamo(minLoanTime);
        return tipoRecursoRepository.save(tipoRecurso);
    }
    public TipoRecurso getResourceType(Long resourceTypeID){
        return tipoRecursoRepository.findById(resourceTypeID).orElseThrow(() -> new RuntimeException("Tipo de recurso no encontrado"));
    }
    public List<TipoRecurso> getResourceTypes() {
        return tipoRecursoRepository.findAll();
    }
    public List<TipoRecurso> getServiceUnitResourceTypes(Long serviceUnitID){
        return tipoRecursoRepository.findByUnidadId(serviceUnitID);
    }

    //Assigns a schedule to a resource type
    public HorarioDisponibilidad assignSchedule(Long resourceTypeID, DiaSemana dayOfWeek, Time startTime, Time endTime){
        TipoRecurso resourceType = tipoRecursoRepository.findById(resourceTypeID).orElseThrow(() -> new RuntimeException("Tipo de recurso no encontrado"));
        //Validates that the start time is before the end time
        if(startTime.after(endTime)){
            throw new RuntimeException("La hora de inicio debe ser antes de la hora de fin");
        }
        //Validates that the schedule is within the service unit's working hours
        UnidadServicio serviceUnit = unidadServicioRepository.findById(resourceType.getUnidadId()).orElseThrow(() -> new RuntimeException("Unidad de servicio no encontrada"));
        if(startTime.before(serviceUnit.getHorarioLaboralInicio()) || endTime.after(serviceUnit.getHorarioLaboralFin())){
            throw new RuntimeException("El horario debe estar dentro del horario de atención de la unidad de servicio");
        }
        //Checks if the schedule is already assigned for that day
        List<HorarioDisponibilidad> schedules = horarioDisponibilidadRepository.findByTipoRecursoId(resourceType.getTipoRecursoId());
        for(HorarioDisponibilidad schedule : schedules){
            if(schedule.getDiaSemana().equals(dayOfWeek)){
                //Updated the schedule
                schedule.setHoraInicio(startTime);
                schedule.setHoraFin(endTime);
                return horarioDisponibilidadRepository.save(schedule);
            }
        }
        //Creates a new schedule
        HorarioDisponibilidad schedule = new HorarioDisponibilidad();
        schedule.setTipoRecursoId(resourceType.getTipoRecursoId());
        schedule.setDiaSemana(dayOfWeek);
        schedule.setHoraInicio(startTime);
        schedule.setHoraFin(endTime);
        return horarioDisponibilidadRepository.save(schedule);
    }
    //Gets all schedules for a resource type
    public List<HorarioDisponibilidad> getResourceTypeSchedules(Long resourceTypeID){
        return horarioDisponibilidadRepository.findByTipoRecursoId(resourceTypeID);
    }
    //Gets a schedule by ID
    public HorarioDisponibilidad getSchedule(Long scheduleID){
        return horarioDisponibilidadRepository.findById(scheduleID).orElseThrow(() -> new RuntimeException("Horario no encontrado"));
    }
    //Gets a schedule by day and resource type
    public HorarioDisponibilidad getScheduleByDay(Long resourceTypeID, DiaSemana dayOfWeek){
        TipoRecurso resourceType = tipoRecursoRepository.findById(resourceTypeID).orElseThrow(() -> new RuntimeException("Tipo de recurso no encontrado"));
        List<HorarioDisponibilidad> schedules = horarioDisponibilidadRepository.findByTipoRecursoId(resourceType.getTipoRecursoId());
        for(HorarioDisponibilidad schedule : schedules){
            if(schedule.getDiaSemana().equals(dayOfWeek)){
                return schedule;
            }
        }
        return null;
    }
}
