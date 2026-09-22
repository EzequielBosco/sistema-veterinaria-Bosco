package com.veterinaria.DTO;

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

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @NotNull(message = "La duracion es obligatoria")
    @Min(value = 10, message = "La duracion debe ser de al menos 10 minutos")
    private Integer duracionMinutos;

    @NotNull(message = "El id de la mascota es obligatorio")
    @Positive(message = "El id de la mascota debe ser positivo")
    private Long mascotaId;

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
