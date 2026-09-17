package com.veterinaria.Repository;

import com.veterinaria.Entity.Participacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipacionRepository extends JpaRepository<Participacion, Long> {

    List<Participacion> findByTurnoId(Long turnoId);

    List<Participacion> findByVeterinarioId(Long veterinarioId);
}
