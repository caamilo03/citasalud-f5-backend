package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.mapper.RolMapper;
import com.feature5.pqrs.repository.RolRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    // --- Mocks ---
    @Mock
    private RolRepository rolRepository;
    @Mock
    private RolMapper rolMapper;

    // --- Inyección ---
    @InjectMocks
    private RolService rolService;

    @Test
    void crearRol_conDatosValidos_debeGuardarYRetornarDTO() {
        // 1. Arrange
        RolDTO dtoEntrada = new RolDTO(null, "NUEVO_ROL");
        Rol rolMapeado = new Rol("NUEVO_ROL");
        Rol rolGuardado = new Rol(1L, "NUEVO_ROL");
        RolDTO dtoEsperado = new RolDTO(1L, "NUEVO_ROL");

        when(rolRepository.findByDescripcion("NUEVO_ROL")).thenReturn(Optional.empty());
        when(rolMapper.toEntity(dtoEntrada)).thenReturn(rolMapeado);
        when(rolRepository.save(rolMapeado)).thenReturn(rolGuardado);
        when(rolMapper.toDto(rolGuardado)).thenReturn(dtoEsperado);

        // 2. Act
        RolDTO resultado = rolService.crearRol(dtoEntrada);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRol());
        verify(rolRepository, times(1)).save(rolMapeado);
    }

    @Test
    void crearRol_conDescripcionDuplicada_debeLanzarExcepcion() {
        // 1. Arrange
        RolDTO dtoEntrada = new RolDTO(null, "ROL_EXISTENTE");
        when(rolRepository.findByDescripcion("ROL_EXISTENTE")).thenReturn(Optional.of(new Rol()));

        // 2. Act & 3. Assert
        assertThrows(IllegalArgumentException.class, () -> {
            rolService.crearRol(dtoEntrada);
        });

        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void listarRoles_debeRetornarListaDeDTOs() {
        // 1. Arrange
        Rol rol = new Rol(1L, "ADMIN");
        RolDTO rolDTO = new RolDTO(1L, "ADMIN");

        when(rolRepository.findAll()).thenReturn(List.of(rol));
        when(rolMapper.toDto(rol)).thenReturn(rolDTO);

        // 2. Act
        List<RolDTO> resultado = rolService.listarRoles();

        // 3. Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void eliminarRol_cuandoRolExiste_debeRetornarTrue() {
        // 1. Arrange
        Long rolId = 1L;
        when(rolRepository.existsById(rolId)).thenReturn(true);
        doNothing().when(rolRepository).deleteById(rolId);

        // 2. Act
        boolean resultado = rolService.eliminarRol(rolId);

        // 3. Assert
        assertTrue(resultado);
        verify(rolRepository, times(1)).deleteById(rolId);
    }

    @Test
    void eliminarRol_cuandoRolNoExiste_debeRetornarFalse() {
        // 1. Arrange
        Long rolId = 99L;
        when(rolRepository.existsById(rolId)).thenReturn(false);

        // 2. Act
        boolean resultado = rolService.eliminarRol(rolId);

        // 3. Assert
        assertFalse(resultado);
        verify(rolRepository, never()).deleteById(rolId);
    }
}