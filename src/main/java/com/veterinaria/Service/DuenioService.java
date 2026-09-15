package com.veterinaria.Service;

import com.veterinaria.Entity.Duenio;

import java.util.List;

public interface DuenioService {

    List<Duenio> getAllDuenios();

    Duenio getDuenioById(Long id);

    Duenio getDuenioByCedula(String cedula);

    List<Duenio> searchDuenios(String nombre, String apellido);

    Duenio getDuenioByEmail(String email);

    Duenio createDuenio(Duenio duenio);

    Duenio updateDuenio(Long id, Duenio duenioActualizado);

    void deleteDuenio(Long id);
}
