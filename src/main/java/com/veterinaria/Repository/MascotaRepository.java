package com.veterinaria.Repository;

import com.veterinaria.Entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByDuenioId(Long duenioId);

    long countByDuenioId(Long duenioId);
}
