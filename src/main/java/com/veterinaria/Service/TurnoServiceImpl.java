package com.veterinaria.Service;

import com.veterinaria.DTO.PrescripcionRequestDTO;
import com.veterinaria.DTO.PrescripcionResponseDTO;
import com.veterinaria.DTO.TurnoRequestDTO;
import com.veterinaria.DTO.TurnoResponseDTO;
import com.veterinaria.DTO.TurnoVeterinarioRequestDTO;
import com.veterinaria.DTO.TurnoVeterinarioResponseDTO;
import com.veterinaria.Entity.Mascota;
import com.veterinaria.Entity.Medicamento;
import com.veterinaria.Entity.Participacion;
import com.veterinaria.Entity.Prescripcion;
import com.veterinaria.Entity.Turno;
import com.veterinaria.Entity.Veterinario;
import com.veterinaria.Entity.enums.RolVeterinario;
import com.veterinaria.Exception.BadRequestException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Exception.StockInsuficienteException;
import com.veterinaria.Exception.TurnoSuperpuestoException;
import com.veterinaria.Mapper.TurnoMapper;
import com.veterinaria.Repository.MascotaRepository;
import com.veterinaria.Repository.MedicamentoRepository;
import com.veterinaria.Repository.ParticipacionRepository;
import com.veterinaria.Repository.PrescripcionRepository;
import com.veterinaria.Repository.TurnoRepository;
import com.veterinaria.Repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final ParticipacionRepository participacionRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final PrescripcionRepository prescripcionRepository;
    private final TurnoMapper turnoMapper;

    public TurnoServiceImpl(
            TurnoRepository turnoRepository,
            MascotaRepository mascotaRepository,
            VeterinarioRepository veterinarioRepository,
            ParticipacionRepository participacionRepository,
            MedicamentoRepository medicamentoRepository,
            PrescripcionRepository prescripcionRepository,
            TurnoMapper turnoMapper) {
        this.turnoRepository = turnoRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.participacionRepository = participacionRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.prescripcionRepository = prescripcionRepository;
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
        Map<TurnoVeterinarioRequestDTO, Veterinario> veterinarios = obtenerVeterinarios(turnoRequestDTO);
        validarDisponibilidad(veterinarios.values().stream().toList(), turnoRequestDTO, null);

        Turno turno = turnoMapper.toEntity(turnoRequestDTO);
        turno.setMascota(mascota);
        asignarParticipaciones(turno, veterinarios);

        return turnoMapper.toDto(turnoRepository.save(turno));
    }

    @Override
    @Transactional
    public TurnoResponseDTO updateTurno(Long id, TurnoRequestDTO turnoRequestDTO) {
        validarRequest(turnoRequestDTO);
        Turno turno = obtenerTurno(id);
        Mascota mascota = obtenerMascota(turnoRequestDTO.getMascotaId());
        Map<TurnoVeterinarioRequestDTO, Veterinario> veterinarios = obtenerVeterinarios(turnoRequestDTO);
        validarDisponibilidad(veterinarios.values().stream().toList(), turnoRequestDTO, id);

        turno.setFecha(turnoRequestDTO.getFecha());
        turno.setHora(turnoRequestDTO.getHora());
        turno.setMotivo(turnoRequestDTO.getMotivo());
        turno.setDuracionMinutos(turnoRequestDTO.getDuracionMinutos());
        turno.setMascota(mascota);
        reemplazarParticipaciones(turno, veterinarios);

        return turnoMapper.toDto(turnoRepository.save(turno));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TurnoVeterinarioResponseDTO> getVeterinariosByTurno(Long id) {
        obtenerTurno(id);
        return participacionRepository.findByTurnoId(id).stream()
                .sorted(Comparator
                        .comparing(Participacion::getRol)
                        .thenComparing(participacion -> participacion.getVeterinario().getId()))
                .map(this::toTurnoVeterinarioDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescripcionResponseDTO> getPrescripcionesByTurno(Long id) {
        obtenerTurno(id);
        return prescripcionRepository.findByTurnoId(id).stream()
                .sorted(Comparator.comparing(p -> p.getMedicamento().getNombre()))
                .map(this::toPrescripcionResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public PrescripcionResponseDTO asociarMedicamento(Long turnoId, Long medicamentoId, PrescripcionRequestDTO dto) {
        Turno turno = obtenerTurno(turnoId);
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un medicamento con id " + medicamentoId));

        if (medicamento.getStock() <= 0) {
            throw new StockInsuficienteException(
                    "El medicamento '" + medicamento.getNombre() + "' no tiene stock disponible");
        }

        Prescripcion prescripcion = new Prescripcion();
        prescripcion.setTurno(turno);
        prescripcion.setMedicamento(medicamento);
        prescripcion.setCantidad(1);
        prescripcion.setIndicaciones(dto != null ? dto.getIndicaciones() : null);

        medicamento.setStock(medicamento.getStock() - 1);
        medicamentoRepository.save(medicamento);

        return toPrescripcionResponseDto(prescripcionRepository.save(prescripcion));
    }

    @Override
    public void deleteTurno(Long id) {
        Turno turno = obtenerTurno(id);
        turnoRepository.delete(turno);
    }

    private void validarRequest(TurnoRequestDTO turnoRequestDTO) {
        if (turnoRequestDTO.getFecha() == null
                || turnoRequestDTO.getHora() == null
                || turnoRequestDTO.getDuracionMinutos() == null
                || turnoRequestDTO.getMascotaId() == null
                || turnoRequestDTO.getMotivo() == null
                || turnoRequestDTO.getMotivo().isBlank()) {
            throw new BadRequestException("Debe indicar fecha, hora, duracion, motivo y mascotaId");
        }

        if (turnoRequestDTO.getDuracionMinutos() < 10) {
            throw new BadRequestException("La duracion debe ser de al menos 10 minutos");
        }

        if (turnoRequestDTO.getVeterinarios() == null || turnoRequestDTO.getVeterinarios().isEmpty()) {
            throw new BadRequestException("Debe indicar al menos un veterinario");
        }

        if (turnoRequestDTO.getVeterinarios().stream().anyMatch(veterinario -> veterinario == null
                || veterinario.getVeterinarioId() == null
                || veterinario.getRol() == null)) {
            throw new BadRequestException("Debe indicar veterinarioId y rol para cada veterinario");
        }

        long cantidadVeterinariosUnicos = turnoRequestDTO.getVeterinarios().stream()
                .map(TurnoVeterinarioRequestDTO::getVeterinarioId)
                .distinct()
                .count();

        if (cantidadVeterinariosUnicos != turnoRequestDTO.getVeterinarios().size()) {
            throw new BadRequestException("No se puede repetir el mismo veterinario en un turno");
        }

        long cantidadPrincipales = turnoRequestDTO.getVeterinarios().stream()
                .filter(veterinario -> RolVeterinario.PRINCIPAL.equals(veterinario.getRol()))
                .count();

        if (cantidadPrincipales != 1) {
            throw new BadRequestException("Debe indicar exactamente un veterinario con rol PRINCIPAL");
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

    private Map<TurnoVeterinarioRequestDTO, Veterinario> obtenerVeterinarios(TurnoRequestDTO turnoRequestDTO) {
        Map<TurnoVeterinarioRequestDTO, Veterinario> veterinarios = new LinkedHashMap<>();

        for (TurnoVeterinarioRequestDTO veterinarioRequestDTO : turnoRequestDTO.getVeterinarios()) {
            veterinarios.put(
                    veterinarioRequestDTO,
                    validarVeterinario(veterinarioRequestDTO.getVeterinarioId()));
        }

        return veterinarios;
    }

    private void validarDisponibilidad(
            List<Veterinario> veterinarios,
            TurnoRequestDTO turnoRequestDTO,
            Long turnoId) {
        for (Veterinario veterinario : veterinarios) {
            if (tieneTurnoSuperpuesto(veterinario.getId(), turnoRequestDTO, turnoId)) {
                throw new TurnoSuperpuestoException(
                        "El veterinario con id " + veterinario.getId() + " ya tiene un turno en esa fecha y hora");
            }
        }
    }

    private boolean tieneTurnoSuperpuesto(Long veterinarioId, TurnoRequestDTO turnoRequestDTO, Long turnoId) {
        LocalTime inicioNuevo = turnoRequestDTO.getHora();
        LocalTime finNuevo = inicioNuevo.plusMinutes(turnoRequestDTO.getDuracionMinutos());

        return turnoRepository.findDistinctByParticipacionesVeterinarioIdAndFechaOrderByHoraAsc(
                        veterinarioId,
                        turnoRequestDTO.getFecha())
                .stream()
                .filter(turno -> turnoId == null || !turnoId.equals(turno.getId()))
                .anyMatch(turno -> {
                    LocalTime inicioExistente = turno.getHora();
                    LocalTime finExistente = inicioExistente.plusMinutes(turno.getDuracionMinutos());
                    return inicioExistente.isBefore(finNuevo) && inicioNuevo.isBefore(finExistente);
                });
    }

    private void asignarParticipaciones(Turno turno, Map<TurnoVeterinarioRequestDTO, Veterinario> veterinarios) {
        for (Map.Entry<TurnoVeterinarioRequestDTO, Veterinario> entry : veterinarios.entrySet()) {
            Participacion participacion = new Participacion();
            participacion.setTurno(turno);
            participacion.setVeterinario(entry.getValue());
            participacion.setRol(entry.getKey().getRol());
            turno.getParticipaciones().add(participacion);
        }
    }

    private void reemplazarParticipaciones(Turno turno, Map<TurnoVeterinarioRequestDTO, Veterinario> veterinarios) {
        turno.getParticipaciones().clear();
        asignarParticipaciones(turno, veterinarios);
    }

    private TurnoVeterinarioResponseDTO toTurnoVeterinarioDto(Participacion participacion) {
        Veterinario veterinario = participacion.getVeterinario();
        return new TurnoVeterinarioResponseDTO(
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

    private PrescripcionResponseDTO toPrescripcionResponseDto(Prescripcion prescripcion) {
        Medicamento m = prescripcion.getMedicamento();
        return new PrescripcionResponseDTO(
                prescripcion.getId(),
                m.getNombre(),
                m.getPrincipioActivo(),
                prescripcion.getCantidad(),
                m.getPrecioUnitario(),
                prescripcion.getIndicaciones());
    }
}
