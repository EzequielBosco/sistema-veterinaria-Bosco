package com.veterinaria.DTO;

import com.veterinaria.Entity.enums.RolVeterinario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoVeterinarioRequestDTO {

    @NotNull(message = "El id del veterinario es obligatorio")
    @Positive(message = "El id del veterinario debe ser positivo")
    private Long veterinarioId;

    @NotNull(message = "El rol del veterinario es obligatorio")
    private RolVeterinario rol;
}
