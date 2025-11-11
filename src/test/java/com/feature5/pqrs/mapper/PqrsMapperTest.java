package com.feature5.pqrs.mapper;

import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.entities.Tipo;
import com.feature5.pqrs.entities.Usuario;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PqrsMapperTest {

    // Obtenemos la implementación real del mapper
    private final PqrsMapper mapper = Mappers.getMapper(PqrsMapper.class);

    @Test
    void debeMapearEntidadADTO() {
        // 1. Arrange
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Tipo tipo = new Tipo();
        tipo.setIdTipo(2);

        Estado estado = new Estado();
        estado.setDescripcion("PENDIENTE");

        Pqrs entidad = new Pqrs();
        entidad.setIdPqrs(100L);
        entidad.setUsuario(usuario);
        entidad.setTipo(tipo);
        entidad.setEstado(estado);
        entidad.setEstadoTexto("PENDIENTE");
        entidad.setRadicado("RAD-123");
        entidad.setDescripcion("Test");
        entidad.setFechaDeGeneracion(LocalDateTime.now());

        // 2. Act
        PqrsDTO dto = mapper.toDTO(entidad);

        // 3. Assert
        assertNotNull(dto);
        assertEquals(entidad.getIdPqrs(), dto.getIdPqrs());
        assertEquals(entidad.getUsuario().getIdUsuario(), dto.getIdUsuario());
        assertEquals(entidad.getTipo().getIdTipo(), dto.getIdTipo());
        assertEquals(entidad.getEstadoTexto(), dto.getEstado());
        assertEquals(entidad.getRadicado(), dto.getRadicado());
        assertEquals(entidad.getDescripcion(), dto.getDescripcion());
    }

    @Test
    void debeMapearDTOAEntidad() {
        // 1. Arrange
        PqrsDTO dto = new PqrsDTO();
        dto.setIdPqrs(100L);
        dto.setIdUsuario(1L); // Este campo existe en el DTO
        dto.setIdTipo(2);    // Este campo existe en el DTO
        dto.setEstado("PENDIENTE");
        dto.setRadicado("RAD-123");
        dto.setDescripcion("Test");

        // 2. Act
        Pqrs entidad = mapper.toEntity(dto);

        // 3. Assert
        assertNotNull(entidad);
        assertNull(entidad.getUsuario());
        assertNull(entidad.getTipo());

        assertEquals(dto.getEstado(), entidad.getEstadoTexto());
        assertEquals(dto.getRadicado(), entidad.getRadicado());
        assertEquals(dto.getDescripcion(), entidad.getDescripcion());
    }
}