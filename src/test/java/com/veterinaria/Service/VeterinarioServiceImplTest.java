package com.veterinaria.Service;

import com.veterinaria.DTO.VeterinarioRequestDTO;
import com.veterinaria.DTO.VeterinarioResponseDTO;
import com.veterinaria.DTO.VeterinarioTurnoResponseDTO;
import com.veterinaria.Entity.Mascota;
import com.veterinaria.Entity.Participacion;
import com.veterinaria.Entity.Turno;
import com.veterinaria.Entity.Veterinario;
import com.veterinaria.Entity.enums.RolVeterinario;
import com.veterinaria.Exception.BadRequestException;
import com.veterinaria.Exception.DuplicateResourceException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.VeterinarioMapper;
import com.veterinaria.Repository.ParticipacionRepository;
import com.veterinaria.Repository.VeterinarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeterinarioServiceImplTest {

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private ParticipacionRepository participacionRepository;

    @Mock
    private VeterinarioMapper veterinarioMapper;

    @InjectMocks
    private VeterinarioServiceImpl veterinarioService;

    @Test
    void getAllVeterinarios_retornaListaMapeada() {
        Veterinario veterinario = crearVeterinario();
        VeterinarioResponseDTO response = crearResponse();
        when(veterinarioRepository.findAll()).thenReturn(List.of(veterinario));
        when(veterinarioMapper.toResponseDtoList(List.of(veterinario))).thenReturn(List.of(response));

        List<VeterinarioResponseDTO> resultado = veterinarioService.getAllVeterinarios();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getNombre()).isEqualTo("Laura");
    }

    @Test
    void getAllVeterinarios_sinDatos_retornaListaVacia() {
        when(veterinarioRepository.findAll()).thenReturn(List.of());
        when(veterinarioMapper.toResponseDtoList(List.of())).thenReturn(List.of());

        List<VeterinarioResponseDTO> resultado = veterinarioService.getAllVeterinarios();

        assertThat(resultado).isEmpty();
    }

    @Test
    void getVeterinarioById_cuandoExiste_retornaVeterinarioMapeado() {
        Veterinario veterinario = crearVeterinario();
        VeterinarioResponseDTO response = crearResponse();
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(veterinarioMapper.toResponseDto(veterinario)).thenReturn(response);

        VeterinarioResponseDTO resultado = veterinarioService.getVeterinarioById(1L);

        assertThat(resultado.getNombre()).isEqualTo("Laura");
    }

    @Test
    void getVeterinarioById_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> veterinarioService.getVeterinarioById(99L));
    }

    @Test
    void createVeterinario_exitoso_guardaVeterinario() {
        VeterinarioRequestDTO request = crearRequest();
        Veterinario veterinario = crearVeterinario();
        VeterinarioResponseDTO response = crearResponse();
        when(veterinarioRepository.existsByMatricula("MAT-123")).thenReturn(false);
        when(veterinarioRepository.existsByEmail("laura@mail.com")).thenReturn(false);
        when(veterinarioMapper.toEntity(request)).thenReturn(veterinario);
        when(veterinarioRepository.save(veterinario)).thenReturn(veterinario);
        when(veterinarioMapper.toResponseDto(veterinario)).thenReturn(response);

        VeterinarioResponseDTO resultado = veterinarioService.createVeterinario(request);

        assertThat(resultado.getMatricula()).isEqualTo("MAT-123");
        verify(veterinarioRepository).save(veterinario);
    }

    @Test
    void createVeterinario_conMatriculaDuplicada_lanzaDuplicateResourceExceptionYNoGuarda() {
        VeterinarioRequestDTO request = crearRequest();
        when(veterinarioRepository.existsByMatricula("MAT-123")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> veterinarioService.createVeterinario(request));
        verify(veterinarioRepository, never()).save(any(Veterinario.class));
    }

    @Test
    void createVeterinario_conEmailDuplicado_lanzaDuplicateResourceExceptionYNoGuarda() {
        VeterinarioRequestDTO request = crearRequest();
        when(veterinarioRepository.existsByMatricula("MAT-123")).thenReturn(false);
        when(veterinarioRepository.existsByEmail("laura@mail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> veterinarioService.createVeterinario(request));
        verify(veterinarioRepository, never()).save(any(Veterinario.class));
    }

    @Test
    void createVeterinario_conCamposObligatoriosInvalidos_lanzaBadRequestExceptionYNoGuarda() {
        VeterinarioRequestDTO request = new VeterinarioRequestDTO("", "Suarez", "1122334455", null, "MAT-123", "Clinica");

        assertThrows(BadRequestException.class, () -> veterinarioService.createVeterinario(request));
        verify(veterinarioRepository, never()).save(any(Veterinario.class));
    }

    @Test
    void updateVeterinario_cuandoExiste_actualizaYGuarda() {
        Veterinario veterinario = crearVeterinario();
        VeterinarioRequestDTO request = new VeterinarioRequestDTO(
                "Laura",
                "Suarez",
                "1199999999",
                "laura2@mail.com",
                "MAT-123",
                "Cirugia");
        VeterinarioResponseDTO response = new VeterinarioResponseDTO(
                1L,
                "Laura",
                "Suarez",
                "1199999999",
                "laura2@mail.com",
                "MAT-123",
                "Cirugia");

        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(veterinarioRepository.existsByEmail("laura2@mail.com")).thenReturn(false);
        when(veterinarioRepository.save(veterinario)).thenReturn(veterinario);
        when(veterinarioMapper.toResponseDto(veterinario)).thenReturn(response);

        VeterinarioResponseDTO resultado = veterinarioService.updateVeterinario(1L, request);

        assertThat(resultado.getEspecialidad()).isEqualTo("Cirugia");
        verify(veterinarioRepository).save(veterinario);
    }

    @Test
    void updateVeterinario_cuandoNoExiste_lanzaResourceNotFoundExceptionYNoGuarda() {
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> veterinarioService.updateVeterinario(99L, crearRequest()));
        verify(veterinarioRepository, never()).save(any(Veterinario.class));
    }

    @Test
    void updateVeterinario_conMatriculaDuplicada_lanzaDuplicateResourceExceptionYNoGuarda() {
        Veterinario veterinario = crearVeterinario();
        VeterinarioRequestDTO request = new VeterinarioRequestDTO(
                "Laura", "Suarez", "1122334455", "laura@mail.com", "MAT-999", "Clinica");

        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(veterinarioRepository.existsByMatricula("MAT-999")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> veterinarioService.updateVeterinario(1L, request));
        verify(veterinarioRepository, never()).save(any(Veterinario.class));
    }

    @Test
    void getTurnosByVeterinario_retornaParticipacionesOrdenadasPorFechaYHora() {
        Veterinario veterinario = crearVeterinario();
        Participacion segunda = crearParticipacion(
                LocalDate.of(2026, 10, 2), LocalTime.of(9, 0), "Control");
        Participacion primera = crearParticipacion(
                LocalDate.of(2026, 10, 1), LocalTime.of(16, 0), "Vacuna");

        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(participacionRepository.findByVeterinarioId(1L)).thenReturn(List.of(segunda, primera));

        List<VeterinarioTurnoResponseDTO> resultado = veterinarioService.getTurnosByVeterinario(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getMotivo()).isEqualTo("Vacuna");
        assertThat(resultado.get(1).getMotivo()).isEqualTo("Control");
    }

    @Test
    void getTurnosByVeterinario_cuandoVeterinarioNoExiste_lanzaResourceNotFoundException() {
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> veterinarioService.getTurnosByVeterinario(99L));
    }

    @Test
    void deleteVeterinario_cuandoExiste_eliminaVeterinario() {
        Veterinario veterinario = crearVeterinario();
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));

        veterinarioService.deleteVeterinario(1L);

        verify(veterinarioRepository).delete(veterinario);
    }

    @Test
    void deleteVeterinario_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> veterinarioService.deleteVeterinario(99L));
        verify(veterinarioRepository, never()).delete(any(Veterinario.class));
    }

    private VeterinarioRequestDTO crearRequest() {
        return new VeterinarioRequestDTO(
                "Laura",
                "Suarez",
                "1122334455",
                "laura@mail.com",
                "MAT-123",
                "Clinica");
    }

    private Veterinario crearVeterinario() {
        return new Veterinario("Laura", "Suarez", "1122334455", "laura@mail.com", "MAT-123", "Clinica");
    }

    private VeterinarioResponseDTO crearResponse() {
        return new VeterinarioResponseDTO(1L, "Laura", "Suarez", "1122334455", "laura@mail.com", "MAT-123", "Clinica");
    }

    private Participacion crearParticipacion(LocalDate fecha, LocalTime hora, String motivo) {
        Mascota mascota = mock(Mascota.class);
        when(mascota.getNombre()).thenReturn("Luna");

        Turno turno = mock(Turno.class);
        when(turno.getId()).thenReturn(1L);
        when(turno.getFecha()).thenReturn(fecha);
        when(turno.getHora()).thenReturn(hora);
        when(turno.getMotivo()).thenReturn(motivo);
        when(turno.getMascota()).thenReturn(mascota);

        Participacion participacion = new Participacion();
        participacion.setTurno(turno);
        participacion.setRol(RolVeterinario.PRINCIPAL);
        return participacion;
    }
}
