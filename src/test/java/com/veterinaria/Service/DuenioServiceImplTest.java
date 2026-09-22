package com.veterinaria.Service;

import com.veterinaria.DTO.DuenioRequestDTO;
import com.veterinaria.DTO.DuenioResponseDTO;
import com.veterinaria.Entity.Duenio;
import com.veterinaria.Exception.DuplicateResourceException;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.DuenioMapper;
import com.veterinaria.Repository.DuenioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuenioServiceImplTest {

    @Mock
    private DuenioRepository duenioRepository;

    @Mock
    private DuenioMapper duenioMapper;

    @InjectMocks
    private DuenioServiceImpl duenioService;

    @Test
    void getAllDuenios_cuandoNoHayDatos_retornaListaVacia() {
        when(duenioRepository.findAll()).thenReturn(List.of());
        when(duenioMapper.toResponseDtoList(List.of())).thenReturn(List.of());

        List<DuenioResponseDTO> resultado = duenioService.getAllDuenios();

        assertThat(resultado).isEmpty();
    }

    @Test
    void getAllDuenios_cuandoHayDatos_retornaListaConDuenios() {
        Duenio duenio = crearDuenio();
        DuenioResponseDTO response = crearResponse();
        when(duenioRepository.findAll()).thenReturn(List.of(duenio));
        when(duenioMapper.toResponseDtoList(List.of(duenio))).thenReturn(List.of(response));

        List<DuenioResponseDTO> resultado = duenioService.getAllDuenios();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getNombre()).isEqualTo("Ana");
    }

    @Test
    void getDuenioById_cuandoExiste_retornaDuenio() {
        Duenio duenio = crearDuenio();
        DuenioResponseDTO response = crearResponse();
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(duenio));
        when(duenioMapper.toResponseDto(duenio)).thenReturn(response);

        DuenioResponseDTO resultado = duenioService.getDuenioById(1L);

        assertThat(resultado.getNombre()).isEqualTo("Ana");
    }

    @Test
    void getDuenioById_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(duenioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> duenioService.getDuenioById(99L));
        verify(duenioRepository, never()).save(any(Duenio.class));
    }

    @Test
    void getDuenioByCedula_cuandoExiste_retornaDuenio() {
        Duenio duenio = crearDuenio();
        DuenioResponseDTO response = crearResponse();
        when(duenioRepository.findByCedula("12345678")).thenReturn(Optional.of(duenio));
        when(duenioMapper.toResponseDto(duenio)).thenReturn(response);

        DuenioResponseDTO resultado = duenioService.getDuenioByCedula("12345678");

        assertThat(resultado.getCedula()).isEqualTo("12345678");
    }

    @Test
    void getDuenioByCedula_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(duenioRepository.findByCedula("99999999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> duenioService.getDuenioByCedula("99999999"));
    }

    @Test
    void searchDuenios_retornaCoincidencias() {
        Duenio duenio = crearDuenio();
        DuenioResponseDTO response = crearResponse();
        when(duenioRepository.findByNombreAndApellido("Ana", "Gomez")).thenReturn(List.of(duenio));
        when(duenioMapper.toResponseDtoList(List.of(duenio))).thenReturn(List.of(response));

        List<DuenioResponseDTO> resultado = duenioService.searchDuenios("Ana", "Gomez");

        assertThat(resultado).containsExactly(response);
    }

    @Test
    void searchDuenios_sinCoincidencias_retornaListaVacia() {
        when(duenioRepository.findByNombreAndApellido("No", "Existe")).thenReturn(List.of());
        when(duenioMapper.toResponseDtoList(List.of())).thenReturn(List.of());

        List<DuenioResponseDTO> resultado = duenioService.searchDuenios("No", "Existe");

        assertThat(resultado).isEmpty();
    }

    @Test
    void getDuenioByEmail_cuandoExiste_retornaDuenio() {
        Duenio duenio = crearDuenio();
        DuenioResponseDTO response = crearResponse();
        when(duenioRepository.findByEmail("ana@mail.com")).thenReturn(Optional.of(duenio));
        when(duenioMapper.toResponseDto(duenio)).thenReturn(response);

        DuenioResponseDTO resultado = duenioService.getDuenioByEmail("ana@mail.com");

        assertThat(resultado.getEmail()).isEqualTo("ana@mail.com");
    }

    @Test
    void getDuenioByEmail_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(duenioRepository.findByEmail("nadie@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> duenioService.getDuenioByEmail("nadie@mail.com"));
    }

    @Test
    void createDuenio_cuandoEsValido_guardaDuenio() {
        DuenioRequestDTO request = crearRequest("ana@mail.com");
        Duenio duenio = crearDuenio();
        DuenioResponseDTO response = crearResponse();
        when(duenioMapper.toEntity(request)).thenReturn(duenio);
        when(duenioRepository.existsByCedula("12345678")).thenReturn(false);
        when(duenioRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(duenioRepository.save(duenio)).thenReturn(duenio);
        when(duenioMapper.toResponseDto(duenio)).thenReturn(response);

        DuenioResponseDTO resultado = duenioService.createDuenio(request);

        assertThat(resultado.getCedula()).isEqualTo("12345678");
        verify(duenioRepository).save(duenio);
    }

    @Test
    void createDuenio_cuandoCedulaDuplicada_lanzaDuplicateResourceExceptionYNoGuarda() {
        DuenioRequestDTO request = crearRequest("ana@mail.com");
        Duenio duenio = crearDuenio();
        when(duenioMapper.toEntity(request)).thenReturn(duenio);
        when(duenioRepository.existsByCedula("12345678")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> duenioService.createDuenio(request));
        verify(duenioRepository, never()).save(any(Duenio.class));
    }

    @Test
    void createDuenio_cuandoEmailDuplicado_lanzaDuplicateResourceExceptionYNoGuarda() {
        DuenioRequestDTO request = crearRequest("ana@mail.com");
        Duenio duenio = crearDuenio();
        when(duenioMapper.toEntity(request)).thenReturn(duenio);
        when(duenioRepository.existsByCedula("12345678")).thenReturn(false);
        when(duenioRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> duenioService.createDuenio(request));
        verify(duenioRepository, never()).save(any(Duenio.class));
    }

    @Test
    void createDuenio_conEmailEnBlanco_normalizaEmailANull() {
        DuenioRequestDTO request = crearRequest("");
        Duenio duenio = new Duenio("Ana", "Gomez", "1122334455", null, "12345678");
        DuenioResponseDTO response = new DuenioResponseDTO(1L, "Ana", "Gomez", "12345678", "1122334455", null);
        when(duenioMapper.toEntity(request)).thenReturn(duenio);
        when(duenioRepository.existsByCedula("12345678")).thenReturn(false);
        when(duenioRepository.save(duenio)).thenReturn(duenio);
        when(duenioMapper.toResponseDto(duenio)).thenReturn(response);

        DuenioResponseDTO resultado = duenioService.createDuenio(request);

        assertThat(request.getEmail()).isNull();
        assertThat(resultado.getEmail()).isNull();
        verify(duenioRepository, never()).existsByEmail(anyString());
        verify(duenioRepository).save(duenio);
    }

    @Test
    void updateDuenio_cuandoExiste_actualizaYGuarda() {
        Duenio existente = crearDuenio();
        Duenio actualizado = new Duenio("Ana Maria", "Gomez", "1199999999", "ana2@mail.com", "12345678");
        DuenioRequestDTO request = new DuenioRequestDTO("Ana Maria", "Gomez", "12345678", "1199999999", "ana2@mail.com");
        DuenioResponseDTO response = new DuenioResponseDTO(1L, "Ana Maria", "Gomez", "12345678", "1199999999", "ana2@mail.com");

        when(duenioMapper.toEntity(request)).thenReturn(actualizado);
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(duenioRepository.existsByEmail("ana2@mail.com")).thenReturn(false);
        when(duenioRepository.save(existente)).thenReturn(existente);
        when(duenioMapper.toResponseDto(existente)).thenReturn(response);

        DuenioResponseDTO resultado = duenioService.updateDuenio(1L, request);

        assertThat(resultado.getNombre()).isEqualTo("Ana Maria");
        assertThat(resultado.getEmail()).isEqualTo("ana2@mail.com");
        verify(duenioRepository).save(existente);
    }

    @Test
    void updateDuenio_cuandoNoExiste_lanzaResourceNotFoundExceptionYNoGuarda() {
        DuenioRequestDTO request = crearRequest("ana@mail.com");
        Duenio duenio = crearDuenio();
        when(duenioMapper.toEntity(request)).thenReturn(duenio);
        when(duenioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> duenioService.updateDuenio(99L, request));
        verify(duenioRepository, never()).save(any(Duenio.class));
    }

    @Test
    void updateDuenio_conCedulaDuplicada_lanzaDuplicateResourceExceptionYNoGuarda() {
        Duenio existente = crearDuenio();
        Duenio actualizado = new Duenio("Ana", "Gomez", "1122334455", "ana@mail.com", "87654321");
        DuenioRequestDTO request = new DuenioRequestDTO("Ana", "Gomez", "87654321", "1122334455", "ana@mail.com");

        when(duenioMapper.toEntity(request)).thenReturn(actualizado);
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(duenioRepository.existsByCedula("87654321")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> duenioService.updateDuenio(1L, request));
        verify(duenioRepository, never()).save(any(Duenio.class));
    }

    @Test
    void updateDuenio_conEmailEnBlanco_normalizaEmailANull() {
        Duenio existente = crearDuenio();
        Duenio actualizado = new Duenio("Ana", "Gomez", "1122334455", null, "12345678");
        DuenioRequestDTO request = crearRequest("   ");
        DuenioResponseDTO response = new DuenioResponseDTO(1L, "Ana", "Gomez", "12345678", "1122334455", null);
        when(duenioMapper.toEntity(request)).thenReturn(actualizado);
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(duenioRepository.save(existente)).thenReturn(existente);
        when(duenioMapper.toResponseDto(existente)).thenReturn(response);

        DuenioResponseDTO resultado = duenioService.updateDuenio(1L, request);

        assertThat(request.getEmail()).isNull();
        assertThat(resultado.getEmail()).isNull();
        verify(duenioRepository, never()).existsByEmail(anyString());
        verify(duenioRepository).save(existente);
    }

    @Test
    void deleteDuenio_cuandoExiste_eliminaDuenio() {
        Duenio duenio = crearDuenio();
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(duenio));

        duenioService.deleteDuenio(1L);

        verify(duenioRepository).delete(duenio);
    }

    @Test
    void deleteDuenio_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(duenioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> duenioService.deleteDuenio(99L));
        verify(duenioRepository, never()).delete(any(Duenio.class));
    }

    private Duenio crearDuenio() {
        return new Duenio("Ana", "Gomez", "1122334455", "ana@mail.com", "12345678");
    }

    private DuenioRequestDTO crearRequest(String email) {
        return new DuenioRequestDTO("Ana", "Gomez", "12345678", "1122334455", email);
    }

    private DuenioResponseDTO crearResponse() {
        return new DuenioResponseDTO(1L, "Ana", "Gomez", "12345678", "1122334455", "ana@mail.com");
    }
}
