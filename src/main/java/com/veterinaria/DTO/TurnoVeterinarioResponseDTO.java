package com.veterinaria.DTO;

import com.veterinaria.Entity.enums.RolVeterinario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoVeterinarioResponseDTO {

    @Schema(description = "ID único del veterinario", example = "2")
    private Long id;

    @Schema(description = "Nombre y apellido del veterinario", example = "María García")
    private String nombreApellido;

    @Schema(description = "Rol del veterinario en el turno", example = "PRINCIPAL")
    private RolVeterinario rol;
}
