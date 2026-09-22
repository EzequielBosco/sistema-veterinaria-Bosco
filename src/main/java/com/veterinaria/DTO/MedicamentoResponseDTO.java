package com.veterinaria.DTO;

import com.veterinaria.Entity.enums.TipoPresentacion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoResponseDTO {

    @Schema(description = "ID único del medicamento", example = "1")
    private Long id;

    @Schema(description = "Nombre comercial del medicamento", example = "Amoxicilina 500mg")
    private String nombre;

    @Schema(description = "Principio activo del medicamento", example = "Amoxicilina")
    private String principioActivo;

    @Schema(description = "Cantidad disponible en stock", example = "100")
    private Integer stock;

    @Schema(description = "Precio unitario del medicamento", example = "1500.00")
    private BigDecimal precioUnitario;

    @Schema(description = "Forma de presentación del medicamento", example = "COMPRIMIDO")
    private TipoPresentacion presentacion;
}
