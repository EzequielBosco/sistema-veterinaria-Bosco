package com.veterinaria.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoRequestDTO {

    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private Long mascotaId;
    private List<Long> veterinarioIds;
}
