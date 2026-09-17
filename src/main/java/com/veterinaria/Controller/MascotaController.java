package com.veterinaria.Controller;

import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.DTO.MascotaResponseDTO;
import com.veterinaria.Exception.BadRequestException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.MascotaMapper;
import com.veterinaria.Service.MascotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final MascotaMapper mascotaMapper;

    public MascotaController(MascotaService mascotaService, MascotaMapper mascotaMapper) {
        this.mascotaService = mascotaService;
        this.mascotaMapper = mascotaMapper;
    }

    @GetMapping
    public ResponseEntity<List<MascotaResponseDTO>> getAllMascotas() {
        return ResponseEntity.ok(mascotaMapper.toResponseDtoList(mascotaService.getAllMascotas()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> getMascotaById(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaMapper.toResponseDto(mascotaService.getMascotaById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> updateMascota(
            @PathVariable Long id,
            @RequestBody MascotaRequestDTO mascotaRequestDTO) {
        return ResponseEntity.ok(mascotaMapper.toResponseDto(mascotaService.updateMascota(id, mascotaRequestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMascota(@PathVariable Long id) {
        mascotaService.deleteMascota(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
