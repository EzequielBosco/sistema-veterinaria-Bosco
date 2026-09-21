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
public class VeterinarioRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "^[0-9+()\\-\\s]{6,30}$", message = "El telefono debe tener un formato valido")
    private String telefono;

    @Email(message = "El email debe tener un formato valido")
    private String email;

    @NotBlank(message = "La matricula es obligatoria")
    private String matricula;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;
}
