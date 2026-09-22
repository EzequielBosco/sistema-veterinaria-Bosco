package com.veterinaria.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescripcionRequestDTO {

    @Schema(description = "Indicaciones de administración del medicamento", example = "Administrar en ayunas, una vez al día por 7 días")
    private String indicaciones;
}
