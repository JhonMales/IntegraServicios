package com.proyectovegeta.seminario.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginRequest {
    private String username;
    private String password;
}