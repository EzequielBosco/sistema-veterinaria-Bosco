package com.veterinaria.Service;

import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.Entity.Mascota;

import java.util.List;

public interface MascotaService {

    List<Mascota> getAllMascotas();

    Mascota getMascotaById(Long id);

    List<Mascota> getMascotasByDuenioId(Long duenioId);

    Mascota createMascota(Long duenioId, Mascota mascota);

    Mascota updateMascota(Long id, MascotaRequestDTO mascotaActualizada);

    void deleteMascota(Long id);
}
