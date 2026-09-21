package com.veterinaria.Controller;

import com.veterinaria.DTO.VeterinarioRequestDTO;
import com.veterinaria.DTO.VeterinarioResponseDTO;
import com.veterinaria.DTO.VeterinarioTurnoResponseDTO;
import com.veterinaria.Service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @GetMapping
    public ResponseEntity<List<VeterinarioResponseDTO>> getAllVeterinarios() {
        return ResponseEntity.ok(veterinarioService.getAllVeterinarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDTO> getVeterinarioById(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.getVeterinarioById(id));
    }

    @GetMapping("/{id}/turnos")
    public ResponseEntity<List<VeterinarioTurnoResponseDTO>> getTurnosByVeterinario(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.getTurnosByVeterinario(id));
    }

    @PostMapping
    public ResponseEntity<VeterinarioResponseDTO> createVeterinario(
            @Valid @RequestBody VeterinarioRequestDTO veterinarioRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veterinarioService.createVeterinario(veterinarioRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDTO> updateVeterinario(
            @PathVariable Long id,
            @Valid @RequestBody VeterinarioRequestDTO veterinarioRequestDTO) {
        return ResponseEntity.ok(veterinarioService.updateVeterinario(id, veterinarioRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeterinario(@PathVariable Long id) {
        veterinarioService.deleteVeterinario(id);
        return ResponseEntity.noContent().build();
    }
}
