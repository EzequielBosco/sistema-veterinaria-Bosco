package com.veterinaria.Service;

import com.veterinaria.DTO.DuenioRequestDTO;
import com.veterinaria.DTO.DuenioResponseDTO;
import com.veterinaria.Entity.Duenio;
import com.veterinaria.Exception.DuplicateResourceException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.DuenioMapper;
import com.veterinaria.Repository.DuenioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DuenioServiceImpl implements DuenioService {

    private final DuenioRepository duenioRepository;
    private final DuenioMapper duenioMapper;

    public DuenioServiceImpl(DuenioRepository duenioRepository, DuenioMapper duenioMapper) {
        this.duenioRepository = duenioRepository;
        this.duenioMapper = duenioMapper;
    }

    @Override
    public List<DuenioResponseDTO> getAllDuenios() {
        return duenioMapper.toResponseDtoList(duenioRepository.findAll());
    }

    @Override
    public DuenioResponseDTO getDuenioById(Long id) {
        return duenioMapper.toResponseDto(obtenerDuenio(id));
    }

    @Override
    public DuenioResponseDTO getDuenioByCedula(String cedula) {
        return duenioMapper.toResponseDto(duenioRepository.findByCedula(cedula)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un duenio con cedula/DNI " + cedula)));
    }

    @Override
    public List<DuenioResponseDTO> searchDuenios(String nombre, String apellido) {
        return duenioMapper.toResponseDtoList(duenioRepository.findByNombreAndApellido(nombre, apellido));
    }

    @Override
    public DuenioResponseDTO getDuenioByEmail(String email) {
        return duenioMapper.toResponseDto(duenioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un duenio con email " + email)));
    }

    @Override
    public DuenioResponseDTO createDuenio(DuenioRequestDTO duenioRequestDTO) {
        normalizarEmail(duenioRequestDTO);
        Duenio duenio = duenioMapper.toEntity(duenioRequestDTO);
        if (duenioRepository.existsByCedula(duenio.getCedula())) {
            throw new DuplicateResourceException("La cedula/DNI ya esta registrada");
        }
        if (duenio.getEmail() != null && duenioRepository.existsByEmail(duenio.getEmail())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }
        return duenioMapper.toResponseDto(duenioRepository.save(duenio));
    }

    @Override
    public DuenioResponseDTO updateDuenio(Long id, DuenioRequestDTO duenioRequestDTO) {
        normalizarEmail(duenioRequestDTO);
        Duenio duenioActualizado = duenioMapper.toEntity(duenioRequestDTO);
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

        return duenioMapper.toResponseDto(duenioRepository.save(duenioExistente));
    }

    @Override
    public void deleteDuenio(Long id) {
        Optional<Duenio> duenioOpt = duenioRepository.findById(id);
        if (duenioOpt.isEmpty()) {
            throw new ResourceNotFoundException("No existe un duenio con id " + id);
        }
        duenioRepository.delete(duenioOpt.get());
    }

    private Duenio obtenerDuenio(Long id) {
        return duenioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un duenio con id " + id));
    }

    private void normalizarEmail(DuenioRequestDTO duenioRequestDTO) {
        if (duenioRequestDTO.getEmail() != null && duenioRequestDTO.getEmail().isBlank()) {
            duenioRequestDTO.setEmail(null);
        }
    }
}
