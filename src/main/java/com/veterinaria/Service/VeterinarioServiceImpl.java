package com.veterinaria.Service;

import com.veterinaria.DTO.VeterinarioRequestDTO;
import com.veterinaria.DTO.VeterinarioResponseDTO;
import com.veterinaria.DTO.VeterinarioTurnoResponseDTO;
import com.veterinaria.Entity.Participacion;
import com.veterinaria.Entity.Turno;
import com.veterinaria.Entity.Veterinario;
import com.veterinaria.Exception.BadRequestException;
import com.veterinaria.Exception.DuplicateResourceException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.VeterinarioMapper;
import com.veterinaria.Repository.ParticipacionRepository;
import com.veterinaria.Repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final ParticipacionRepository participacionRepository;
    private final VeterinarioMapper veterinarioMapper;

    public VeterinarioServiceImpl(
            VeterinarioRepository veterinarioRepository,
            ParticipacionRepository participacionRepository,
            VeterinarioMapper veterinarioMapper) {
        this.veterinarioRepository = veterinarioRepository;
        this.participacionRepository = participacionRepository;
        this.veterinarioMapper = veterinarioMapper;
    }

    @Override
    public List<VeterinarioResponseDTO> getAllVeterinarios() {
        return veterinarioMapper.toResponseDtoList(veterinarioRepository.findAll());
    }

    @Override
    public VeterinarioResponseDTO getVeterinarioById(Long id) {
        return veterinarioMapper.toResponseDto(obtenerVeterinario(id));
    }

    @Override
    public VeterinarioResponseDTO createVeterinario(VeterinarioRequestDTO veterinarioRequestDTO) {
        validarCamposObligatorios(veterinarioRequestDTO);
        validarMatriculaDisponible(veterinarioRequestDTO.getMatricula());
        validarEmailDisponible(veterinarioRequestDTO.getEmail());
        Veterinario veterinario = veterinarioMapper.toEntity(veterinarioRequestDTO);
        return veterinarioMapper.toResponseDto(veterinarioRepository.save(veterinario));
    }

    @Override
    public VeterinarioResponseDTO updateVeterinario(Long id, VeterinarioRequestDTO veterinarioRequestDTO) {
        validarCamposObligatorios(veterinarioRequestDTO);
        Veterinario veterinario = obtenerVeterinario(id);

        if (!veterinario.getMatricula().equals(veterinarioRequestDTO.getMatricula())) {
            validarMatriculaDisponible(veterinarioRequestDTO.getMatricula());
        }

        if (veterinarioRequestDTO.getEmail() != null
                && !veterinarioRequestDTO.getEmail().equals(veterinario.getEmail())) {
            validarEmailDisponible(veterinarioRequestDTO.getEmail());
        }

        veterinario.setNombre(veterinarioRequestDTO.getNombre());
        veterinario.setApellido(veterinarioRequestDTO.getApellido());
        veterinario.setTelefono(veterinarioRequestDTO.getTelefono());
        veterinario.setEmail(veterinarioRequestDTO.getEmail());
        veterinario.setMatricula(veterinarioRequestDTO.getMatricula());
        veterinario.setEspecialidad(veterinarioRequestDTO.getEspecialidad());

        return veterinarioMapper.toResponseDto(veterinarioRepository.save(veterinario));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioTurnoResponseDTO> getTurnosByVeterinario(Long id) {
        obtenerVeterinario(id);
        return participacionRepository.findByVeterinarioId(id).stream()
                .sorted(Comparator
                        .comparing((Participacion participacion) -> participacion.getTurno().getFecha())
                        .thenComparing(participacion -> participacion.getTurno().getHora()))
                .map(this::toVeterinarioTurnoDto)
                .toList();
    }

    @Override
    public void deleteVeterinario(Long id) {
        Veterinario veterinario = obtenerVeterinario(id);
        veterinarioRepository.delete(veterinario);
    }

    private Veterinario obtenerVeterinario(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un veterinario con id " + id));
    }

    private void validarMatriculaDisponible(String matricula) {
        if (veterinarioRepository.existsByMatricula(matricula)) {
            throw new DuplicateResourceException("La matricula ya esta registrada");
        }
    }

    private void validarEmailDisponible(String email) {
        if (email != null && veterinarioRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }
    }

    private void validarCamposObligatorios(VeterinarioRequestDTO veterinarioRequestDTO) {
        if (veterinarioRequestDTO.getNombre() == null || veterinarioRequestDTO.getNombre().isBlank()
                || veterinarioRequestDTO.getApellido() == null || veterinarioRequestDTO.getApellido().isBlank()
                || veterinarioRequestDTO.getTelefono() == null || veterinarioRequestDTO.getTelefono().isBlank()
                || veterinarioRequestDTO.getMatricula() == null || veterinarioRequestDTO.getMatricula().isBlank()
                || veterinarioRequestDTO.getEspecialidad() == null || veterinarioRequestDTO.getEspecialidad().isBlank()) {
            throw new BadRequestException("Debe indicar nombre, apellido, telefono, matricula y especialidad");
        }
    }

    private VeterinarioTurnoResponseDTO toVeterinarioTurnoDto(Participacion participacion) {
        Turno turno = participacion.getTurno();
        return new VeterinarioTurnoResponseDTO(
                turno.getId(),
                turno.getFecha(),
                turno.getHora(),
                turno.getMotivo(),
                turno.getMascota().getNombre(),
                participacion.getRol());
    }
}
