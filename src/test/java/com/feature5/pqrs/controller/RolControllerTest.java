package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.config.JwtUtils;
import com.feature5.pqrs.service.RolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; // <-- IMPORTACIÓN AÑADIDA
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// --- ARREGLO AQUÍ: Desactivamos la seguridad para esta prueba ---
@WebMvcTest(value = RolController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolService rolService;

    @MockBean private JwtUtils jwtUtils;
    // Ya no necesitamos mockear JwtUtils

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarRoles_debeRetornarListaDeRoles() throws Exception {
        // 1. Arrange
        when(rolService.listarRoles()).thenReturn(List.of(new RolDTO(1L, "ADMIN")));

        // 2. Act & 3. Assert
        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descripcion", is("ADMIN")));
    }

    @Test
    void obtenerRolPorId_cuandoExiste_debeRetornarRol() throws Exception {
        // 1. Arrange
        when(rolService.obtenerRolPorId(1L)).thenReturn(Optional.of(new RolDTO(1L, "ADMIN")));

        // 2. Act & 3. Assert
        mockMvc.perform(get("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("ADMIN")));
    }

    @Test
    void obtenerRolPorId_cuandoNoExiste_debeRetornar404NotFound() throws Exception {
        // 1. Arrange
        when(rolService.obtenerRolPorId(anyLong())).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        mockMvc.perform(get("/roles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearRol_debeRetornarRolCreado() throws Exception {
        // 1. Arrange
        RolDTO rolACrear = new RolDTO(null, "NUEVO");
        RolDTO rolCreado = new RolDTO(1L, "NUEVO");
        when(rolService.crearRol(any(RolDTO.class))).thenReturn(rolCreado);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolACrear)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRol", is(1)));
    }
}