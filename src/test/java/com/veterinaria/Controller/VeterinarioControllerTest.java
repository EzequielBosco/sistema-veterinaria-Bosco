package com.veterinaria.Controller;

import com.veterinaria.DTO.VeterinarioResponseDTO;
import com.veterinaria.DTO.VeterinarioTurnoResponseDTO;
import com.veterinaria.Entity.enums.RolVeterinario;
import com.veterinaria.Exception.ResourceNotFoundException;
import com.veterinaria.Service.VeterinarioService;
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

@WebMvcTest(VeterinarioController.class)
class VeterinarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VeterinarioService veterinarioService;

    @Test
    void getAllVeterinarios_retornaHttp200ConListaVacia() throws Exception {
        when(veterinarioService.getAllVeterinarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/veterinarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getVeterinarioById_cuandoExiste_retornaHttp200() throws Exception {
        when(veterinarioService.getVeterinarioById(1L)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/veterinarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laura"));
    }

    @Test
    void getVeterinarioById_cuandoNoExiste_retornaHttp404() throws Exception {
        when(veterinarioService.getVeterinarioById(99L))
                .thenThrow(new ResourceNotFoundException("No existe un veterinario con id 99"));

        mockMvc.perform(get("/api/veterinarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTurnosByVeterinario_retornaHttp200() throws Exception {
        VeterinarioTurnoResponseDTO turno = new VeterinarioTurnoResponseDTO(
                1L,
                LocalDate.of(2026, 10, 1),
                LocalTime.of(10, 0),
                "Consulta general",
                "Luna",
                RolVeterinario.PRINCIPAL);
        when(veterinarioService.getTurnosByVeterinario(1L)).thenReturn(List.of(turno));

        mockMvc.perform(get("/api/veterinarios/1/turnos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mascotaNombre").value("Luna"));
    }

    @Test
    void getTurnosByVeterinario_cuandoNoExiste_retornaHttp404() throws Exception {
        when(veterinarioService.getTurnosByVeterinario(99L))
                .thenThrow(new ResourceNotFoundException("No existe un veterinario con id 99"));

        mockMvc.perform(get("/api/veterinarios/99/turnos"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createVeterinario_conBodyValido_retornaHttp201() throws Exception {
        when(veterinarioService.createVeterinario(any())).thenReturn(crearResponse());

        mockMvc.perform(post("/api/veterinarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laura",
                                  "apellido": "Suarez",
                                  "telefono": "1122334455",
                                  "email": "laura@mail.com",
                                  "matricula": "MAT-123",
                                  "especialidad": "Clinica"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("MAT-123"));
    }

    @Test
    void createVeterinario_conMatriculaVacia_retornaHttp400() throws Exception {
        mockMvc.perform(post("/api/veterinarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laura",
                                  "apellido": "Suarez",
                                  "telefono": "1122334455",
                                  "email": "laura@mail.com",
                                  "matricula": "",
                                  "especialidad": "Clinica"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateVeterinario_conBodyValido_retornaHttp200() throws Exception {
        when(veterinarioService.updateVeterinario(org.mockito.ArgumentMatchers.eq(1L), any()))
                .thenReturn(crearResponse());

        mockMvc.perform(put("/api/veterinarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laura",
                                  "apellido": "Suarez",
                                  "telefono": "1122334455",
                                  "email": "laura@mail.com",
                                  "matricula": "MAT-123",
                                  "especialidad": "Clinica"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laura"));
    }

    @Test
    void updateVeterinario_conBodyInvalido_retornaHttp400() throws Exception {
        mockMvc.perform(put("/api/veterinarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laura",
                                  "apellido": "Suarez",
                                  "telefono": "1122334455",
                                  "email": "laura@mail.com",
                                  "matricula": "",
                                  "especialidad": "Clinica"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVeterinario_retornaHttp204() throws Exception {
        doNothing().when(veterinarioService).deleteVeterinario(1L);

        mockMvc.perform(delete("/api/veterinarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVeterinario_cuandoNoExiste_retornaHttp404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("No existe un veterinario con id 99"))
                .when(veterinarioService).deleteVeterinario(99L);

        mockMvc.perform(delete("/api/veterinarios/99"))
                .andExpect(status().isNotFound());
    }

    private VeterinarioResponseDTO crearResponse() {
        return new VeterinarioResponseDTO(1L, "Laura", "Suarez", "1122334455", "laura@mail.com", "MAT-123", "Clinica");
    }
}
