package com.veterinaria.Service;

import com.veterinaria.DTO.DuenioRequestDTO;
import com.veterinaria.DTO.DuenioResponseDTO;

import java.util.List;

public interface DuenioService {

    List<DuenioResponseDTO> getAllDuenios();

    DuenioResponseDTO getDuenioById(Long id);

    DuenioResponseDTO getDuenioByCedula(String cedula);

    List<DuenioResponseDTO> searchDuenios(String nombre, String apellido);

    DuenioResponseDTO getDuenioByEmail(String email);

    DuenioResponseDTO createDuenio(DuenioRequestDTO duenioRequestDTO);

    DuenioResponseDTO updateDuenio(Long id, DuenioRequestDTO duenioRequestDTO);

    void deleteDuenio(Long id);
}
