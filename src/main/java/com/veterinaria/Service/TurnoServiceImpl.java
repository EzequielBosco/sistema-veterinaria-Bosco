package com.veterinaria.Service;

import com.veterinaria.DTO.TurnoRequestDTO;
import com.veterinaria.DTO.TurnoResponseDTO;
import com.veterinaria.DTO.TurnoVeterinarioDTO;
import com.veterinaria.Entity.Mascota;
import com.veterinaria.Entity.Participacion;
import com.veterinaria.Entity.Turno;
import com.veterinaria.Entity.Veterinario;
import com.veterinaria.Entity.enums.RolVeterinario;
import com.veterinaria.Exception.BadRequestException;
import com.veterinaria.Exception.DuplicateResourceException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.TurnoMapper;
import com.veterinaria.Repository.MascotaRepository;
import com.veterinaria.Repository.ParticipacionRepository;
import com.veterinaria.Repository.TurnoRepository;
import com.veterinaria.Repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final ParticipacionRepository participacionRepository;
    private final TurnoMapper turnoMapper;

    public TurnoServiceImpl(
            TurnoRepository turnoRepository,
            MascotaRepository mascotaRepository,
            VeterinarioRepository veterinarioRepository,
            ParticipacionRepository participacionRepository,
            TurnoMapper turnoMapper) {
        this.turnoRepository = turnoRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.participacionRepository = participacionRepository;
        this.turnoMapper = turnoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> getAllTurnos() {
        return turnoMapper.toDtoList(turnoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> getAgenda(Long veterinarioId, LocalDate fecha) {
        if (veterinarioId == null || fecha == null) {
            throw new BadRequestException("Debe indicar veterinarioId y fecha");
        }
        validarVeterinario(veterinarioId);
        return turnoMapper.toDtoList(
                turnoRepository.findDistinctByParticipacionesVeterinarioIdAndFechaOrderByHoraAsc(veterinarioId, fecha));
    }

    @Override
    @Transactional(readOnly = true)
    public TurnoResponseDTO getTurnoById(Long id) {
        return turnoMapper.toDto(obtenerTurno(id));
    }

    @Override
    @Transactional
    public TurnoResponseDTO createTurno(TurnoRequestDTO turnoRequestDTO) {
        validarRequest(turnoRequestDTO);
        Mascota mascota = obtenerMascota(turnoRequestDTO.getMascotaId());
        List<Veterinario> veterinarios = obtenerVeterinarios(turnoRequestDTO);
        validarDisponibilidad(veterinarios, turnoRequestDTO, null);

        Turno turno = turnoMapper.toEntity(turnoRequestDTO);
        turno.setMascota(mascota);
        asignarParticipaciones(turno, veterinarios);

        Turno turnoGuardado = turnoRepository.save(turno);
        return turnoMapper.toDto(turnoGuardado);
    }

    @Override
    @Transactional
    public TurnoResponseDTO updateTurno(Long id, TurnoRequestDTO turnoRequestDTO) {
        validarRequest(turnoRequestDTO);
        Turno turno = obtenerTurno(id);
        Mascota mascota = obtenerMascota(turnoRequestDTO.getMascotaId());
        List<Veterinario> veterinarios = obtenerVeterinarios(turnoRequestDTO);
        validarDisponibilidad(veterinarios, turnoRequestDTO, id);

        turno.setFecha(turnoRequestDTO.getFecha());
        turno.setHora(turnoRequestDTO.getHora());
        turno.setMotivo(turnoRequestDTO.getMotivo());
        turno.setMascota(mascota);
        reemplazarParticipaciones(turno, veterinarios);

        return turnoMapper.toDto(turnoRepository.save(turno));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoVeterinarioDTO> getVeterinariosByTurno(Long id) {
        obtenerTurno(id);
        return participacionRepository.findByTurnoId(id).stream()
                .sorted(Comparator
                        .comparing(Participacion::getRol)
                        .thenComparing(participacion -> participacion.getVeterinario().getId()))
                .map(this::toTurnoVeterinarioDto)
                .toList();
    }

    @Override
    public void deleteTurno(Long id) {
        Turno turno = obtenerTurno(id);
        turnoRepository.delete(turno);
    }

    private void validarRequest(TurnoRequestDTO turnoRequestDTO) {
        if (turnoRequestDTO.getFecha() == null
                || turnoRequestDTO.getHora() == null
                || turnoRequestDTO.getMascotaId() == null) {
            throw new BadRequestException("Debe indicar fecha, hora y mascotaId");
        }

        if (obtenerVeterinarioIds(turnoRequestDTO).isEmpty()) {
            throw new BadRequestException("Debe indicar al menos un veterinario en veterinarioIds");
        }
    }

    private Turno obtenerTurno(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un turno con id " + id));
    }

    private Mascota obtenerMascota(Long mascotaId) {
        return mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una mascota con id " + mascotaId));
    }

    private Veterinario validarVeterinario(Long veterinarioId) {
        return veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un veterinario con id " + veterinarioId));
    }

    private List<Veterinario> obtenerVeterinarios(TurnoRequestDTO turnoRequestDTO) {
        return obtenerVeterinarioIds(turnoRequestDTO).stream()
                .map(this::validarVeterinario)
                .toList();
    }

    private List<Long> obtenerVeterinarioIds(TurnoRequestDTO turnoRequestDTO) {
        Set<Long> veterinarioIds = new LinkedHashSet<>();

        if (turnoRequestDTO.getVeterinarioIds() != null) {
            veterinarioIds.addAll(turnoRequestDTO.getVeterinarioIds());
        }

        veterinarioIds.remove(null);
        return new ArrayList<>(veterinarioIds);
    }

    private void validarDisponibilidad(
            List<Veterinario> veterinarios,
            TurnoRequestDTO turnoRequestDTO,
            Long turnoId) {
        for (Veterinario veterinario : veterinarios) {
            boolean ocupado = turnoId == null
                    ? turnoRepository.existsDistinctByParticipacionesVeterinarioIdAndFechaAndHora(
                            veterinario.getId(), turnoRequestDTO.getFecha(), turnoRequestDTO.getHora())
                    : turnoRepository.existsDistinctByParticipacionesVeterinarioIdAndFechaAndHoraAndIdNot(
                            veterinario.getId(), turnoRequestDTO.getFecha(), turnoRequestDTO.getHora(), turnoId);

            if (ocupado) {
                throw new DuplicateResourceException(
                        "El veterinario con id " + veterinario.getId() + " ya tiene un turno en esa fecha y hora");
            }
        }
    }

    private void asignarParticipaciones(Turno turno, List<Veterinario> veterinarios) {
        for (int i = 0; i < veterinarios.size(); i++) {
            Participacion participacion = new Participacion();
            participacion.setTurno(turno);
            participacion.setVeterinario(veterinarios.get(i));
            participacion.setRol(i == 0 ? RolVeterinario.PRINCIPAL : RolVeterinario.ASISTENTE);
            turno.getParticipaciones().add(participacion);
        }
    }

    private void reemplazarParticipaciones(Turno turno, List<Veterinario> veterinarios) {
        turno.getParticipaciones().clear();
        asignarParticipaciones(turno, veterinarios);
    }

    private TurnoVeterinarioDTO toTurnoVeterinarioDto(Participacion participacion) {
        Veterinario veterinario = participacion.getVeterinario();
        return new TurnoVeterinarioDTO(
                veterinario.getId(),
                formatearNombreVeterinario(veterinario),
                participacion.getRol());
    }

    private String formatearNombreVeterinario(Veterinario veterinario) {
        if (veterinario.getApellido() == null || veterinario.getApellido().isBlank()) {
            return veterinario.getNombre();
        }
        return veterinario.getNombre() + " " + veterinario.getApellido();
    }
}
