package com.veterinaria.DTO;

import com.veterinaria.Entity.enums.TipoPresentacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoRequestDTO {

    @Schema(description = "Nombre comercial del medicamento", example = "Amoxicilina 500mg")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Principio activo del medicamento", example = "Amoxicilina")
    @NotBlank(message = "El principio activo es obligatorio")
    private String principioActivo;

    @Schema(description = "Cantidad disponible en stock", example = "100")
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Precio unitario del medicamento", example = "1500.00")
    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser mayor a cero")
    private BigDecimal precioUnitario;

    @Schema(description = "Forma de presentación del medicamento", example = "COMPRIMIDO",
            allowableValues = {"COMPRIMIDO", "CAPSULA", "JARABE", "INYECTABLE", "TOPICO", "GOTAS", "SPRAY", "POLVO"})
    private TipoPresentacion presentacion;
}
