package com.veterinaria.Controller;

import com.veterinaria.DTO.TurnoRequestDTO;
import com.veterinaria.DTO.TurnoResponseDTO;
import com.veterinaria.DTO.TurnoVeterinarioResponseDTO;
import com.veterinaria.Service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    public ResponseEntity<List<TurnoResponseDTO>> getTurnos(
            @RequestParam(required = false) Long veterinarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        if (veterinarioId != null || fecha != null) {
            return ResponseEntity.ok(turnoService.getAgenda(veterinarioId, fecha));
        }
        return ResponseEntity.ok(turnoService.getAllTurnos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> getTurnoById(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.getTurnoById(id));
    }

    @GetMapping("/{id}/veterinarios")
    public ResponseEntity<List<TurnoVeterinarioResponseDTO>> getVeterinariosByTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.getVeterinariosByTurno(id));
    }

    @PostMapping
    public ResponseEntity<TurnoResponseDTO> createTurno(@Valid @RequestBody TurnoRequestDTO turnoRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.createTurno(turnoRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> updateTurno(
            @PathVariable Long id,
            @Valid @RequestBody TurnoRequestDTO turnoRequestDTO) {
        return ResponseEntity.ok(turnoService.updateTurno(id, turnoRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTurno(@PathVariable Long id) {
        turnoService.deleteTurno(id);
        return ResponseEntity.noContent().build();
    }
}
