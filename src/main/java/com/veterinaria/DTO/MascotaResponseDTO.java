package com.veterinaria.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaResponseDTO {

    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private String color;
    private LocalDate fechaNacimiento;
    private Long duenioId;
    private String duenioNombre;
}
