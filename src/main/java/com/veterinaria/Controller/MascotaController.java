package com.veterinaria.Controller;

import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.DTO.MascotaResponseDTO;
import com.veterinaria.Mapper.MascotaMapper;
import com.veterinaria.Service.MascotaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
            @Valid @RequestBody MascotaRequestDTO mascotaRequestDTO) {
        return ResponseEntity.ok(mascotaMapper.toResponseDto(mascotaService.updateMascota(id, mascotaRequestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMascota(@PathVariable Long id) {
        mascotaService.deleteMascota(id);
        return ResponseEntity.noContent().build();
    }
}
