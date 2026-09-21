package com.veterinaria.Controller;

import com.veterinaria.DTO.DuenioRequestDTO;
import com.veterinaria.DTO.DuenioResponseDTO;
import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.DTO.MascotaResponseDTO;
import com.veterinaria.Mapper.DuenioMapper;
import com.veterinaria.Mapper.MascotaMapper;
import com.veterinaria.Service.DuenioService;
import com.veterinaria.Service.MascotaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/duenios")
public class DuenioController {

    private final DuenioService duenioService;
    private final MascotaService mascotaService;
    private final DuenioMapper duenioMapper;
    private final MascotaMapper mascotaMapper;

    public DuenioController(
            DuenioService duenioService,
            MascotaService mascotaService,
            DuenioMapper duenioMapper,
            MascotaMapper mascotaMapper) {
        this.duenioService = duenioService;
        this.mascotaService = mascotaService;
        this.duenioMapper = duenioMapper;
        this.mascotaMapper = mascotaMapper;
    }

    @GetMapping
    public ResponseEntity<List<DuenioResponseDTO>> getAllDuenios() {
        return ResponseEntity.ok(duenioMapper.toResponseDtoList(duenioService.getAllDuenios()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DuenioResponseDTO> getDuenioById(@PathVariable Long id) {
        return ResponseEntity.ok(duenioMapper.toResponseDto(duenioService.getDuenioById(id)));
    }

    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<MascotaResponseDTO>> getMascotasByDuenioId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaMapper.toResponseDtoList(mascotaService.getMascotasByDuenioId(id)));
    }

    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<DuenioResponseDTO> getDuenioByCedula(@PathVariable String cedula) {
        return ResponseEntity.ok(duenioMapper.toResponseDto(duenioService.getDuenioByCedula(cedula)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DuenioResponseDTO>> searchDuenios(
            @RequestParam String nombre,
            @RequestParam String apellido) {
        return ResponseEntity.ok(duenioMapper.toResponseDtoList(duenioService.searchDuenios(nombre, apellido)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<DuenioResponseDTO> getDuenioByEmail(@PathVariable String email) {
        return ResponseEntity.ok(duenioMapper.toResponseDto(duenioService.getDuenioByEmail(email)));
    }

    @PostMapping
    public ResponseEntity<DuenioResponseDTO> createDuenio(@Valid @RequestBody DuenioRequestDTO duenioRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(duenioMapper.toResponseDto(duenioService.createDuenio(duenioMapper.toEntity(duenioRequestDTO))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DuenioResponseDTO> updateDuenio(
            @PathVariable Long id,
            @Valid @RequestBody DuenioRequestDTO duenioRequestDTO) {
        return ResponseEntity.ok(duenioMapper.toResponseDto(
                duenioService.updateDuenio(id, duenioMapper.toEntity(duenioRequestDTO))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDuenio(@PathVariable Long id) {
        duenioService.deleteDuenio(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{duenioId}/mascotas")
    public ResponseEntity<MascotaResponseDTO> createMascota(
            @PathVariable Long duenioId,
            @Valid @RequestBody MascotaRequestDTO mascotaRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mascotaMapper.toResponseDto(
                        mascotaService.createMascota(duenioId, mascotaMapper.toEntity(mascotaRequestDTO))));
    }
}
