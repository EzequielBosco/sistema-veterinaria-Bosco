package com.veterinaria.DTO;

import com.veterinaria.Entity.enums.RolVeterinario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoVeterinarioRequestDTO {

    @Schema(description = "ID del veterinario a asignar al turno", example = "2")
    @NotNull(message = "El id del veterinario es obligatorio")
    @Positive(message = "El id del veterinario debe ser positivo")
    private Long veterinarioId;

    @Schema(description = "Rol del veterinario en el turno", example = "PRINCIPAL")
    @NotNull(message = "El rol del veterinario es obligatorio")
    private RolVeterinario rol;
}
