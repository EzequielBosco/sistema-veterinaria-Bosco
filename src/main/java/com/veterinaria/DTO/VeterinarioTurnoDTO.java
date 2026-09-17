package com.veterinaria.DTO;

import com.veterinaria.Entity.enums.RolVeterinario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioTurnoDTO {

    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private String mascotaNombre;
    private RolVeterinario rol;
}
