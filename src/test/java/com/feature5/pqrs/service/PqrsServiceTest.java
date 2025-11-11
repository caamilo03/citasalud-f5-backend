package com.feature5.pqrs.service;

// Importaciones necesarias para Mockito y JUnit 5
import com.feature5.pqrs.DTO.PqrsDTO;
import com.feature5.pqrs.entities.Estado;
import com.feature5.pqrs.entities.Pqrs;
import com.feature5.pqrs.entities.Tipo;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.mapper.PqrsMapper;
import com.feature5.pqrs.repository.EstadoRepository;
import com.feature5.pqrs.repository.PqrsRepository;
import com.feature5.pqrs.repository.TipoRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Usamos la extensión de Mockito en lugar de @SpringBootTest
@ExtendWith(MockitoExtension.class)
class PqrsServiceTest {

    // Creamos MOCKS (simulaciones) de TODAS las dependencias de PqrsService
    @Mock
    private PqrsRepository pqrsRepository;
    @Mock
    private EstadoRepository estadoRepository;
    @Mock
    private TipoRepository tipoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PqrsMapper pqrsMapper;

    // Inyectamos los mocks en la clase que vamos a probar
    @InjectMocks
    private PqrsService pqrsService;

    @Test
    void crearPqrs_conDatosValidos_debeGuardarYRetornarDTO() {
        // 1. Arrange

        Long usuarioId = 1L;
        Integer tipoId = 1;
        Integer estadoId = 1;
        PqrsDTO pqrsDTOEntrada = new PqrsDTO();
        pqrsDTOEntrada.setDescripcion("Descripción de prueba");
        pqrsDTOEntrada.setFechaDeGeneracion(LocalDate.now());

        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(usuarioId);

        Tipo tipoMock = new Tipo();
        tipoMock.setIdTipo(tipoId);

        Estado estadoMock = new Estado();
        estadoMock.setIdEstado(estadoId);

        Pqrs pqrsGuardadoMock = new Pqrs();
        pqrsGuardadoMock.setIdPqrs(100L); // Asignamos un ID de ejemplo

        PqrsDTO dtoEsperado = new PqrsDTO();
        dtoEsperado.setIdPqrs(100L);
        dtoEsperado.setDescripcion("Descripción de prueba");

        //  CONFIGURACIÓN DE LOS MOCKS
        // Le decimos a Mockito qué hacer cuando se llamen los métodos de las dependencias
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioMock));
        when(tipoRepository.findById(tipoId)).thenReturn(Optional.of(tipoMock));
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(estadoMock));
        when(pqrsRepository.save(any(Pqrs.class))).thenReturn(pqrsGuardadoMock);
        when(pqrsMapper.toDTO(pqrsGuardadoMock)).thenReturn(dtoEsperado);


        // 2. Act

        // Llamamos al método que estamos probando
        PqrsDTO resultado = pqrsService.crearPqrs(usuarioId, tipoId, estadoId, pqrsDTOEntrada);

        // 3. Assert

        assertNotNull(resultado);
        assertEquals(dtoEsperado.getIdPqrs(), resultado.getIdPqrs());
        assertEquals(dtoEsperado.getDescripcion(), resultado.getDescripcion());

        verify(usuarioRepository, times(1)).findById(usuarioId); // Se llamó a findById del usuario 1 vez
        verify(pqrsRepository, times(1)).save(any(Pqrs.class));   // Se llamó a save del pqrs 1 vez
        verify(pqrsMapper, times(1)).toDTO(any(Pqrs.class));      // Se llamó al mapper 1 vez
    }
    @Test
    void crearPqrs_conUsuarioInexistente_debeLanzarExcepcion() {
        // 1. Arrange

        Long usuarioIdInexistente = 999L;
        Integer tipoId = 1;
        Integer estadoId = 1;
        PqrsDTO pqrsDTOEntrada = new PqrsDTO();
        pqrsDTOEntrada.setDescripcion("Descripción de prueba de error");

        // CONFIGURACIÓN DEL MOCK
        when(usuarioRepository.findById(usuarioIdInexistente)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pqrsService.crearPqrs(usuarioIdInexistente, tipoId, estadoId, pqrsDTOEntrada);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(pqrsRepository, never()).save(any(Pqrs.class));
    }
    @Test
    void actualizarPqrs_conDatosValidos_debeActualizarYRetornarDTO() {
        // 1. Arrange (Organizar)
        Long pqrsId = 1L;
        Long usuarioId = 1L;
        Integer tipoId = 1;
        Integer estadoId = 1;

        PqrsDTO dtoConActualizaciones = new PqrsDTO();
        dtoConActualizaciones.setDescripcion("Descripción actualizada");

        Pqrs pqrsExistenteMock = new Pqrs();
        pqrsExistenteMock.setIdPqrs(pqrsId);
        pqrsExistenteMock.setDescripcion("Descripción original");

        PqrsDTO dtoEsperado = new PqrsDTO();
        dtoEsperado.setIdPqrs(pqrsId);
        dtoEsperado.setDescripcion("Descripción actualizada");

        when(pqrsRepository.findById(pqrsId)).thenReturn(Optional.of(pqrsExistenteMock));

        // Le decimos a los otros mocks qué devolver cuando se busquen las entidades asociadas.
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(usuarioId);
        Tipo tipoMock = new Tipo();
        tipoMock.setIdTipo(tipoId);
        Estado estadoMock = new Estado();
        estadoMock.setIdEstado(estadoId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioMock));
        when(tipoRepository.findById(tipoId)).thenReturn(Optional.of(tipoMock));
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(estadoMock));

        when(pqrsRepository.save(any(Pqrs.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(pqrsMapper.toDTO(any(Pqrs.class))).thenReturn(dtoEsperado);

        // 2. Act

        Optional<PqrsDTO> resultadoOptional = pqrsService.actualizarPqrs(pqrsId, usuarioId, tipoId, estadoId, dtoConActualizaciones);

        // 3. Assert


        assertTrue(resultadoOptional.isPresent());
        assertEquals(dtoEsperado.getDescripcion(), resultadoOptional.get().getDescripcion());

        verify(pqrsRepository, times(1)).findById(pqrsId);
        verify(pqrsRepository, times(1)).save(any(Pqrs.class));
    }
    @Test
    void actualizarPqrs_cuandoPqrsNoExiste_debeRetornarOptionalVacio() {
        // 1. Arrange
        Long pqrsIdInexistente = 999L;
        PqrsDTO dtoConActualizaciones = new PqrsDTO();
        dtoConActualizaciones.setDescripcion("No debería guardarse");

        // --- CONFIGURACIÓN DEL MOCK ---
        // Simulamos que el repositorio no encuentra nada para este ID.
        when(pqrsRepository.findById(pqrsIdInexistente)).thenReturn(Optional.empty());


        // 2. Act


        // Llamamos al método de actualización con el ID inexistente.
        Optional<PqrsDTO> resultadoOptional = pqrsService.actualizarPqrs(pqrsIdInexistente, 1L, 1, 1, dtoConActualizaciones);

        // 3. Assert

        assertTrue(resultadoOptional.isEmpty());

        // Se verifica que nunca se intentó guardar nada,
        // ya que la operación debió fallar al no encontrar la PQRS.
        verify(pqrsRepository, never()).save(any(Pqrs.class));
    }
    @Test
    void eliminarPqrs_cuandoPqrsExiste_debeRetornarTrue() {
        // 1. Arrange
        Long pqrsIdExistente = 1L;

        // --- CONFIGURACIÓN DEL MOCK ---
        // se simula que el repositorio confirma que la PQRS con este ID existe.
        when(pqrsRepository.existsById(pqrsIdExistente)).thenReturn(true);

        doNothing().when(pqrsRepository).deleteById(pqrsIdExistente);


        // 2. Act

        // Llamamos al método de eliminación.
        boolean resultado = pqrsService.eliminarPqrs(pqrsIdExistente);

        // 3. Assert

        // Se verifica que el resultado es 'true', indicando éxito.
        assertTrue(resultado);

        // Se verifica que el método deleteById fue llamado exactamente una vez.
        verify(pqrsRepository, times(1)).deleteById(pqrsIdExistente);
    }
    @Test
    void buscarPorEstado_debeRetornarListaDeDTOs() {
        // 1. Arrange

        String estadoTexto = "PENDIENTE";

        Pqrs pqrs1 = new Pqrs();
        pqrs1.setIdPqrs(1L);
        Pqrs pqrs2 = new Pqrs();
        pqrs2.setIdPqrs(2L);
        java.util.List<Pqrs> listaDePqrs = java.util.List.of(pqrs1, pqrs2);

        PqrsDTO dto1 = new PqrsDTO();
        dto1.setIdPqrs(1L);
        PqrsDTO dto2 = new PqrsDTO();
        dto2.setIdPqrs(2L);

        when(pqrsRepository.findByEstadoTexto(estadoTexto)).thenReturn(listaDePqrs);
        when(pqrsMapper.toDTO(pqrs1)).thenReturn(dto1);
        when(pqrsMapper.toDTO(pqrs2)).thenReturn(dto2);

        // 2. Act


        java.util.List<PqrsDTO> resultado = pqrsService.buscarPorEstado(estadoTexto);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size()); // Verificamos que la lista tiene el tamaño esperado.
        verify(pqrsRepository, times(1)).findByEstadoTexto(estadoTexto);
    }

    @Test
    void buscarPorUsuario_debeRetornarListaDeDTOs() {
        // 1. Arrange

        Long idUsuario = 1L;

        Pqrs pqrs1 = new Pqrs();
        pqrs1.setIdPqrs(1L);
        java.util.List<Pqrs> listaDePqrs = java.util.List.of(pqrs1);

        PqrsDTO dto1 = new PqrsDTO();
        dto1.setIdPqrs(1L);

        // CONFIGURACIÓN DE LOS MOCKS
        when(pqrsRepository.findByUsuario_IdUsuario(idUsuario)).thenReturn(listaDePqrs);
        when(pqrsMapper.toDTO(pqrs1)).thenReturn(dto1);


        // 2. Act


        java.util.List<PqrsDTO> resultado = pqrsService.buscarPorUsuario(idUsuario);

        // 3. Assert

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pqrsRepository, times(1)).findByUsuario_IdUsuario(idUsuario);
    }
    @Test
    void responderPqrs_cuandoPqrsExiste_debeActualizarYRetornarDTO() {
        // 1. Arrange

        Long pqrsId = 1L;
        String respuestaTexto = "Esta es la respuesta oficial.";

        Pqrs pqrsExistente = new Pqrs();
        pqrsExistente.setIdPqrs(pqrsId);

        Estado estadoRespondido = new Estado();
        estadoRespondido.setIdEstado(2);
        estadoRespondido.setDescripcion("RESPONDIDO");

        PqrsDTO dtoEsperado = new PqrsDTO();
        dtoEsperado.setIdPqrs(pqrsId);
        dtoEsperado.setRespuesta(respuestaTexto);
        dtoEsperado.setEstado("RESPONDIDO");

        // --- CONFIGURACIÓN DE LOS MOCKS ---
        when(pqrsRepository.findById(pqrsId)).thenReturn(Optional.of(pqrsExistente));
        when(estadoRepository.findByDescripcion("RESPONDIDO")).thenReturn(Optional.of(estadoRespondido));
        when(pqrsRepository.save(any(Pqrs.class))).thenReturn(pqrsExistente); // Devuelve la entidad actualizada
        when(pqrsMapper.toDTO(pqrsExistente)).thenReturn(dtoEsperado);

        // 2. Act
        Optional<PqrsDTO> resultado = pqrsService.responderPqrs(pqrsId, respuestaTexto);

        // 3. Assert
        assertTrue(resultado.isPresent());
        assertEquals(respuestaTexto, resultado.get().getRespuesta());
        assertEquals("RESPONDIDO", resultado.get().getEstado());
        verify(pqrsRepository, times(1)).save(any(Pqrs.class));
    }

    @Test
    void responderPqrs_cuandoPqrsNoExiste_debeRetornarOptionalVacio() {
        // 1. Arrange
        Long pqrsIdInexistente = 999L;
        String respuestaTexto = "No debería guardarse.";

        // --- CONFIGURACIÓN DEL MOCK ---
        when(pqrsRepository.findById(pqrsIdInexistente)).thenReturn(Optional.empty());


        // 2. Act

        Optional<PqrsDTO> resultado = pqrsService.responderPqrs(pqrsIdInexistente, respuestaTexto);


        // 3. Assert

        assertTrue(resultado.isEmpty());
        verify(pqrsRepository, never()).save(any(Pqrs.class));
    }
}
