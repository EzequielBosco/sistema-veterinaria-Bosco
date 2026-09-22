package com.veterinaria.Service;

import com.veterinaria.DTO.PrescripcionRequestDTO;
import com.veterinaria.DTO.PrescripcionResponseDTO;
import com.veterinaria.DTO.TurnoRequestDTO;
import com.veterinaria.DTO.TurnoResponseDTO;
import com.veterinaria.DTO.TurnoVeterinarioResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface TurnoService {

    List<TurnoResponseDTO> getAllTurnos();

    List<TurnoResponseDTO> getAgenda(Long veterinarioId, LocalDate fecha);

    TurnoResponseDTO getTurnoById(Long id);

    TurnoResponseDTO createTurno(TurnoRequestDTO turnoRequestDTO);

    TurnoResponseDTO updateTurno(Long id, TurnoRequestDTO turnoRequestDTO);

    List<TurnoVeterinarioResponseDTO> getVeterinariosByTurno(Long id);

    List<PrescripcionResponseDTO> getPrescripcionesByTurno(Long id);

    PrescripcionResponseDTO asociarMedicamento(Long turnoId, Long medicamentoId, PrescripcionRequestDTO dto);

    void deleteTurno(Long id);
}
