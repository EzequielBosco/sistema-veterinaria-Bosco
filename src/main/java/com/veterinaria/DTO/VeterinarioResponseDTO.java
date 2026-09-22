package com.veterinaria.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioResponseDTO {

    @Schema(description = "ID único del veterinario", example = "2")
    private Long id;

    @Schema(description = "Nombre del veterinario", example = "María")
    private String nombre;

    @Schema(description = "Apellido del veterinario", example = "García")
    private String apellido;

    @Schema(description = "Teléfono de contacto del veterinario", example = "+54 11 9876-5432")
    private String telefono;

    @Schema(description = "Correo electrónico del veterinario", example = "maria.garcia@clinica.com")
    private String email;

    @Schema(description = "Número de matrícula profesional del veterinario", example = "MV-4521")
    private String matricula;

    @Schema(description = "Especialidad del veterinario", example = "Clínica general")
    private String especialidad;
}
