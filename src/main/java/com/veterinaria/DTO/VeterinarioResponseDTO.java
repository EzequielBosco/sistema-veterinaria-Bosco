package com.veterinaria.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String matricula;
    private String especialidad;
}
