package com.veterinaria.Service;

import com.veterinaria.DTO.MedicamentoRequestDTO;
import com.veterinaria.DTO.MedicamentoResponseDTO;

import java.util.List;

public interface MedicamentoService {

    List<MedicamentoResponseDTO> getAllMedicamentos();

    MedicamentoResponseDTO getMedicamentoById(Long id);

    MedicamentoResponseDTO createMedicamento(MedicamentoRequestDTO dto);

    MedicamentoResponseDTO updateMedicamento(Long id, MedicamentoRequestDTO dto);

    void deleteMedicamento(Long id);
}
