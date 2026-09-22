package com.veterinaria.DTO;

import com.veterinaria.Entity.enums.EstadoTurno;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "ID único del turno", example = "1")
    private Long id;

    @Schema(description = "Fecha del turno", example = "2026-10-15")
    private LocalDate fecha;

    @Schema(description = "Hora del turno", example = "10:30")
    private LocalTime hora;

    @Schema(description = "Motivo de la consulta", example = "Control anual y vacunación")
    private String motivo;

    @Schema(description = "Duración del turno en minutos", example = "30")
    private Integer duracionMinutos;

    @Schema(description = "Estado actual del turno", example = "PENDIENTE")
    private EstadoTurno estado;

    @Schema(description = "Nombre de la mascota que asiste al turno", example = "Firulais")
    private String mascotaNombre;

    @Schema(description = "Lista de veterinarios asignados al turno")
    private List<TurnoVeterinarioResponseDTO> veterinarios;
}
