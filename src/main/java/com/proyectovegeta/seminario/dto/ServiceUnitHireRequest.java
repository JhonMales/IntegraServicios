package com.proyectovegeta.seminario.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceUnitHireRequest {
    private Long employeeID;
    private String position;
}
