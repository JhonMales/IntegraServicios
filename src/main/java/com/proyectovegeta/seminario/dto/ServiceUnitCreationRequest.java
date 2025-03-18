package com.proyectovegeta.seminario.dto;

import java.sql.Time;
import lombok.Setter;

@Setter
public class ServiceUnitCreationRequest {
    private String name;
    private Time startWorkingHours;
    private Time endWorkingHours;
    // Getters
    public String getName() {
        return name;
    }
    public Time getStartWorkingHours() {
        return startWorkingHours;
    }
    public Time getEndWorkingHours() {
        return endWorkingHours;
    }
}