package com.veterinaria.Service;

import com.veterinaria.DTO.PrescripcionRequestDTO;
import com.veterinaria.DTO.PrescripcionResponseDTO;
import com.veterinaria.DTO.TurnoRequestDTO;
import com.veterinaria.DTO.TurnoResponseDTO;
import com.veterinaria.DTO.TurnoVeterinarioResponseDTO;
import com.veterinaria.DTO.TurnoVeterinarioRequestDTO;
import com.veterinaria.Entity.Mascota;
import com.veterinaria.Entity.Medicamento;
import com.veterinaria.Entity.Participacion;
import com.veterinaria.Entity.Prescripcion;
import com.veterinaria.Entity.Turno;
import com.veterinaria.Entity.Veterinario;
import com.veterinaria.Entity.enums.EstadoTurno;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoServiceImplTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private ParticipacionRepository participacionRepository;

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private PrescripcionRepository prescripcionRepository;

    @Mock
    private TurnoMapper turnoMapper;

    @InjectMocks
    private TurnoServiceImpl turnoService;

    @Test
    void getAllTurnos_retornaListaMapeada() {
        Turno turno = new Turno();
        TurnoResponseDTO response = crearResponse();
        when(turnoRepository.findAll()).thenReturn(List.of(turno));
        when(turnoMapper.toDtoList(List.of(turno))).thenReturn(List.of(response));

        List<TurnoResponseDTO> resultado = turnoService.getAllTurnos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getMotivo()).isEqualTo("Consulta general");
    }

    @Test
    void getAgenda_conParametrosValidos_retornaListaMapeada() {
        TurnoRequestDTO request = crearRequest();
        Veterinario veterinario = new Veterinario();
        Turno turno = new Turno();
        TurnoResponseDTO response = crearResponse();

        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(turnoRepository.findDistinctByParticipacionesVeterinarioIdAndFechaOrderByHoraAsc(1L, request.getFecha()))
                .thenReturn(List.of(turno));
        when(turnoMapper.toDtoList(List.of(turno))).thenReturn(List.of(response));

        List<TurnoResponseDTO> resultado = turnoService.getAgenda(1L, request.getFecha());

        assertThat(resultado).containsExactly(response);
    }

    @Test
    void getAgenda_sinFecha_lanzaBadRequestException() {
        assertThrows(BadRequestException.class, () -> turnoService.getAgenda(1L, null));
    }

    @Test
    void getAgenda_conVeterinarioInexistente_lanzaResourceNotFoundException() {
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> turnoService.getAgenda(99L, LocalDate.now().plusDays(1)));
    }

    @Test
    void getTurnoById_cuandoExiste_retornaTurnoMapeado() {
        Turno turno = new Turno();
        TurnoResponseDTO response = crearResponse();
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(turnoMapper.toDto(turno)).thenReturn(response);

        TurnoResponseDTO resultado = turnoService.getTurnoById(1L);

        assertThat(resultado.getMotivo()).isEqualTo("Consulta general");
    }

    @Test
    void getTurnoById_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(turnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> turnoService.getTurnoById(99L));
    }

    @Test
    void createTurno_exitoso_guardaTurnoUnaVez() {
        TurnoRequestDTO request = crearRequest();
        Mascota mascota = new Mascota();
        Veterinario veterinario = crearVeterinarioMock(1L);
        Turno turno = new Turno();
        TurnoResponseDTO response = new TurnoResponseDTO(
                1L,
                request.getFecha(),
                request.getHora(),
                request.getMotivo(),
                request.getDuracionMinutos(),
                EstadoTurno.PENDIENTE,
                10L,
                "Luna",
                List.of());

        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascota));
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(turnoRepository.findDistinctByParticipacionesVeterinarioIdAndFechaOrderByHoraAsc(1L, request.getFecha()))
                .thenReturn(List.of());
        when(turnoMapper.toEntity(request)).thenReturn(turno);
        when(turnoRepository.save(turno)).thenReturn(turno);
        when(turnoMapper.toDto(turno)).thenReturn(response);

        TurnoResponseDTO resultado = turnoService.createTurno(request);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(turnoRepository, times(1)).save(turno);
    }

    @Test
    void createTurno_conSuperposicion_lanzaTurnoSuperpuestoExceptionYNoGuarda() {
        TurnoRequestDTO request = crearRequest();
        Mascota mascota = new Mascota();
        Veterinario veterinario = crearVeterinarioMock(1L);

        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascota));
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        Turno turnoExistente = crearTurnoExistente(7L, request.getFecha(), LocalTime.of(10, 15), 30);
        when(turnoRepository.findDistinctByParticipacionesVeterinarioIdAndFechaOrderByHoraAsc(1L, request.getFecha()))
                .thenReturn(List.of(turnoExistente));

        TurnoSuperpuestoException exception =
                assertThrows(TurnoSuperpuestoException.class, () -> turnoService.createTurno(request));
        assertThat(exception.getMessage())
                .contains("turno con id 7")
                .contains(request.getFecha().toString())
                .contains("de 10:15 a 10:45");
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    void createTurno_conMascotaInexistente_lanzaResourceNotFoundExceptionYNoGuarda() {
        TurnoRequestDTO request = crearRequest();
        when(mascotaRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> turnoService.createTurno(request));
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    void createTurno_conVeterinarioInexistente_lanzaResourceNotFoundExceptionYNoGuarda() {
        TurnoRequestDTO request = crearRequest();
        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(new Mascota()));
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> turnoService.createTurno(request));
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    void createTurno_sinVeterinarios_lanzaBadRequestExceptionYNoGuarda() {
        TurnoRequestDTO request = crearRequest();
        request.setVeterinarios(List.of());

        assertThrows(BadRequestException.class, () -> turnoService.createTurno(request));
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    void updateTurno_cuandoExiste_actualizaYGuarda() {
        TurnoRequestDTO request = crearRequest();
        Turno turno = new Turno();
        Mascota mascota = new Mascota();
        Veterinario veterinario = crearVeterinarioMock(1L);
        TurnoResponseDTO response = crearResponse();

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(mascota));
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(turnoRepository.findDistinctByParticipacionesVeterinarioIdAndFechaOrderByHoraAsc(1L, request.getFecha()))
                .thenReturn(List.of());
        when(turnoRepository.save(turno)).thenReturn(turno);
        when(turnoMapper.toDto(turno)).thenReturn(response);

        TurnoResponseDTO resultado = turnoService.updateTurno(1L, request);

        assertThat(resultado.getMotivo()).isEqualTo("Consulta general");
        verify(turnoRepository).save(turno);
    }

    @Test
    void updateTurno_cuandoNoExiste_lanzaResourceNotFoundExceptionYNoGuarda() {
        TurnoRequestDTO request = crearRequest();
        when(turnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> turnoService.updateTurno(99L, request));
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    void updateTurno_conSuperposicion_lanzaTurnoSuperpuestoExceptionYNoGuarda() {
        TurnoRequestDTO request = crearRequest();
        Turno turno = new Turno();
        Veterinario veterinario = crearVeterinarioMock(1L);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(mascotaRepository.findById(10L)).thenReturn(Optional.of(new Mascota()));
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        Turno turnoExistente = crearTurnoExistente(7L, request.getFecha(), LocalTime.of(10, 15), 30);
        when(turnoRepository.findDistinctByParticipacionesVeterinarioIdAndFechaOrderByHoraAsc(1L, request.getFecha()))
                .thenReturn(List.of(turnoExistente));

        TurnoSuperpuestoException exception =
                assertThrows(TurnoSuperpuestoException.class, () -> turnoService.updateTurno(1L, request));
        assertThat(exception.getMessage())
                .contains("turno con id 7")
                .contains(request.getFecha().toString())
                .contains("de 10:15 a 10:45");
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    void getVeterinariosByTurno_retornaParticipacionesOrdenadas() {
        Turno turno = new Turno();
        Veterinario principal = crearVeterinarioMock(1L, "Ana", "Gomez");
        Veterinario asistente = crearVeterinarioMock(2L, "Luis", "Perez");
        Participacion participacionPrincipal = new Participacion(null, turno, principal, RolVeterinario.PRINCIPAL);
        Participacion participacionAsistente = new Participacion(null, turno, asistente, RolVeterinario.ASISTENTE);

        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));
        when(participacionRepository.findByTurnoId(1L))
                .thenReturn(List.of(participacionAsistente, participacionPrincipal));

        List<TurnoVeterinarioResponseDTO> resultado = turnoService.getVeterinariosByTurno(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombreApellido()).isEqualTo("Ana Gomez");
        assertThat(resultado.get(1).getNombreApellido()).isEqualTo("Luis Perez");
    }

    @Test
    void getVeterinariosByTurno_cuandoTurnoNoExiste_lanzaResourceNotFoundException() {
        when(turnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> turnoService.getVeterinariosByTurno(99L));
    }

    @Test
    void asociarMedicamento_conCantidad_descuentaEsaCantidadDelStock() {
        Medicamento medicamento = crearMedicamento(10);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(new Turno()));
        when(medicamentoRepository.findById(5L)).thenReturn(Optional.of(medicamento));
        when(prescripcionRepository.save(any(Prescripcion.class))).thenAnswer(inv -> inv.getArgument(0));

        PrescripcionResponseDTO resultado = turnoService.asociarMedicamento(
                1L, 5L, new PrescripcionRequestDTO(3, "Cada 8 horas"));

        assertThat(resultado.getCantidad()).isEqualTo(3);
        assertThat(resultado.getIndicaciones()).isEqualTo("Cada 8 horas");
        assertThat(medicamento.getStock()).isEqualTo(7);
        verify(medicamentoRepository).save(medicamento);
    }

    @Test
    void asociarMedicamento_sinBody_usaCantidadUno() {
        Medicamento medicamento = crearMedicamento(10);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(new Turno()));
        when(medicamentoRepository.findById(5L)).thenReturn(Optional.of(medicamento));
        when(prescripcionRepository.save(any(Prescripcion.class))).thenAnswer(inv -> inv.getArgument(0));

        PrescripcionResponseDTO resultado = turnoService.asociarMedicamento(1L, 5L, null);

        assertThat(resultado.getCantidad()).isEqualTo(1);
        assertThat(medicamento.getStock()).isEqualTo(9);
    }

    @Test
    void asociarMedicamento_conCantidadMayorAlStock_lanzaStockInsuficienteExceptionYNoGuarda() {
        Medicamento medicamento = crearMedicamento(2);
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(new Turno()));
        when(medicamentoRepository.findById(5L)).thenReturn(Optional.of(medicamento));

        StockInsuficienteException exception = assertThrows(StockInsuficienteException.class,
                () -> turnoService.asociarMedicamento(1L, 5L, new PrescripcionRequestDTO(3, null)));

        assertThat(exception.getMessage()).contains("se solicitaron 3").contains("hay 2");
        assertThat(medicamento.getStock()).isEqualTo(2);
        verify(prescripcionRepository, never()).save(any(Prescripcion.class));
        verify(medicamentoRepository, never()).save(any(Medicamento.class));
    }

    @Test
    void asociarMedicamento_conCantidadCero_lanzaBadRequestException() {
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(new Turno()));
        when(medicamentoRepository.findById(5L)).thenReturn(Optional.of(crearMedicamento(10)));

        assertThrows(BadRequestException.class,
                () -> turnoService.asociarMedicamento(1L, 5L, new PrescripcionRequestDTO(0, null)));
        verify(prescripcionRepository, never()).save(any(Prescripcion.class));
    }

    @Test
    void deleteTurno_cuandoExiste_eliminaTurno() {
        Turno turno = new Turno();
        when(turnoRepository.findById(1L)).thenReturn(Optional.of(turno));

        turnoService.deleteTurno(1L);

        verify(turnoRepository).delete(turno);
    }

    @Test
    void deleteTurno_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(turnoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> turnoService.deleteTurno(99L));
        verify(turnoRepository, never()).delete(any(Turno.class));
    }

    private TurnoRequestDTO crearRequest() {
        return new TurnoRequestDTO(
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                "Consulta general",
                30,
                10L,
                List.of(new TurnoVeterinarioRequestDTO(1L, RolVeterinario.PRINCIPAL)));
    }

    private TurnoResponseDTO crearResponse() {
        TurnoRequestDTO request = crearRequest();
        return new TurnoResponseDTO(
                1L,
                request.getFecha(),
                request.getHora(),
                request.getMotivo(),
                request.getDuracionMinutos(),
                EstadoTurno.PENDIENTE,
                10L,
                "Luna",
                List.of());
    }

    private Turno crearTurnoExistente(Long id, LocalDate fecha, LocalTime hora, Integer duracionMinutos) {
        Turno turno = new Turno();
        ReflectionTestUtils.setField(turno, "id", id);
        turno.setFecha(fecha);
        turno.setHora(hora);
        turno.setDuracionMinutos(duracionMinutos);
        return turno;
    }

    private Medicamento crearMedicamento(int stock) {
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre("Amoxicilina 500mg");
        medicamento.setPrincipioActivo("Amoxicilina");
        medicamento.setStock(stock);
        medicamento.setPrecioUnitario(new BigDecimal("1500.00"));
        return medicamento;
    }

    private Veterinario crearVeterinarioMock(Long id) {
        Veterinario veterinario = mock(Veterinario.class);
        when(veterinario.getId()).thenReturn(id);
        return veterinario;
    }

    private Veterinario crearVeterinarioMock(Long id, String nombre, String apellido) {
        Veterinario veterinario = crearVeterinarioMock(id);
        when(veterinario.getNombre()).thenReturn(nombre);
        when(veterinario.getApellido()).thenReturn(apellido);
        return veterinario;
    }
}
