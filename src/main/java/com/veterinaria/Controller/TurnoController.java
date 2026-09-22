package com.veterinaria.Controller;

import com.veterinaria.DTO.PrescripcionRequestDTO;
import com.veterinaria.DTO.PrescripcionResponseDTO;
import com.veterinaria.DTO.TurnoRequestDTO;
import com.veterinaria.DTO.TurnoResponseDTO;
import com.veterinaria.DTO.TurnoVeterinarioResponseDTO;
import com.veterinaria.Exception.ErrorResponse;
import com.veterinaria.Service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Turnos", description = "Gestión de turnos y agenda de la veterinaria")
@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @Operation(
        summary = "Listar turnos / consultar agenda",
        description = "Retorna todos los turnos. Si se desea filtrar la agenda, deben proveerse ambos parámetros 'veterinarioId' y 'fecha' juntos; pasar solo uno devuelve 400"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Se proporcionó solo uno de los dos parámetros de filtro (se requieren ambos o ninguno)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Veterinario no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<TurnoResponseDTO>> getTurnos(
            @RequestParam(required = false) Long veterinarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        if (veterinarioId != null || fecha != null) {
            return ResponseEntity.ok(turnoService.getAgenda(veterinarioId, fecha));
        }
        return ResponseEntity.ok(turnoService.getAllTurnos());
    }

    @Operation(summary = "Obtener turno por ID", description = "Retorna los datos de un turno a partir de su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turno encontrado"),
        @ApiResponse(responseCode = "404", description = "Turno no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> getTurnoById(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.getTurnoById(id));
    }

    @Operation(summary = "Listar veterinarios de un turno", description = "Retorna los veterinarios asignados a un turno junto con su rol")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Turno no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/veterinarios")
    public ResponseEntity<List<TurnoVeterinarioResponseDTO>> getVeterinariosByTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.getVeterinariosByTurno(id));
    }

    @Operation(summary = "Listar prescripciones de un turno", description = "Retorna los medicamentos prescriptos en un turno junto con la cantidad e indicaciones")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Turno no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/medicamentos")
    public ResponseEntity<List<PrescripcionResponseDTO>> getPrescripcionesByTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.getPrescripcionesByTurno(id));
    }

    @Operation(
        summary = "Registrar un nuevo turno",
        description = "Crea un nuevo turno. La fecha y hora deben ser posteriores al momento actual. " +
            "La duración mínima es de 10 minutos. Debe indicarse exactamente un veterinario con rol PRINCIPAL. " +
            "No se puede repetir el mismo veterinario en el turno. No se permiten turnos superpuestos para un mismo veterinario"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Turno creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos: falla de validación, fecha/hora en el pasado, veterinario repetido o sin exactamente un PRINCIPAL",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Mascota o veterinario no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "El turno se superpone con otro turno del veterinario. El mensaje indica el ID y horario del turno conflictivo",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<TurnoResponseDTO> createTurno(@Valid @RequestBody TurnoRequestDTO turnoRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.createTurno(turnoRequestDTO));
    }

    @Operation(
        summary = "Agregar prescripción a un turno",
        description = "Asocia un medicamento al turno creando una prescripción y descuenta una unidad del stock. " +
            "El cuerpo es opcional y permite incluir indicaciones de administración. " +
            "Devuelve 422 si el medicamento no tiene stock disponible"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Prescripción registrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Turno o medicamento no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "El medicamento no tiene stock disponible",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{turnoId}/medicamentos/{medicamentoId}")
    public ResponseEntity<PrescripcionResponseDTO> asociarMedicamento(
            @PathVariable Long turnoId,
            @PathVariable Long medicamentoId,
            @RequestBody(required = false) PrescripcionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(turnoService.asociarMedicamento(turnoId, medicamentoId, dto));
    }

    @Operation(
        summary = "Actualizar turno",
        description = "Modifica los datos de un turno existente. La fecha y hora deben ser posteriores al momento actual. " +
            "La duración mínima es de 10 minutos. Debe indicarse exactamente un veterinario con rol PRINCIPAL. " +
            "No se puede repetir el mismo veterinario en el turno. No se permiten turnos superpuestos para un mismo veterinario"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turno actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos: falla de validación, fecha/hora en el pasado, veterinario repetido o sin exactamente un PRINCIPAL",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Turno, mascota o veterinario no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "El turno se superpone con otro turno del veterinario",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> updateTurno(
            @PathVariable Long id,
            @Valid @RequestBody TurnoRequestDTO turnoRequestDTO) {
        return ResponseEntity.ok(turnoService.updateTurno(id, turnoRequestDTO));
    }

    @Operation(summary = "Eliminar turno", description = "Elimina un turno del sistema a partir de su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Turno eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Turno no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTurno(@PathVariable Long id) {
        turnoService.deleteTurno(id);
        return ResponseEntity.noContent().build();
    }
}
