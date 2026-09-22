package com.veterinaria.Controller;

import com.veterinaria.DTO.DuenioRequestDTO;
import com.veterinaria.DTO.DuenioResponseDTO;
import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.DTO.MascotaResponseDTO;
import com.veterinaria.Exception.ErrorResponse;
import com.veterinaria.Service.DuenioService;
import com.veterinaria.Service.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Dueños", description = "Gestión de dueños y sus mascotas asociadas")
@RestController
@RequestMapping("/api/duenios")
public class DuenioController {

    private final DuenioService duenioService;
    private final MascotaService mascotaService;

    public DuenioController(
            DuenioService duenioService,
            MascotaService mascotaService) {
        this.duenioService = duenioService;
        this.mascotaService = mascotaService;
    }

    @Operation(summary = "Listar todos los dueños", description = "Retorna la lista completa de dueños registrados")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<DuenioResponseDTO>> getAllDuenios() {
        return ResponseEntity.ok(duenioService.getAllDuenios());
    }

    @Operation(summary = "Obtener dueño por ID", description = "Retorna los datos de un dueño a partir de su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dueño encontrado"),
        @ApiResponse(responseCode = "404", description = "Dueño no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DuenioResponseDTO> getDuenioById(@PathVariable Long id) {
        return ResponseEntity.ok(duenioService.getDuenioById(id));
    }

    @Operation(summary = "Listar mascotas de un dueño", description = "Retorna todas las mascotas registradas a nombre del dueño indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Dueño no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<MascotaResponseDTO>> getMascotasByDuenioId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.getMascotasByDuenioId(id));
    }

    @Operation(summary = "Obtener dueño por cédula", description = "Retorna los datos de un dueño buscando por su número de cédula o DNI")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dueño encontrado"),
        @ApiResponse(responseCode = "404", description = "Dueño no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<DuenioResponseDTO> getDuenioByCedula(@PathVariable String cedula) {
        return ResponseEntity.ok(duenioService.getDuenioByCedula(cedula));
    }

    @Operation(summary = "Buscar dueños por nombre y apellido", description = "Retorna los dueños cuyo nombre y apellido coincidan con los parámetros de búsqueda")
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    @GetMapping("/search")
    public ResponseEntity<List<DuenioResponseDTO>> searchDuenios(
            @RequestParam String nombre,
            @RequestParam String apellido) {
        return ResponseEntity.ok(duenioService.searchDuenios(nombre, apellido));
    }

    @Operation(summary = "Obtener dueño por email", description = "Retorna los datos de un dueño buscando por su correo electrónico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dueño encontrado"),
        @ApiResponse(responseCode = "404", description = "Dueño no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<DuenioResponseDTO> getDuenioByEmail(@PathVariable String email) {
        return ResponseEntity.ok(duenioService.getDuenioByEmail(email));
    }

    @Operation(
        summary = "Registrar un nuevo dueño",
        description = "Crea un nuevo dueño. La cédula debe tener entre 7 y 8 dígitos. El email y la cédula deben ser únicos en el sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Dueño creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (falla de validación)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "El email o la cédula ya están registrados",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DuenioResponseDTO> createDuenio(@Valid @RequestBody DuenioRequestDTO duenioRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(duenioService.createDuenio(duenioRequestDTO));
    }

    @Operation(
        summary = "Actualizar dueño",
        description = "Modifica los datos de un dueño existente. La cédula debe tener entre 7 y 8 dígitos. El email y la cédula deben ser únicos en el sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dueño actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (falla de validación)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Dueño no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "El email o la cédula ya pertenecen a otro dueño",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DuenioResponseDTO> updateDuenio(
            @PathVariable Long id,
            @Valid @RequestBody DuenioRequestDTO duenioRequestDTO) {
        return ResponseEntity.ok(duenioService.updateDuenio(id, duenioRequestDTO));
    }

    @Operation(summary = "Eliminar dueño", description = "Elimina un dueño del sistema a partir de su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Dueño eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Dueño no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDuenio(@PathVariable Long id) {
        duenioService.deleteDuenio(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Registrar mascota para un dueño",
        description = "Crea una nueva mascota asociada al dueño indicado. El sexo debe ser MACHO o HEMBRA y la fecha de nacimiento no puede ser futura"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mascota creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (falla de validación)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Dueño no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{duenioId}/mascotas")
    public ResponseEntity<MascotaResponseDTO> createMascota(
            @PathVariable Long duenioId,
            @Valid @RequestBody MascotaRequestDTO mascotaRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mascotaService.createMascota(duenioId, mascotaRequestDTO));
    }
}
