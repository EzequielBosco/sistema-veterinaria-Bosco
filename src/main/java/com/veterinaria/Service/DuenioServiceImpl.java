package com.veterinaria.Service;

import com.veterinaria.Entity.Duenio;
import com.veterinaria.Exception.DuplicateResourceException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Repository.DuenioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DuenioServiceImpl implements DuenioService {

    private final DuenioRepository duenioRepository;

    public DuenioServiceImpl(DuenioRepository duenioRepository) {
        this.duenioRepository = duenioRepository;
    }

    @Override
    public List<Duenio> getAllDuenios() {
        return duenioRepository.findAll();
    }

    @Override
    public Duenio getDuenioById(Long id) {
        return duenioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un duenio con id " + id));
    }

    @Override
    public Duenio getDuenioByCedula(String cedula) {
        return duenioRepository.findByCedula(cedula)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un duenio con cedula/DNI " + cedula));
    }

    @Override
    public List<Duenio> searchDuenios(String nombre, String apellido) {
        return duenioRepository.findByNombreAndApellido(nombre, apellido);
    }

    @Override
    public Duenio getDuenioByEmail(String email) {
        return duenioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un duenio con email " + email));
    }

    @Override
    public Duenio createDuenio(Duenio duenio) {
        if (duenioRepository.existsByCedula(duenio.getCedula())) {
            throw new DuplicateResourceException("La cedula/DNI ya esta registrada");
        }
        if (duenio.getEmail() != null && duenioRepository.existsByEmail(duenio.getEmail())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }
        return duenioRepository.save(duenio);
    }

    @Override
    public Duenio updateDuenio(Long id, Duenio duenioActualizado) {
        Optional<Duenio> duenioExistenteOpt = duenioRepository.findById(id);
        if (duenioExistenteOpt.isEmpty()) {
            throw new ResourceNotFoundException("No existe un duenio con id " + id);
        }

        Duenio duenioExistente = duenioExistenteOpt.get();

        if (!duenioExistente.getCedula().equals(duenioActualizado.getCedula())) {
            if (duenioRepository.existsByCedula(duenioActualizado.getCedula())) {
                throw new DuplicateResourceException("La cedula/DNI ya esta registrada");
            }
        }
        if (duenioActualizado.getEmail() != null
                && !duenioActualizado.getEmail().equals(duenioExistente.getEmail())
                && duenioRepository.existsByEmail(duenioActualizado.getEmail())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }

        duenioExistente.setNombre(duenioActualizado.getNombre());
        duenioExistente.setApellido(duenioActualizado.getApellido());
        duenioExistente.setTelefono(duenioActualizado.getTelefono());
        duenioExistente.setEmail(duenioActualizado.getEmail());
        duenioExistente.setCedula(duenioActualizado.getCedula());

        return duenioRepository.save(duenioExistente);
    }

    @Override
    public void deleteDuenio(Long id) {
        Optional<Duenio> duenioOpt = duenioRepository.findById(id);
        if (duenioOpt.isEmpty()) {
            throw new ResourceNotFoundException("No existe un duenio con id " + id);
        }
        duenioRepository.delete(duenioOpt.get());
    }
}
