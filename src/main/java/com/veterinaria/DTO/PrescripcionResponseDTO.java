package com.veterinaria.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescripcionResponseDTO {

    @Schema(description = "ID de la prescripción", example = "1")
    private Long id;

    @Schema(description = "Nombre comercial del medicamento", example = "Amoxicilina 500mg")
    private String nombre;

    @Schema(description = "Principio activo del medicamento", example = "Amoxicilina")
    private String principioActivo;

    @Schema(description = "Cantidad recetada", example = "1")
    private Integer cantidad;

    @Schema(description = "Precio unitario al momento de la prescripción", example = "1500.00")
    private BigDecimal precioUnitario;

    @Schema(description = "Indicaciones de administración", example = "Administrar en ayunas, una vez al día por 7 días")
    private String indicaciones;
}
