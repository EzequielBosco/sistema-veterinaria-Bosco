package com.veterinaria.Controller;

import com.veterinaria.DTO.DuenioRequestDTO;
import com.veterinaria.DTO.DuenioResponseDTO;
import com.veterinaria.DTO.MascotaResponseDTO;
import com.veterinaria.Entity.Duenio;
import com.veterinaria.Entity.Mascota;
import com.veterinaria.Entity.enums.SexoMascota;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.DuenioMapper;
import com.veterinaria.Mapper.MascotaMapper;
import com.veterinaria.Service.DuenioService;
import com.veterinaria.Service.MascotaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DuenioController.class)
class DuenioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DuenioService duenioService;

    @MockitoBean
    private MascotaService mascotaService;

    @MockitoBean
    private DuenioMapper duenioMapper;

    @MockitoBean
    private MascotaMapper mascotaMapper;

    @Test
    void getAllDuenios_retornaHttp200ConListaVacia() throws Exception {
        when(duenioService.getAllDuenios()).thenReturn(List.of());
        when(duenioMapper.toResponseDtoList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/api/duenios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getDuenioById_cuandoExiste_retornaHttp200ConDuenio() throws Exception {
        Duenio duenio = crearDuenio();
        DuenioResponseDTO response = crearResponse();
        when(duenioService.getDuenioById(1L)).thenReturn(duenio);
        when(duenioMapper.toResponseDto(duenio)).thenReturn(response);

        mockMvc.perform(get("/api/duenios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void getDuenioById_cuandoNoExiste_retornaHttp404() throws Exception {
        when(duenioService.getDuenioById(99L))
                .thenThrow(new ResourceNotFoundException("No existe un duenio con id 99"));

        mockMvc.perform(get("/api/duenios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createDuenio_conBodyValido_retornaHttp201() throws Exception {
        Duenio duenio = crearDuenio();
        DuenioResponseDTO response = crearResponse();
        when(duenioMapper.toEntity(any(DuenioRequestDTO.class))).thenReturn(duenio);
        when(duenioService.createDuenio(duenio)).thenReturn(duenio);
        when(duenioMapper.toResponseDto(duenio)).thenReturn(response);

        mockMvc.perform(post("/api/duenios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana",
                                  "apellido": "Gomez",
                                  "cedula": "12345678",
                                  "telefono": "1122334455",
                                  "email": "ana@mail.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void createDuenio_conEmailInvalido_retornaHttp400() throws Exception {
        mockMvc.perform(post("/api/duenios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana",
                                  "apellido": "Gomez",
                                  "cedula": "12345678",
                                  "telefono": "1122334455",
                                  "email": "email-invalido"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDuenioByCedula_cuandoExiste_retornaHttp200() throws Exception {
        Duenio duenio = crearDuenio();
        when(duenioService.getDuenioByCedula("12345678")).thenReturn(duenio);
        when(duenioMapper.toResponseDto(duenio)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/duenios/cedula/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula").value("12345678"));
    }

    @Test
    void getDuenioByCedula_cuandoNoExiste_retornaHttp404() throws Exception {
        when(duenioService.getDuenioByCedula("99999999"))
                .thenThrow(new ResourceNotFoundException("No existe un duenio con cedula/DNI 99999999"));

        mockMvc.perform(get("/api/duenios/cedula/99999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchDuenios_retornaHttp200() throws Exception {
        Duenio duenio = crearDuenio();
        when(duenioService.searchDuenios("Ana", "Gomez")).thenReturn(List.of(duenio));
        when(duenioMapper.toResponseDtoList(List.of(duenio))).thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/duenios/search")
                        .param("nombre", "Ana")
                        .param("apellido", "Gomez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Ana"));
    }

    @Test
    void getDuenioByEmail_cuandoExiste_retornaHttp200() throws Exception {
        Duenio duenio = crearDuenio();
        when(duenioService.getDuenioByEmail("ana@mail.com")).thenReturn(duenio);
        when(duenioMapper.toResponseDto(duenio)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/duenios/email/ana@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@mail.com"));
    }

    @Test
    void getDuenioByEmail_cuandoNoExiste_retornaHttp404() throws Exception {
        when(duenioService.getDuenioByEmail("nadie@mail.com"))
                .thenThrow(new ResourceNotFoundException("No existe un duenio con email nadie@mail.com"));

        mockMvc.perform(get("/api/duenios/email/nadie@mail.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDuenio_conBodyValido_retornaHttp200() throws Exception {
        Duenio duenio = crearDuenio();
        when(duenioMapper.toEntity(any(DuenioRequestDTO.class))).thenReturn(duenio);
        when(duenioService.updateDuenio(1L, duenio)).thenReturn(duenio);
        when(duenioMapper.toResponseDto(duenio)).thenReturn(crearResponse());

        mockMvc.perform(put("/api/duenios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana",
                                  "apellido": "Gomez",
                                  "cedula": "12345678",
                                  "telefono": "1122334455",
                                  "email": "ana@mail.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void updateDuenio_conNombreVacio_retornaHttp400() throws Exception {
        mockMvc.perform(put("/api/duenios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "",
                                  "apellido": "Gomez",
                                  "cedula": "12345678",
                                  "telefono": "1122334455",
                                  "email": "ana@mail.com"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteDuenio_retornaHttp204() throws Exception {
        doNothing().when(duenioService).deleteDuenio(1L);

        mockMvc.perform(delete("/api/duenios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDuenio_cuandoNoExiste_retornaHttp404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("No existe un duenio con id 99"))
                .when(duenioService).deleteDuenio(99L);

        mockMvc.perform(delete("/api/duenios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMascotasByDuenioId_retornaHttp200() throws Exception {
        Mascota mascota = new Mascota();
        MascotaResponseDTO response = new MascotaResponseDTO(
                1L, "Luna", "Perro", null, null, SexoMascota.HEMBRA, null, 1L, "Ana");
        when(mascotaService.getMascotasByDuenioId(1L)).thenReturn(List.of(mascota));
        when(mascotaMapper.toResponseDtoList(List.of(mascota))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/duenios/1/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Luna"));
    }

    private Duenio crearDuenio() {
        return new Duenio("Ana", "Gomez", "1122334455", "ana@mail.com", "12345678");
    }

    private DuenioResponseDTO crearResponse() {
        return new DuenioResponseDTO(1L, "Ana", "Gomez", "12345678", "1122334455", "ana@mail.com");
    }
}
