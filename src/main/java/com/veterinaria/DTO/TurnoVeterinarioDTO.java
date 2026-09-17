package com.veterinaria.DTO;

import com.veterinaria.Entity.enums.RolVeterinario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoVeterinarioDTO {

    private Long id;
    private String nombreApellido;
    private RolVeterinario rol;
}
