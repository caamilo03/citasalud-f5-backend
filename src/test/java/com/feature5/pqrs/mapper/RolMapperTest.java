package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.entities.Rol;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.jupiter.api.Assertions.*;

class RolMapperTest {

    private final RolMapper mapper = Mappers.getMapper(RolMapper.class);

    @Test
    void debeMapearEntidadADTO() {
        // 1. Arrange
        Rol entidad = new Rol(1L, "ADMIN");

        // 2. Act
        RolDTO dto = mapper.toDto(entidad);

        // 3. Assert
        assertNotNull(dto);
        assertEquals(entidad.getIdRol(), dto.getIdRol());
        assertEquals(entidad.getDescripcion(), dto.getDescripcion());
    }

    @Test
    void debeMapearDTOAEntidad() {
        // 1. Arrange
        RolDTO dto = new RolDTO(1L, "ADMIN");

        // 2. Act
        Rol entidad = mapper.toEntity(dto);

        // 3. Assert
        assertNotNull(entidad);
        assertEquals(dto.getIdRol(), entidad.getIdRol());
        assertEquals(dto.getDescripcion(), entidad.getDescripcion());
    }
}