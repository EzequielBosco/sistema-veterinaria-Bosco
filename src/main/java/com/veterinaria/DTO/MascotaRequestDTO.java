package com.veterinaria.DTO;

import com.veterinaria.Entity.enums.SexoMascota;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaRequestDTO {

    @Schema(description = "Nombre de la mascota", example = "Firulais")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Especie de la mascota", example = "Perro")
    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    @Schema(description = "Raza de la mascota", example = "Labrador")
    private String raza;

    @Schema(description = "Color del pelaje de la mascota", example = "Dorado")
    private String color;

    @Schema(description = "Sexo de la mascota", example = "MACHO")
    @NotNull(message = "El sexo es obligatorio")
    private SexoMascota sexo;

    @Schema(description = "Fecha de nacimiento de la mascota (yyyy-MM-dd)", example = "2020-03-10")
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    private LocalDate fechaNacimiento;

    @Schema(description = "ID del dueño al que pertenece la mascota", example = "1")
    @Positive(message = "El id del duenio debe ser positivo")
    private Long duenioId;
}
