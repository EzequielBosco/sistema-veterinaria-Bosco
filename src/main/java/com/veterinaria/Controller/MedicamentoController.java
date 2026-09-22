package com.veterinaria.Controller;

import com.veterinaria.DTO.MedicamentoRequestDTO;
import com.veterinaria.DTO.MedicamentoResponseDTO;
import com.veterinaria.Exception.ErrorResponse;
import com.veterinaria.Service.MedicamentoService;
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

@Tag(name = "Medicamentos", description = "Gestión del catálogo y stock de medicamentos")
@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @Operation(summary = "Listar todos los medicamentos", description = "Retorna el catálogo completo de medicamentos con su stock y precio")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDTO>> getAllMedicamentos() {
        return ResponseEntity.ok(medicamentoService.getAllMedicamentos());
    }

    @Operation(summary = "Obtener medicamento por ID", description = "Retorna los datos de un medicamento a partir de su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Medicamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Medicamento no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> getMedicamentoById(@PathVariable Long id) {
        return ResponseEntity.ok(medicamentoService.getMedicamentoById(id));
    }

    @Operation(
        summary = "Registrar un nuevo medicamento",
        description = "Crea un nuevo medicamento en el catálogo. La combinación de nombre y principio activo debe ser única"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Medicamento creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (falla de validación)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Ya existe un medicamento con ese nombre y principio activo",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<MedicamentoResponseDTO> createMedicamento(
            @Valid @RequestBody MedicamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoService.createMedicamento(dto));
    }

    @Operation(
        summary = "Actualizar medicamento",
        description = "Modifica los datos de un medicamento existente. Permite actualizar stock y precio"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Medicamento actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (falla de validación)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Medicamento no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Ya existe un medicamento con ese nombre y principio activo",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDTO> updateMedicamento(
            @PathVariable Long id,
            @Valid @RequestBody MedicamentoRequestDTO dto) {
        return ResponseEntity.ok(medicamentoService.updateMedicamento(id, dto));
    }

    @Operation(summary = "Eliminar medicamento", description = "Elimina un medicamento del catálogo a partir de su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Medicamento eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Medicamento no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicamento(@PathVariable Long id) {
        medicamentoService.deleteMedicamento(id);
        return ResponseEntity.noContent().build();
    }

}
