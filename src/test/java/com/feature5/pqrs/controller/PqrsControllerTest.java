package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.DTO.PqrsRequestDTO;
import com.feature5.pqrs.config.JwtUtils; // <-- 1. ASEGÚRATE DE QUE ESTÉ IMPORTADO
import com.feature5.pqrs.service.PqrsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PqrsController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})

class PqrsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PqrsService pqrsService;


    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void listarPqrs_debeRetornarListaDePqrs() throws Exception {
        // 1. Arrange
        PqrsDTO dto = new PqrsDTO(1L, 1, "Descripción test");
        when(pqrsService.listarTodos()).thenReturn(List.of(dto));

        // 2. Act & 3. Assert
        mockMvc.perform(get("/pqrs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descripcion", is("Descripción test")));
    }

    @Test
    void crearPqrs_conDatosValidos_debeRetornar201Created() throws Exception {
        // 1. Arrange
        PqrsRequestDTO request = new PqrsRequestDTO();
        request.descripcion = "Nueva PQRS";

        PqrsDTO dtoCreado = new PqrsDTO();
        dtoCreado.setIdPqrs(1L);
        dtoCreado.setDescripcion("Nueva PQRS");

        when(pqrsService.crearPqrs(any(), any(), any(), any(PqrsDTO.class))).thenReturn(dtoCreado);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/pqrs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcion", is("Nueva PQRS")));
    }

    @Test
    void crearPqrs_conDatosInvalidos_debeRetornar400BadRequest() throws Exception {
        // 1. Arrange
        PqrsRequestDTO request = new PqrsRequestDTO();
        request.usuarioId = 999L;

        when(pqrsService.crearPqrs(any(), any(), any(), any(PqrsDTO.class)))
                .thenThrow(new IllegalArgumentException("Usuario no encontrado"));

        // 2. Act & 3. Assert
        mockMvc.perform(post("/pqrs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPqrsPorId_cuandoExiste_debeRetornarPqrs() throws Exception {
        // 1. Arrange
        PqrsDTO dto = new PqrsDTO(1L, 1, "PQRS Encontrada");
        when(pqrsService.obtenerPorId(1L)).thenReturn(Optional.of(dto));

        // 2. Act & 3. Assert
        mockMvc.perform(get("/pqrs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("PQRS Encontrada")));
    }

    @Test
    void obtenerPqrsPorId_cuandoNoExiste_debeRetornar404NotFound() throws Exception {
        // 1. Arrange
        when(pqrsService.obtenerPorId(anyLong())).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        mockMvc.perform(get("/pqrs/999"))
                .andExpect(status().isNotFound());
    }
}