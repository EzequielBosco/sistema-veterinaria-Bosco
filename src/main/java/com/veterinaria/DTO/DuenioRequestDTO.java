package com.veterinaria.DTO;

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

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "La cedula/DNI es obligatoria")
    @Pattern(regexp = "\\d{7,8}", message = "La cedula/DNI debe tener 7 u 8 digitos")
    private String cedula;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "^[0-9+()\\-\\s]{6,30}$", message = "El telefono debe tener un formato valido")
    private String telefono;

    @Email(message = "El email debe tener un formato valido")
    private String email;
}
