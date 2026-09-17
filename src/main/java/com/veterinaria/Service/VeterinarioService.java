package com.veterinaria.Service;

import com.veterinaria.DTO.VeterinarioRequestDTO;
import com.veterinaria.DTO.VeterinarioResponseDTO;
import com.veterinaria.DTO.VeterinarioTurnoDTO;

import java.util.List;

public interface VeterinarioService {

    List<VeterinarioResponseDTO> getAllVeterinarios();

    VeterinarioResponseDTO getVeterinarioById(Long id);

    VeterinarioResponseDTO createVeterinario(VeterinarioRequestDTO veterinarioRequestDTO);

    VeterinarioResponseDTO updateVeterinario(Long id, VeterinarioRequestDTO veterinarioRequestDTO);

    List<VeterinarioTurnoDTO> getTurnosByVeterinario(Long id);

    void deleteVeterinario(Long id);
}
