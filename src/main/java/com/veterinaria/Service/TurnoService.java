package com.veterinaria.Service;

import com.veterinaria.DTO.TurnoRequestDTO;
import com.veterinaria.DTO.TurnoResponseDTO;
import com.veterinaria.DTO.TurnoVeterinarioDTO;

import java.time.LocalDate;
import java.util.List;

public interface TurnoService {

    List<TurnoResponseDTO> getAllTurnos();

    List<TurnoResponseDTO> getAgenda(Long veterinarioId, LocalDate fecha);

    TurnoResponseDTO getTurnoById(Long id);

    TurnoResponseDTO createTurno(TurnoRequestDTO turnoRequestDTO);

    TurnoResponseDTO updateTurno(Long id, TurnoRequestDTO turnoRequestDTO);

    List<TurnoVeterinarioDTO> getVeterinariosByTurno(Long id);

    void deleteTurno(Long id);
}
