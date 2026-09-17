package com.veterinaria.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenioRequestDTO {

    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;
    private String email;
}
