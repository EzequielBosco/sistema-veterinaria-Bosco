package com.veterinaria.Controller;

import com.veterinaria.DTO.MascotaRequestDTO;
import com.veterinaria.DTO.MascotaResponseDTO;
import com.veterinaria.Entity.Mascota;
import com.veterinaria.Entity.enums.SexoMascota;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Mapper.MascotaMapper;
import com.veterinaria.Service.MascotaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MascotaController.class)
class MascotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MascotaService mascotaService;

    @MockitoBean
    private MascotaMapper mascotaMapper;

    @Test
    void getAllMascotas_retornaHttp200ConListaVacia() throws Exception {
        when(mascotaService.getAllMascotas()).thenReturn(List.of());
        when(mascotaMapper.toResponseDtoList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/api/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getMascotaById_cuandoExiste_retornaHttp200ConMascota() throws Exception {
        Mascota mascota = new Mascota();
        MascotaResponseDTO response = crearResponse();
        when(mascotaService.getMascotaById(1L)).thenReturn(mascota);
        when(mascotaMapper.toResponseDto(mascota)).thenReturn(response);

        mockMvc.perform(get("/api/mascotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Luna"));
    }

    @Test
    void getMascotaById_cuandoNoExiste_retornaHttp404() throws Exception {
        when(mascotaService.getMascotaById(99L))
                .thenThrow(new ResourceNotFoundException("No existe una mascota con id 99"));

        mockMvc.perform(get("/api/mascotas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMascota_conBodyValido_retornaHttp200() throws Exception {
        Mascota mascota = new Mascota();
        MascotaResponseDTO response = crearResponse();
        when(mascotaService.updateMascota(org.mockito.ArgumentMatchers.eq(1L), any(MascotaRequestDTO.class)))
                .thenReturn(mascota);
        when(mascotaMapper.toResponseDto(mascota)).thenReturn(response);

        mockMvc.perform(put("/api/mascotas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Luna",
                                  "especie": "Perro",
                                  "raza": "Mestiza",
                                  "color": "Negro",
                                  "sexo": "HEMBRA",
                                  "fechaNacimiento": "2020-01-10",
                                  "duenioId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Luna"));
    }

    @Test
    void updateMascota_conNombreVacio_retornaHttp400() throws Exception {
        mockMvc.perform(put("/api/mascotas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "",
                                  "especie": "Perro",
                                  "sexo": "HEMBRA",
                                  "fechaNacimiento": "2020-01-10",
                                  "duenioId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMascota_cuandoNoExiste_retornaHttp404() throws Exception {
        when(mascotaService.updateMascota(org.mockito.ArgumentMatchers.eq(99L), any(MascotaRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("No existe una mascota con id 99"));

        mockMvc.perform(put("/api/mascotas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Luna",
                                  "especie": "Perro",
                                  "sexo": "HEMBRA",
                                  "fechaNacimiento": "2020-01-10",
                                  "duenioId": 1
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMascota_retornaHttp204() throws Exception {
        doNothing().when(mascotaService).deleteMascota(1L);

        mockMvc.perform(delete("/api/mascotas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMascota_cuandoNoExiste_retornaHttp404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("No existe una mascota con id 99"))
                .when(mascotaService).deleteMascota(99L);

        mockMvc.perform(delete("/api/mascotas/99"))
                .andExpect(status().isNotFound());
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
