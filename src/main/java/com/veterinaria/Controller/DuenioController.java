package com.veterinaria.Controller;

import com.veterinaria.Entity.Duenio;
import com.veterinaria.Exception.DuplicateResourceException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Service.DuenioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/duenios")
public class DuenioController {

    private final DuenioService duenioService;

    public DuenioController(DuenioService duenioService) {
        this.duenioService = duenioService;
    }

    @GetMapping
    public ResponseEntity<List<Duenio>> getAllDuenios() {
        return ResponseEntity.ok(duenioService.getAllDuenios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Duenio> getDuenioById(@PathVariable Long id) {
        return ResponseEntity.ok(duenioService.getDuenioById(id));
    }

    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<Duenio> getDuenioByCedula(@PathVariable String cedula) {
        return ResponseEntity.ok(duenioService.getDuenioByCedula(cedula));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Duenio>> searchDuenios(
            @RequestParam String nombre,
            @RequestParam String apellido) {
        return ResponseEntity.ok(duenioService.searchDuenios(nombre, apellido));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Duenio> getDuenioByEmail(@PathVariable String email) {
        return ResponseEntity.ok(duenioService.getDuenioByEmail(email));
    }

    @PostMapping
    public ResponseEntity<Duenio> createDuenio(@RequestBody Duenio duenio) {
        return ResponseEntity.status(HttpStatus.CREATED).body(duenioService.createDuenio(duenio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Duenio> updateDuenio(@PathVariable Long id, @RequestBody Duenio duenioActualizado) {
        return ResponseEntity.ok(duenioService.updateDuenio(id, duenioActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDuenio(@PathVariable Long id) {
        duenioService.deleteDuenio(id);
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
