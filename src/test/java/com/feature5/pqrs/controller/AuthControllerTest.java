package com.feature5.pqrs.controller;

import com.feature5.pqrs.DTO.LoginRequestDTO;
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
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // <-- ELIMINADO
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// --- ARREGLO AQUÍ: Desactivamos la seguridad para esta prueba ---
@WebMvcTest(value = AuthController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean // AuthController SÍ necesita JwtUtils, así que este @MockBean se queda
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_conCredencialesCorrectas_debeRetornarTokenYUsuario() throws Exception {
        // 1. Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO("testuser", "pass123");

        UsuarioDTO usuarioValido = new UsuarioDTO();
        usuarioValido.setNickname("testuser");
        usuarioValido.setEmail("test@test.com");
        usuarioValido.setRol(new RolDTO(3L, "Usuario"));

        String tokenFalso = "fake.jwt.token";

        when(usuarioService.login("testuser", "pass123")).thenReturn(usuarioValido);
        when(jwtUtils.generateToken("testuser")).thenReturn(tokenFalso);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))) // <-- SIN .with(csrf())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(tokenFalso)))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void login_conCredencialesInvalidas_debeRetornar401Unauthorized() throws Exception {
        // 1. Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO("testuser", "wrongpass");

        when(usuarioService.login(anyString(), anyString())).thenReturn(null);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))) // <-- SIN .with(csrf())
                .andExpect(status().isUnauthorized());
    }
}