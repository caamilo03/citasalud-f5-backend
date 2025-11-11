package com.feature5.pqrs.controller;

import com.feature5.pqrs.config.JwtUtils; // <-- 1. ASEGÚRATE DE IMPORTARLO
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TestController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JdbcTemplate jdbcTemplate; // TestController depende de esto

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void publicEndpoint_debeRetornarOk() throws Exception {
        mockMvc.perform(get("/api/test/public"))
                .andExpect(status().isOk());
    }

    @Test
    void envEndpoint_debeRetornarOk() throws Exception {
        mockMvc.perform(get("/api/test/env"))
                .andExpect(status().isOk());
    }

    @Test
    void seguroEndpoint_debeRetornarOk() throws Exception {
        mockMvc.perform(get("/api/test/seguro"))
                .andExpect(status().isOk());
    }

    @Test
    void verificarTipos_debeRetornarOk() throws Exception {
        // 1. Arrange
        when(jdbcTemplate.queryForList(anyString())).thenReturn(Collections.emptyList());

        // 2. Act & 3. Assert
        mockMvc.perform(get("/api/test/tipos"))
                .andExpect(status().isOk());
    }

    @Test
    void verificarEsquemaPqrs_debeRetornarOk() throws Exception {
        // 1. Arrange
        when(jdbcTemplate.queryForList(anyString())).thenReturn(Collections.emptyList());

        // 2. Act & 3. Assert
        mockMvc.perform(get("/api/test/schema"))
                .andExpect(status().isOk());
    }
}