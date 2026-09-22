package com.veterinaria.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenioResponseDTO {

    @Schema(description = "ID único del dueño", example = "1")
    private Long id;

    @Schema(description = "Nombre del dueño", example = "Juan")
    private String nombre;

    @Schema(description = "Apellido del dueño", example = "Perez")
    private String apellido;

    @Schema(description = "Cédula o DNI del dueño", example = "12345678")
    private String cedula;

    @Schema(description = "Teléfono de contacto del dueño", example = "+54 11 1234-5678")
    private String telefono;

    @Schema(description = "Correo electrónico del dueño", example = "juan.perez@gmail.com")
    private String email;
}
