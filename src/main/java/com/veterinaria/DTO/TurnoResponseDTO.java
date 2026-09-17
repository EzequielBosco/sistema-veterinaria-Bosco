package com.veterinaria.DTO;

import com.veterinaria.Entity.enums.EstadoTurno;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponseDTO {

    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private EstadoTurno estado;
    private String mascotaNombre;
    private List<TurnoVeterinarioDTO> veterinarios;
}
