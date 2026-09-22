package com.veterinaria.Controller;

import com.veterinaria.DTO.TurnoResponseDTO;
import com.veterinaria.DTO.TurnoVeterinarioResponseDTO;
import com.veterinaria.Entity.enums.EstadoTurno;
import com.veterinaria.Entity.enums.RolVeterinario;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Service.TurnoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
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

@WebMvcTest(TurnoController.class)
class TurnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TurnoService turnoService;

    @Test
    void getTurnos_retornaHttp200ConListaVacia() throws Exception {
        when(turnoService.getAllTurnos()).thenReturn(List.of());

        mockMvc.perform(get("/api/turnos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAgenda_conParametros_retornaHttp200() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);
        TurnoResponseDTO response = crearResponse();
        when(turnoService.getAgenda(1L, fecha)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/turnos")
                        .param("veterinarioId", "1")
                        .param("fecha", fecha.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].motivo").value("Consulta general"));
    }

    @Test
    void getTurnoById_cuandoNoExiste_retornaHttp404() throws Exception {
        when(turnoService.getTurnoById(99L)).thenThrow(new ResourceNotFoundException("No existe un turno con id 99"));

        mockMvc.perform(get("/api/turnos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTurnoById_cuandoExiste_retornaHttp200() throws Exception {
        when(turnoService.getTurnoById(1L)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/turnos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo").value("Consulta general"));
    }

    @Test
    void getVeterinariosByTurno_retornaHttp200() throws Exception {
        TurnoVeterinarioResponseDTO response =
                new TurnoVeterinarioResponseDTO(1L, "Laura Suarez", RolVeterinario.PRINCIPAL);
        when(turnoService.getVeterinariosByTurno(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/turnos/1/veterinarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreApellido").value("Laura Suarez"));
    }

    @Test
    void getVeterinariosByTurno_cuandoNoExiste_retornaHttp404() throws Exception {
        when(turnoService.getVeterinariosByTurno(99L))
                .thenThrow(new ResourceNotFoundException("No existe un turno con id 99"));

        mockMvc.perform(get("/api/turnos/99/veterinarios"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTurno_conBodyValido_retornaHttp201() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);
        TurnoResponseDTO response = crearResponse();
        when(turnoService.createTurno(any())).thenReturn(response);

        mockMvc.perform(post("/api/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fecha": "%s",
                                  "hora": "10:00:00",
                                  "motivo": "Consulta general",
                                  "duracionMinutos": 30,
                                  "mascotaId": 1,
                                  "veterinarios": [
                                    { "veterinarioId": 1, "rol": "PRINCIPAL" }
                                  ]
                                }
                                """.formatted(fecha)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.motivo").value("Consulta general"));
    }

    @Test
    void createTurno_sinMotivo_retornaHttp400() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);
        mockMvc.perform(post("/api/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fecha": "%s",
                                  "hora": "10:00:00",
                                  "motivo": "",
                                  "duracionMinutos": 30,
                                  "mascotaId": 1,
                                  "veterinarios": [
                                    { "veterinarioId": 1, "rol": "PRINCIPAL" }
                                  ]
                                }
                                """.formatted(fecha)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTurno_conBodyValido_retornaHttp200() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);
        when(turnoService.updateTurno(org.mockito.ArgumentMatchers.eq(1L), any())).thenReturn(crearResponse());

        mockMvc.perform(put("/api/turnos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fecha": "%s",
                                  "hora": "10:00:00",
                                  "motivo": "Consulta general",
                                  "duracionMinutos": 30,
                                  "mascotaId": 1,
                                  "veterinarios": [
                                    { "veterinarioId": 1, "rol": "PRINCIPAL" }
                                  ]
                                }
                                """.formatted(fecha)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo").value("Consulta general"));
    }

    @Test
    void updateTurno_conBodyInvalido_retornaHttp400() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);
        mockMvc.perform(put("/api/turnos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fecha": "%s",
                                  "hora": "10:00:00",
                                  "motivo": "",
                                  "duracionMinutos": 30,
                                  "mascotaId": 1,
                                  "veterinarios": [
                                    { "veterinarioId": 1, "rol": "PRINCIPAL" }
                                  ]
                                }
                                """.formatted(fecha)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTurno_retornaHttp204() throws Exception {
        doNothing().when(turnoService).deleteTurno(1L);

        mockMvc.perform(delete("/api/turnos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTurno_cuandoNoExiste_retornaHttp404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("No existe un turno con id 99"))
                .when(turnoService).deleteTurno(99L);

        mockMvc.perform(delete("/api/turnos/99"))
                .andExpect(status().isNotFound());
    }

    private TurnoResponseDTO crearResponse() {
        return new TurnoResponseDTO(
                1L,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                "Consulta general",
                30,
                EstadoTurno.PENDIENTE,
                10L,
                "Luna",
                List.of(new TurnoVeterinarioResponseDTO(1L, "Laura Suarez", RolVeterinario.PRINCIPAL)));
    }
}
