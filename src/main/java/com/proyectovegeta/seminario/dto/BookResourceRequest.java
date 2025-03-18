package com.proyectovegeta.seminario.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
@Builder
public class BookResourceRequest {
    Long userId;
    Date date;
    Time start;
    Time end;
}
