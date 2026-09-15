package com.veterinaria.Service;

import com.veterinaria.Entity.Mascota;

import java.util.List;

public interface MascotaService {

    List<Mascota> getAllMascotas();

    Mascota getMascotaById(Long id);

    List<Mascota> getMascotasByDuenioId(Long duenioId);

    Mascota createMascota(Long duenioId, Mascota mascota);

    Mascota updateMascota(Long id, Mascota mascotaActualizada);

    void deleteMascota(Long id);
}
