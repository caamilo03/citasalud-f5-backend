package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.config.JwtUtils;
import com.feature5.pqrs.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; // <-- IMPORTACIÓN AÑADIDA
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UsuarioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrar_conDatosValidos_debeRetornarUsuario() throws Exception {
        // 1. Arrange
        RolDTO rolDto = new RolDTO(3L, "Usuario");
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setEmail("test@test.com");
        dto.setNickname("testnick");
        dto.setPassword("pass123");
        dto.setRol(rolDto);

        when(usuarioService.registrarUsuario(any(UsuarioDTO.class))).thenReturn(dto);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname", is("testnick")));
    }

    @Test
    void listar_debeRetornarListaDeUsuarios() throws Exception {
        // 1. Arrange
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNickname("testnick");
        when(usuarioService.listarUsuarios()).thenReturn(List.of(dto));

        // 2. Act & 3. Assert
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname", is("testnick")));
    }

    @Test
    void buscarPorNickname_cuandoExiste_debeRetornarUsuario() throws Exception {
        // 1. Arrange
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNickname("testnick");
        when(usuarioService.buscarPorNickname("testnick")).thenReturn(dto);

        // 2. Act & 3. Assert
        mockMvc.perform(get("/usuarios/testnick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname", is("testnick")));
    }

    @Test
    void buscarPorNickname_cuandoNoExiste_debeRetornar404NotFound() throws Exception {
        // 1. Arrange
        when(usuarioService.buscarPorNickname(anyString())).thenReturn(null);

        // 2. Act & 3. Assert
        mockMvc.perform(get("/usuarios/notfound"))
                .andExpect(status().isNotFound());
    }
}