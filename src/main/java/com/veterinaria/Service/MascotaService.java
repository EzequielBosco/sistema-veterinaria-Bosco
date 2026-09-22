package com.veterinaria.Service;

import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.DTO.MascotaResponseDTO;

import java.util.List;

public interface MascotaService {

    List<MascotaResponseDTO> getAllMascotas();

    MascotaResponseDTO getMascotaById(Long id);

    List<MascotaResponseDTO> getMascotasByDuenioId(Long duenioId);

    MascotaResponseDTO createMascota(Long duenioId, MascotaRequestDTO mascotaRequestDTO);

    MascotaResponseDTO updateMascota(Long id, MascotaRequestDTO mascotaRequestDTO);

    void deleteMascota(Long id);
}
