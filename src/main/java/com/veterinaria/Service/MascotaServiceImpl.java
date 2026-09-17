package com.veterinaria.Service;

import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.Entity.Duenio;
import com.veterinaria.Entity.Mascota;
import com.veterinaria.Exception.BadRequestException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Repository.DuenioRepository;
import com.veterinaria.Repository.MascotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;
    private final DuenioRepository duenioRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository, DuenioRepository duenioRepository) {
        this.mascotaRepository = mascotaRepository;
        this.duenioRepository = duenioRepository;
    }

    @Override
    public List<Mascota> getAllMascotas() {
        return mascotaRepository.findAll();
    }

    @Override
    public Mascota getMascotaById(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una mascota con id " + id));
    }

    @Override
    public List<Mascota> getMascotasByDuenioId(Long duenioId) {
        validarDuenioExistente(duenioId);
        return mascotaRepository.findByDuenioId(duenioId);
    }

    @Override
    public Mascota createMascota(Long duenioId, Mascota mascota) {
        mascota.setDuenio(obtenerDuenioObligatorio(duenioId));
        return mascotaRepository.save(mascota);
    }

    @Override
    public Mascota updateMascota(Long id, MascotaRequestDTO mascotaActualizada) {
        Mascota mascotaExistente = getMascotaById(id);

        mascotaExistente.setNombre(mascotaActualizada.getNombre());
        mascotaExistente.setEspecie(mascotaActualizada.getEspecie());
        mascotaExistente.setRaza(mascotaActualizada.getRaza());
        mascotaExistente.setColor(mascotaActualizada.getColor());
        mascotaExistente.setFechaNacimiento(mascotaActualizada.getFechaNacimiento());

        if (mascotaActualizada.getDuenioId() != null) {
            mascotaExistente.setDuenio(validarDuenioExistente(mascotaActualizada.getDuenioId()));
        }

        return mascotaRepository.save(mascotaExistente);
    }

    @Override
    public void deleteMascota(Long id) {
        Mascota mascota = getMascotaById(id);
        mascotaRepository.delete(mascota);
    }

    private Duenio obtenerDuenioObligatorio(Long duenioId) {
        if (duenioId == null) {
            throw new BadRequestException("Debe indicar el id del duenio de la mascota");
        }

        return validarDuenioExistente(duenioId);
    }

    private Duenio validarDuenioExistente(Long duenioId) {
        return duenioRepository.findById(duenioId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un duenio con id " + duenioId));
    }
}
