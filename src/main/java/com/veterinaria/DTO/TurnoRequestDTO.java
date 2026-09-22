package com.veterinaria.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoRequestDTO {

    @Schema(description = "Fecha del turno (formato ISO: yyyy-MM-dd)", example = "2026-10-15")
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @Schema(description = "Hora del turno (formato HH:mm)", example = "10:30")
    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @Schema(description = "Motivo de la consulta", example = "Control anual y vacunación")
    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @Schema(description = "Duración estimada del turno en minutos (mínimo 10)", example = "30")
    @NotNull(message = "La duracion es obligatoria")
    @Min(value = 10, message = "La duracion debe ser de al menos 10 minutos")
    private Integer duracionMinutos;

    @Schema(description = "ID de la mascota que asiste al turno", example = "1")
    @NotNull(message = "El id de la mascota es obligatorio")
    @Positive(message = "El id de la mascota debe ser positivo")
    private Long mascotaId;

    @Schema(description = "Lista de veterinarios asignados al turno")
    @NotEmpty(message = "Debe indicar al menos un veterinario")
    private List<@Valid @NotNull(message = "El veterinario no puede ser nulo") TurnoVeterinarioRequestDTO> veterinarios;

    @AssertTrue(message = "La fecha y hora del turno deben ser posteriores al momento actual")
    public boolean isFechaHoraValida() {
        if (fecha == null || hora == null) {
            return true;
        }

        return LocalDateTime.of(fecha, hora).isAfter(LocalDateTime.now());
    }
}
