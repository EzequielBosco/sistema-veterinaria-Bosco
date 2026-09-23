package com.veterinaria.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescripcionRequestDTO {

    @Schema(description = "Cantidad de unidades recetadas. Se descuenta del stock del medicamento. Si no se indica, se toma 1", example = "2")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @Schema(description = "Indicaciones de administración del medicamento", example = "Administrar en ayunas, una vez al día por 7 días")
    private String indicaciones;
}
