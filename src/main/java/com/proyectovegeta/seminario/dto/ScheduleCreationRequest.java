package com.proyectovegeta.seminario.dto;

import com.proyectovegeta.seminario.model.DiaSemana;

import java.sql.Time;

public class ScheduleCreationRequest {
    private DiaSemana dayOfWeek;
    private Time startTime;
    private Time endTime;
    //Getters
    public DiaSemana getDayOfWeek() {
        return dayOfWeek;
    }
    public Time getStartTime() {
        return startTime;
    }
    public Time getEndTime() {
        return endTime;
    }
}
