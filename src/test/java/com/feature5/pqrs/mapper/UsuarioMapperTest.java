package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

// Usamos una configuración ligera de Spring para que pueda inyectar los mappers
@SpringJUnitConfig(classes = UsuarioMapperTest.TestConfig.class)
class UsuarioMapperTest {

    // Configuración para que Spring detecte los mappers
    @ComponentScan(basePackages = "com.feature5.pqrs.mapper")
    static class TestConfig {}

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Test
    void debeMapearEntidadADTO() {
        // 1. Arrange
        Rol rol = new Rol(1L, "ADMIN");
        Usuario entidad = new Usuario();
        entidad.setIdUsuario(1L);
        entidad.setNombre("Test");
        entidad.setEmail("test@test.com");
        entidad.setNickname("testuser");
        entidad.setRol(rol);

        // 2. Act
        UsuarioDTO dto = usuarioMapper.toDto(entidad);

        // 3. Assert
        assertNotNull(dto);
        assertEquals(entidad.getNombre(), dto.getNombre());
        assertEquals(entidad.getEmail(), dto.getEmail());
        assertEquals(entidad.getNickname(), dto.getNickname());
        assertNotNull(dto.getRol());
        assertEquals(entidad.getRol().getDescripcion(), dto.getRol().getDescripcion());
    }

    @Test
    void debeMapearDTOAEntidad() {
        // 1. Arrange
        RolDTO rolDTO = new RolDTO(1L, "ADMIN");
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test");
        dto.setEmail("test@test.com");
        dto.setNickname("testuser");
        dto.setRol(rolDTO);

        // 2. Act
        Usuario entidad = usuarioMapper.toEntity(dto);

        // 3. Assert
        assertNotNull(entidad);
        assertEquals(dto.getNombre(), entidad.getNombre());
        assertEquals(dto.getEmail(), entidad.getEmail());
        assertEquals(dto.getNickname(), entidad.getNickname());
        assertNotNull(entidad.getRol());
        assertEquals(dto.getRol().getDescripcion(), entidad.getRol().getDescripcion());
    }
}