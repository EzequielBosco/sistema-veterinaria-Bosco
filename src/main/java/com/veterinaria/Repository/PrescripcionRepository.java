package com.veterinaria.Repository;

import com.veterinaria.Entity.Prescripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescripcionRepository extends JpaRepository<Prescripcion, Long> {

    List<Prescripcion> findByTurnoId(Long turnoId);
}
