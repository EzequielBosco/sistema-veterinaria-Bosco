package com.veterinaria.Controller;

import com.veterinaria.DTO.VeterinarioRequestDTO;
import com.veterinaria.DTO.VeterinarioResponseDTO;
import com.veterinaria.DTO.VeterinarioTurnoResponseDTO;
import com.veterinaria.Exception.ErrorResponse;
import com.veterinaria.Service.VeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Veterinarios", description = "Gestión de veterinarios y consulta de su agenda de turnos")
@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @Operation(summary = "Listar todos los veterinarios", description = "Retorna la lista completa de veterinarios registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<VeterinarioResponseDTO>> getAllVeterinarios() {
        return ResponseEntity.ok(veterinarioService.getAllVeterinarios());
    }

    @Operation(summary = "Obtener veterinario por ID", description = "Retorna los datos de un veterinario a partir de su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Veterinario encontrado"),
        @ApiResponse(responseCode = "404", description = "Veterinario no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDTO> getVeterinarioById(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.getVeterinarioById(id));
    }

    @Operation(summary = "Listar turnos de un veterinario", description = "Retorna todos los turnos asignados a un veterinario junto con el rol que desempeña en cada uno")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Veterinario no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/turnos")
    public ResponseEntity<List<VeterinarioTurnoResponseDTO>> getTurnosByVeterinario(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.getTurnosByVeterinario(id));
    }

    @Operation(
        summary = "Registrar un nuevo veterinario",
        description = "Crea un nuevo veterinario. El email y la matrícula deben ser únicos en el sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Veterinario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (falla de validación)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "El email o la matrícula ya están registrados",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<VeterinarioResponseDTO> createVeterinario(
            @Valid @RequestBody VeterinarioRequestDTO veterinarioRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veterinarioService.createVeterinario(veterinarioRequestDTO));
    }

    @Operation(
        summary = "Actualizar veterinario",
        description = "Modifica los datos de un veterinario existente. El email y la matrícula deben ser únicos en el sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Veterinario actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (falla de validación)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Veterinario no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "El email o la matrícula ya pertenecen a otro veterinario",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDTO> updateVeterinario(
            @PathVariable Long id,
            @Valid @RequestBody VeterinarioRequestDTO veterinarioRequestDTO) {
        return ResponseEntity.ok(veterinarioService.updateVeterinario(id, veterinarioRequestDTO));
    }

    @Operation(summary = "Eliminar veterinario", description = "Elimina un veterinario del sistema a partir de su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Veterinario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Veterinario no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeterinario(@PathVariable Long id) {
        veterinarioService.deleteVeterinario(id);
        return ResponseEntity.noContent().build();
    }
}
