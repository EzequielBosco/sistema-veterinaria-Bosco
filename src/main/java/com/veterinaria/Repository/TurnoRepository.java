package com.veterinaria.Repository;

import com.veterinaria.Entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    boolean existsDistinctByParticipacionesVeterinarioIdAndFechaAndHora(
            Long veterinarioId,
            LocalDate fecha,
            LocalTime hora);

    boolean existsDistinctByParticipacionesVeterinarioIdAndFechaAndHoraAndIdNot(
            Long veterinarioId,
            LocalDate fecha,
            LocalTime hora,
            Long turnoId);

    List<Turno> findDistinctByParticipacionesVeterinarioIdAndFechaOrderByHoraAsc(
            Long veterinarioId,
            LocalDate fecha);
}
