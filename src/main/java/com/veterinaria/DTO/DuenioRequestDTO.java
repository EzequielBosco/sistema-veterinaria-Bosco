package com.veterinaria.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenioRequestDTO {

    @Schema(description = "Nombre del dueño", example = "Juan")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Apellido del dueño", example = "Perez")
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Schema(description = "Cédula o DNI del dueño (7 u 8 dígitos)", example = "12345678")
    @NotBlank(message = "La cedula/DNI es obligatoria")
    @Pattern(regexp = "\\d{7,8}", message = "La cedula/DNI debe tener 7 u 8 digitos")
    private String cedula;

    @Schema(description = "Teléfono de contacto del dueño", example = "+54 11 1234-5678")
    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "^[0-9+()\\-\\s]{6,30}$", message = "El telefono debe tener un formato valido")
    private String telefono;

    @Schema(description = "Correo electrónico del dueño", example = "juan.perez@gmail.com")
    @Email(message = "El email debe tener un formato valido")
    private String email;
}
