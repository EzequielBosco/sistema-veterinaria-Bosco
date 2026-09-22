package com.veterinaria.Service;

import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.DTO.MascotaResponseDTO;
import com.veterinaria.Entity.Duenio;
import com.veterinaria.Entity.Mascota;
import com.veterinaria.Exception.BadRequestException;
import com.veterinaria.Exception.CupoMascotasExcedidoException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.MascotaMapper;
import com.veterinaria.Repository.DuenioRepository;
import com.veterinaria.Repository.MascotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaServiceImpl implements MascotaService {

    private static final int MAX_MASCOTAS_POR_DUENIO = 5;

    private final MascotaRepository mascotaRepository;
    private final DuenioRepository duenioRepository;
    private final MascotaMapper mascotaMapper;

    public MascotaServiceImpl(
            MascotaRepository mascotaRepository,
            DuenioRepository duenioRepository,
            MascotaMapper mascotaMapper) {
        this.mascotaRepository = mascotaRepository;
        this.duenioRepository = duenioRepository;
        this.mascotaMapper = mascotaMapper;
    }

    @Override
    public List<MascotaResponseDTO> getAllMascotas() {
        return mascotaMapper.toResponseDtoList(mascotaRepository.findAll());
    }

    @Override
    public MascotaResponseDTO getMascotaById(Long id) {
        return mascotaMapper.toResponseDto(obtenerMascota(id));
    }

    @Override
    public List<MascotaResponseDTO> getMascotasByDuenioId(Long duenioId) {
        validarDuenioExistente(duenioId);
        return mascotaMapper.toResponseDtoList(mascotaRepository.findByDuenioId(duenioId));
    }

    @Override
    public MascotaResponseDTO createMascota(Long duenioId, MascotaRequestDTO mascotaRequestDTO) {
        Mascota mascota = mascotaMapper.toEntity(mascotaRequestDTO);
        mascota.setDuenio(obtenerDuenioObligatorio(duenioId));
        validarCupoMascotas(duenioId);
        return mascotaMapper.toResponseDto(mascotaRepository.save(mascota));
    }

    @Override
    public MascotaResponseDTO updateMascota(Long id, MascotaRequestDTO mascotaRequestDTO) {
        Mascota mascotaExistente = obtenerMascota(id);

        mascotaExistente.setNombre(mascotaRequestDTO.getNombre());
        mascotaExistente.setEspecie(mascotaRequestDTO.getEspecie());
        mascotaExistente.setRaza(mascotaRequestDTO.getRaza());
        mascotaExistente.setColor(mascotaRequestDTO.getColor());
        mascotaExistente.setSexo(mascotaRequestDTO.getSexo());
        mascotaExistente.setFechaNacimiento(mascotaRequestDTO.getFechaNacimiento());

        if (mascotaRequestDTO.getDuenioId() != null) {
            mascotaExistente.setDuenio(validarDuenioExistente(mascotaRequestDTO.getDuenioId()));
        }

        return mascotaMapper.toResponseDto(mascotaRepository.save(mascotaExistente));
    }

    @Override
    public void deleteMascota(Long id) {
        Mascota mascota = obtenerMascota(id);
        mascotaRepository.delete(mascota);
    }

    private Mascota obtenerMascota(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una mascota con id " + id));
    }

    private Duenio obtenerDuenioObligatorio(Long duenioId) {
        if (duenioId == null) {
            throw new BadRequestException("Debe indicar el id del duenio de la mascota");
        }

        return validarDuenioExistente(duenioId);
    }

    private void validarCupoMascotas(Long duenioId) {
        long cantidadMascotas = mascotaRepository.countByDuenioId(duenioId);
        if (cantidadMascotas >= MAX_MASCOTAS_POR_DUENIO) {
            throw new CupoMascotasExcedidoException(
                    "El duenio con id " + duenioId + " ya tiene " + cantidadMascotas
                            + " mascotas activas. El limite es de " + MAX_MASCOTAS_POR_DUENIO + " mascotas por duenio");
        }
    }

    private Duenio validarDuenioExistente(Long duenioId) {
        return duenioRepository.findById(duenioId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un duenio con id " + duenioId));
    }
}
