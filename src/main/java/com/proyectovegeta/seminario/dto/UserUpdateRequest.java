package com.proyectovegeta.seminario.dto;

import java.util.Optional;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateRequest {
    private Optional<String> username;
    private Optional<String> password;
    private Optional<String> email;
    private Optional<String> rol;
}