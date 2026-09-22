package com.veterinaria.Service;

import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.DTO.MascotaResponseDTO;
import com.veterinaria.Entity.Duenio;
import com.veterinaria.Entity.Mascota;
import com.veterinaria.Entity.enums.SexoMascota;
import com.veterinaria.Exception.BadRequestException;
import com.veterinaria.Exception.CupoMascotasExcedidoException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.MascotaMapper;
import com.veterinaria.Repository.DuenioRepository;
import com.veterinaria.Repository.MascotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private DuenioRepository duenioRepository;

    @Mock
    private MascotaMapper mascotaMapper;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    @Test
    void getAllMascotas_retornaListaConMascotas() {
        Mascota mascota = crearMascota();
        MascotaResponseDTO response = crearResponse();
        when(mascotaRepository.findAll()).thenReturn(List.of(mascota));
        when(mascotaMapper.toResponseDtoList(List.of(mascota))).thenReturn(List.of(response));

        List<MascotaResponseDTO> resultado = mascotaService.getAllMascotas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getNombre()).isEqualTo("Luna");
    }

    @Test
    void getAllMascotas_sinDatos_retornaListaVacia() {
        when(mascotaRepository.findAll()).thenReturn(List.of());
        when(mascotaMapper.toResponseDtoList(List.of())).thenReturn(List.of());

        List<MascotaResponseDTO> resultado = mascotaService.getAllMascotas();

        assertThat(resultado).isEmpty();
    }

    @Test
    void getMascotaById_cuandoExiste_retornaMascota() {
        Mascota mascota = crearMascota();
        MascotaResponseDTO response = crearResponse();
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(mascotaMapper.toResponseDto(mascota)).thenReturn(response);

        MascotaResponseDTO resultado = mascotaService.getMascotaById(1L);

        assertThat(resultado.getNombre()).isEqualTo("Luna");
    }

    @Test
    void getMascotaById_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mascotaService.getMascotaById(99L));
    }

    @Test
    void createMascota_cuandoDuenioExiste_guardaMascota() {
        Duenio duenio = new Duenio("Ana", "Gomez", "1122334455", null, "12345678");
        MascotaRequestDTO request = crearRequest(null);
        Mascota mascota = crearMascota();
        MascotaResponseDTO response = crearResponse();
        when(mascotaMapper.toEntity(request)).thenReturn(mascota);
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(duenio));
        when(mascotaRepository.save(mascota)).thenReturn(mascota);
        when(mascotaMapper.toResponseDto(mascota)).thenReturn(response);

        MascotaResponseDTO resultado = mascotaService.createMascota(1L, request);

        assertThat(resultado.getNombre()).isEqualTo("Luna");
        assertThat(mascota.getDuenio()).isSameAs(duenio);
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void createMascota_sinDuenioId_lanzaBadRequestExceptionYNoGuarda() {
        MascotaRequestDTO request = crearRequest(null);
        when(mascotaMapper.toEntity(request)).thenReturn(crearMascota());

        assertThrows(BadRequestException.class, () -> mascotaService.createMascota(null, request));
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void createMascota_conDuenioInexistente_lanzaResourceNotFoundExceptionYNoGuarda() {
        MascotaRequestDTO request = crearRequest(null);
        Mascota mascota = crearMascota();
        when(mascotaMapper.toEntity(request)).thenReturn(mascota);
        when(duenioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mascotaService.createMascota(99L, request));
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void createMascota_conDuenioEnLimiteDeMascotas_lanzaCupoMascotasExcedidoExceptionYNoGuarda() {
        Duenio duenio = new Duenio("Ana", "Gomez", "1122334455", null, "12345678");
        MascotaRequestDTO request = crearRequest(null);
        when(mascotaMapper.toEntity(request)).thenReturn(crearMascota());
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(duenio));
        when(mascotaRepository.countByDuenioId(1L)).thenReturn(5L);

        CupoMascotasExcedidoException exception =
                assertThrows(CupoMascotasExcedidoException.class, () -> mascotaService.createMascota(1L, request));
        assertThat(exception.getMessage())
                .contains("duenio con id 1")
                .contains("limite es de 5");
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void createMascota_conDuenioDebajoDelLimite_guardaMascota() {
        Duenio duenio = new Duenio("Ana", "Gomez", "1122334455", null, "12345678");
        MascotaRequestDTO request = crearRequest(null);
        Mascota mascota = crearMascota();
        when(mascotaMapper.toEntity(request)).thenReturn(mascota);
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(duenio));
        when(mascotaRepository.countByDuenioId(1L)).thenReturn(4L);
        when(mascotaRepository.save(mascota)).thenReturn(mascota);
        when(mascotaMapper.toResponseDto(mascota)).thenReturn(crearResponse());

        mascotaService.createMascota(1L, request);

        verify(mascotaRepository).save(mascota);
    }

    @Test
    void getMascotasByDuenioId_cuandoDuenioExiste_retornaMascotas() {
        Duenio duenio = new Duenio("Ana", "Gomez", "1122334455", null, "12345678");
        Mascota mascota = crearMascota();
        MascotaResponseDTO response = crearResponse();
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(duenio));
        when(mascotaRepository.findByDuenioId(1L)).thenReturn(List.of(mascota));
        when(mascotaMapper.toResponseDtoList(List.of(mascota))).thenReturn(List.of(response));

        List<MascotaResponseDTO> resultado = mascotaService.getMascotasByDuenioId(1L);

        assertThat(resultado).containsExactly(response);
    }

    @Test
    void getMascotasByDuenioId_cuandoDuenioNoExiste_lanzaResourceNotFoundException() {
        when(duenioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mascotaService.getMascotasByDuenioId(99L));
    }

    @Test
    void updateMascota_cuandoDuenioNuevoExiste_actualizaYGuarda() {
        Mascota mascota = crearMascota();
        Duenio duenioNuevo = new Duenio("Carlos", "Diaz", "1155555555", null, "87654321");
        MascotaRequestDTO request = crearRequest(2L);
        MascotaResponseDTO response = new MascotaResponseDTO(
                1L, "Milo", "Gato", "Siames", "Gris", SexoMascota.MACHO, LocalDate.of(2021, 5, 20), 2L, "Carlos");

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(duenioRepository.findById(2L)).thenReturn(Optional.of(duenioNuevo));
        when(mascotaRepository.save(mascota)).thenReturn(mascota);
        when(mascotaMapper.toResponseDto(mascota)).thenReturn(response);

        MascotaResponseDTO resultado = mascotaService.updateMascota(1L, request);

        assertThat(resultado.getNombre()).isEqualTo("Milo");
        assertThat(mascota.getDuenio()).isSameAs(duenioNuevo);
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void updateMascota_cambiandoADuenioEnLimite_lanzaCupoMascotasExcedidoExceptionYNoGuarda() {
        Mascota mascota = crearMascota();
        mascota.setDuenio(crearDuenioConId(1L));
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(duenioRepository.findById(2L)).thenReturn(Optional.of(crearDuenioConId(2L)));
        when(mascotaRepository.countByDuenioId(2L)).thenReturn(5L);

        CupoMascotasExcedidoException exception =
                assertThrows(CupoMascotasExcedidoException.class, () -> mascotaService.updateMascota(1L, crearRequest(2L)));
        assertThat(exception.getMessage()).contains("duenio con id 2");
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void updateMascota_mismoDuenioEnLimite_noValidaCupoYGuarda() {
        Duenio duenio = crearDuenioConId(1L);
        Mascota mascota = crearMascota();
        mascota.setDuenio(duenio);
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(duenio));
        when(mascotaRepository.save(mascota)).thenReturn(mascota);
        when(mascotaMapper.toResponseDto(mascota)).thenReturn(crearResponse());

        mascotaService.updateMascota(1L, crearRequest(1L));

        verify(mascotaRepository, never()).countByDuenioId(any());
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void updateMascota_cuandoMascotaNoExiste_lanzaResourceNotFoundExceptionYNoGuarda() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mascotaService.updateMascota(99L, crearRequest(1L)));
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void updateMascota_conDuenioNuevoInexistente_lanzaResourceNotFoundExceptionYNoGuarda() {
        Mascota mascota = crearMascota();
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(duenioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mascotaService.updateMascota(1L, crearRequest(99L)));
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    void deleteMascota_cuandoExiste_eliminaMascota() {
        Mascota mascota = crearMascota();
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));

        mascotaService.deleteMascota(1L);

        verify(mascotaRepository).delete(mascota);
    }

    @Test
    void deleteMascota_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mascotaService.deleteMascota(99L));
        verify(mascotaRepository, never()).delete(any(Mascota.class));
    }

    private Mascota crearMascota() {
        Mascota mascota = new Mascota();
        mascota.setNombre("Luna");
        mascota.setEspecie("Perro");
        mascota.setRaza("Mestiza");
        mascota.setColor("Negro");
        mascota.setSexo(SexoMascota.HEMBRA);
        mascota.setFechaNacimiento(LocalDate.of(2020, 1, 10));
        return mascota;
    }

    private Duenio crearDuenioConId(Long id) {
        Duenio duenio = new Duenio("Ana", "Gomez", "1122334455", null, "12345678");
        ReflectionTestUtils.setField(duenio, "id", id);
        return duenio;
    }

    private MascotaRequestDTO crearRequest(Long duenioId) {
        return new MascotaRequestDTO(
                "Milo",
                "Gato",
                "Siames",
                "Gris",
                SexoMascota.MACHO,
                LocalDate.of(2021, 5, 20),
                duenioId);
    }

    private MascotaResponseDTO crearResponse() {
        return new MascotaResponseDTO(
                1L,
                "Luna",
                "Perro",
                "Mestiza",
                "Negro",
                SexoMascota.HEMBRA,
                LocalDate.of(2020, 1, 10),
                1L,
                "Ana");
    }
}
