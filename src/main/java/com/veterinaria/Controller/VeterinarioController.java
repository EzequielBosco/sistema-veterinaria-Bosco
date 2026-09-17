package com.veterinaria.Controller;

import com.veterinaria.DTO.VeterinarioRequestDTO;
import com.veterinaria.DTO.VeterinarioResponseDTO;
import com.veterinaria.DTO.VeterinarioTurnoDTO;
import com.veterinaria.Exception.DuplicateResourceException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Service.VeterinarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    public ResponseEntity<List<VeterinarioTurnoDTO>> getTurnosByVeterinario(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.getTurnosByVeterinario(id));
    }

    @PostMapping
    public ResponseEntity<VeterinarioResponseDTO> createVeterinario(
            @RequestBody VeterinarioRequestDTO veterinarioRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veterinarioService.createVeterinario(veterinarioRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDTO> updateVeterinario(
            @PathVariable Long id,
            @RequestBody VeterinarioRequestDTO veterinarioRequestDTO) {
        return ResponseEntity.ok(veterinarioService.updateVeterinario(id, veterinarioRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeterinario(@PathVariable Long id) {
        veterinarioService.deleteVeterinario(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResource(DuplicateResourceException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }
}
