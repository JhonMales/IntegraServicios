package com.proyectovegeta.seminario.dto;

import lombok.Setter;

@Setter
public class ResourceCreationRequest {
    private Long serviceUnitID;
    private Long resourceTypeID;
    private String name;
    private String description;
    // Getters
    public Long getServiceUnitID() {
        return serviceUnitID;
    }
    public Long getResourceTypeID() {
        return resourceTypeID;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
}
