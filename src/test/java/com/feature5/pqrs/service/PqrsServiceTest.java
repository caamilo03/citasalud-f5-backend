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
        // ==========================================================
        // 1. Arrange (Organizar)
        // ==========================================================

        // Datos de entrada
        Long usuarioId = 1L;
        Integer tipoId = 1;
        Integer estadoId = 1;
        PqrsDTO pqrsDTOEntrada = new PqrsDTO();
        pqrsDTOEntrada.setDescripcion("Descripción de prueba");
        pqrsDTOEntrada.setFechaDeGeneracion(LocalDate.now());

        // Objetos que los repositorios "encontrarán"
        Usuario usuarioMock = new Usuario();
        usuarioMock.setIdUsuario(usuarioId);

        Tipo tipoMock = new Tipo();
        tipoMock.setIdTipo(tipoId);

        Estado estadoMock = new Estado();
        estadoMock.setIdEstado(estadoId);

        // Objeto que el repositorio "guardará"
        Pqrs pqrsGuardadoMock = new Pqrs();
        pqrsGuardadoMock.setIdPqrs(100L); // Asignamos un ID de ejemplo

        // Objeto DTO que el mapper "devolverá"
        PqrsDTO dtoEsperado = new PqrsDTO();
        dtoEsperado.setIdPqrs(100L);
        dtoEsperado.setDescripcion("Descripción de prueba");

        // --- CONFIGURACIÓN DE LOS MOCKS ---
        // Le decimos a Mockito qué hacer cuando se llamen los métodos de las dependencias
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioMock));
        when(tipoRepository.findById(tipoId)).thenReturn(Optional.of(tipoMock));
        when(estadoRepository.findById(estadoId)).thenReturn(Optional.of(estadoMock));
        when(pqrsRepository.save(any(Pqrs.class))).thenReturn(pqrsGuardadoMock);
        when(pqrsMapper.toDTO(pqrsGuardadoMock)).thenReturn(dtoEsperado);

        // ==========================================================
        // 2. Act (Actuar)
        // ==========================================================

        // Llamamos al método que estamos probando
        PqrsDTO resultado = pqrsService.crearPqrs(usuarioId, tipoId, estadoId, pqrsDTOEntrada);

        // ==========================================================
        // 3. Assert (Afirmar)
        // ==========================================================

        // Verificamos que el resultado es el que esperamos
        assertNotNull(resultado);
        assertEquals(dtoEsperado.getIdPqrs(), resultado.getIdPqrs());
        assertEquals(dtoEsperado.getDescripcion(), resultado.getDescripcion());

        // Verificamos que los métodos de los mocks fueron llamados como esperábamos
        verify(usuarioRepository, times(1)).findById(usuarioId); // Se llamó a findById del usuario 1 vez
        verify(pqrsRepository, times(1)).save(any(Pqrs.class));   // Se llamó a save del pqrs 1 vez
        verify(pqrsMapper, times(1)).toDTO(any(Pqrs.class));      // Se llamó al mapper 1 vez
    }
    @Test
    void crearPqrs_conUsuarioInexistente_debeLanzarExcepcion() {
        // ==========================================================
        // 1. Arrange (Organizar)
        // ==========================================================

        // Datos de entrada con un ID de usuario que no existirá
        Long usuarioIdInexistente = 999L;
        Integer tipoId = 1;
        Integer estadoId = 1;
        PqrsDTO pqrsDTOEntrada = new PqrsDTO();
        pqrsDTOEntrada.setDescripcion("Descripción de prueba de error");

        // --- CONFIGURACIÓN DEL MOCK ---
        // Le decimos a Mockito que cuando busque el usuario 999, devuelva "nada" (Optional.empty())
        when(usuarioRepository.findById(usuarioIdInexistente)).thenReturn(Optional.empty());

        // ==========================================================
        // 2. Act & 3. Assert (Actuar y Afirmar)
        // ==========================================================

        // Verificamos que al llamar al método de servicio, se lanza la excepción esperada.
        // La lógica de la prueba se ejecuta dentro de la expresión lambda.
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pqrsService.crearPqrs(usuarioIdInexistente, tipoId, estadoId, pqrsDTOEntrada);
        });

        // Opcional: podemos verificar que el mensaje de la excepción es el correcto.
        assertEquals("Usuario no encontrado", exception.getMessage());

        // Verificamos que el método save NUNCA fue llamado, porque la operación falló antes.
        verify(pqrsRepository, never()).save(any(Pqrs.class));
    }
    @Test
    void actualizarPqrs_conDatosValidos_debeActualizarYRetornarDTO() {
        // ==========================================================
        // 1. Arrange (Organizar)
        // ==========================================================
        Long pqrsId = 1L;
        Long usuarioId = 1L;
        Integer tipoId = 1;
        Integer estadoId = 1;

        // DTO con la nueva información a actualizar
        PqrsDTO dtoConActualizaciones = new PqrsDTO();
        dtoConActualizaciones.setDescripcion("Descripción actualizada");

        // Creamos una PQRS "existente" que el repositorio encontrará.
        Pqrs pqrsExistenteMock = new Pqrs();
        pqrsExistenteMock.setIdPqrs(pqrsId);
        pqrsExistenteMock.setDescripcion("Descripción original");

        // DTO que el mapper devolverá al final
        PqrsDTO dtoEsperado = new PqrsDTO();
        dtoEsperado.setIdPqrs(pqrsId);
        dtoEsperado.setDescripcion("Descripción actualizada");

        // --- CONFIGURACIÓN DE LOS MOCKS ---
        // Cuando se busque la PQRS por su ID, devolvemos la que acabamos de crear.
        when(pqrsRepository.findById(pqrsId)).thenReturn(Optional.of(pqrsExistenteMock));

        // ****** INICIO DE LA CORRECCIÓN ******
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
        // ****** FIN DE LA CORRECCIÓN ******

        // Cuando se guarde cualquier entidad Pqrs, devolvemos esa misma entidad.
        when(pqrsRepository.save(any(Pqrs.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Cuando el mapper convierta la entidad actualizada, devolverá nuestro DTO esperado.
        when(pqrsMapper.toDTO(any(Pqrs.class))).thenReturn(dtoEsperado);

        // ==========================================================
        // 2. Act (Actuar)
        // ==========================================================

        // Llamamos al método de actualización. El resultado es un Optional.
        Optional<PqrsDTO> resultadoOptional = pqrsService.actualizarPqrs(pqrsId, usuarioId, tipoId, estadoId, dtoConActualizaciones);

        // ==========================================================
        // 3. Assert (Afirmar)
        // ==========================================================

        // Verificamos que el Optional no esté vacío y que el contenido sea el esperado.
        assertTrue(resultadoOptional.isPresent());
        assertEquals(dtoEsperado.getDescripcion(), resultadoOptional.get().getDescripcion());

        // Verificamos que los métodos clave fueron llamados.
        verify(pqrsRepository, times(1)).findById(pqrsId);
        verify(pqrsRepository, times(1)).save(any(Pqrs.class));
    }
    @Test
    void actualizarPqrs_cuandoPqrsNoExiste_debeRetornarOptionalVacio() {
        // ==========================================================
        // 1. Arrange (Organizar)
        // ==========================================================
        Long pqrsIdInexistente = 999L;
        PqrsDTO dtoConActualizaciones = new PqrsDTO();
        dtoConActualizaciones.setDescripcion("No debería guardarse");

        // --- CONFIGURACIÓN DEL MOCK ---
        // Simulamos que el repositorio no encuentra nada para este ID.
        when(pqrsRepository.findById(pqrsIdInexistente)).thenReturn(Optional.empty());

        // ==========================================================
        // 2. Act (Actuar)
        // ==========================================================

        // Llamamos al método de actualización con el ID inexistente.
        Optional<PqrsDTO> resultadoOptional = pqrsService.actualizarPqrs(pqrsIdInexistente, 1L, 1, 1, dtoConActualizaciones);

        // ==========================================================
        // 3. Assert (Afirmar)
        // ==========================================================

        // Verificamos que el resultado es un Optional vacío.
        assertTrue(resultadoOptional.isEmpty());

        // MUY IMPORTANTE: Verificamos que NUNCA se intentó guardar nada,
        // ya que la operación debió fallar al no encontrar la PQRS.
        verify(pqrsRepository, never()).save(any(Pqrs.class));
    }
    @Test
    void eliminarPqrs_cuandoPqrsExiste_debeRetornarTrue() {
        // ==========================================================
        // 1. Arrange (Organizar)
        // ==========================================================
        Long pqrsIdExistente = 1L;

        // --- CONFIGURACIÓN DEL MOCK ---
        // Simulamos que el repositorio confirma que la PQRS con este ID existe.
        when(pqrsRepository.existsById(pqrsIdExistente)).thenReturn(true);

        // Configuramos el mock para que no haga nada cuando se llame a deleteById.
        // Esto previene errores en el mock.
        doNothing().when(pqrsRepository).deleteById(pqrsIdExistente);

        // ==========================================================
        // 2. Act (Actuar)
        // ==========================================================

        // Llamamos al método de eliminación.
        boolean resultado = pqrsService.eliminarPqrs(pqrsIdExistente);

        // ==========================================================
        // 3. Assert (Afirmar)
        // ==========================================================

        // Verificamos que el resultado es 'true', indicando éxito.
        assertTrue(resultado);

        // Verificamos que el método deleteById fue llamado exactamente una vez.
        verify(pqrsRepository, times(1)).deleteById(pqrsIdExistente);
    }
    @Test
    void buscarPorEstado_debeRetornarListaDeDTOs() {
        // ==========================================================
        // 1. Arrange (Organizar)
        // ==========================================================
        String estadoTexto = "PENDIENTE";

        // Creamos una lista de entidades Pqrs que el repositorio "encontrará".
        Pqrs pqrs1 = new Pqrs();
        pqrs1.setIdPqrs(1L);
        Pqrs pqrs2 = new Pqrs();
        pqrs2.setIdPqrs(2L);
        java.util.List<Pqrs> listaDePqrs = java.util.List.of(pqrs1, pqrs2);

        // Creamos los DTOs que el mapper "convertirá".
        PqrsDTO dto1 = new PqrsDTO();
        dto1.setIdPqrs(1L);
        PqrsDTO dto2 = new PqrsDTO();
        dto2.setIdPqrs(2L);

        // --- CONFIGURACIÓN DE LOS MOCKS ---
        when(pqrsRepository.findByEstadoTexto(estadoTexto)).thenReturn(listaDePqrs);
        when(pqrsMapper.toDTO(pqrs1)).thenReturn(dto1);
        when(pqrsMapper.toDTO(pqrs2)).thenReturn(dto2);

        // ==========================================================
        // 2. Act (Actuar)
        // ==========================================================

        java.util.List<PqrsDTO> resultado = pqrsService.buscarPorEstado(estadoTexto);

        // ==========================================================
        // 3. Assert (Afirmar)
        // ==========================================================

        assertNotNull(resultado);
        assertEquals(2, resultado.size()); // Verificamos que la lista tiene el tamaño esperado.
        verify(pqrsRepository, times(1)).findByEstadoTexto(estadoTexto);
    }

    @Test
    void buscarPorUsuario_debeRetornarListaDeDTOs() {
        // ==========================================================
        // 1. Arrange (Organizar)
        // ==========================================================
        Long idUsuario = 1L;

        // La configuración es casi idéntica a la prueba anterior.
        Pqrs pqrs1 = new Pqrs();
        pqrs1.setIdPqrs(1L);
        java.util.List<Pqrs> listaDePqrs = java.util.List.of(pqrs1);

        PqrsDTO dto1 = new PqrsDTO();
        dto1.setIdPqrs(1L);

        // --- CONFIGURACIÓN DE LOS MOCKS ---
        when(pqrsRepository.findByUsuario_IdUsuario(idUsuario)).thenReturn(listaDePqrs);
        when(pqrsMapper.toDTO(pqrs1)).thenReturn(dto1);

        // ==========================================================
        // 2. Act (Actuar)
        // ==========================================================

        java.util.List<PqrsDTO> resultado = pqrsService.buscarPorUsuario(idUsuario);

        // ==========================================================
        // 3. Assert (Afirmar)
        // ==========================================================

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pqrsRepository, times(1)).findByUsuario_IdUsuario(idUsuario);
    }
    @Test
    void responderPqrs_cuandoPqrsExiste_debeActualizarYRetornarDTO() {
        // ==========================================================
        // 1. Arrange (Organizar)
        // ==========================================================
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

        // ==========================================================
        // 2. Act (Actuar)
        // ==========================================================
        Optional<PqrsDTO> resultado = pqrsService.responderPqrs(pqrsId, respuestaTexto);

        // ==========================================================
        // 3. Assert (Afirmar)
        // ==========================================================
        assertTrue(resultado.isPresent());
        assertEquals(respuestaTexto, resultado.get().getRespuesta());
        assertEquals("RESPONDIDO", resultado.get().getEstado());
        verify(pqrsRepository, times(1)).save(any(Pqrs.class));
    }

    @Test
    void responderPqrs_cuandoPqrsNoExiste_debeRetornarOptionalVacio() {
        // ==========================================================
        // 1. Arrange (Organizar)
        // ==========================================================
        Long pqrsIdInexistente = 999L;
        String respuestaTexto = "No debería guardarse.";

        // --- CONFIGURACIÓN DEL MOCK ---
        when(pqrsRepository.findById(pqrsIdInexistente)).thenReturn(Optional.empty());

        // ==========================================================
        // 2. Act (Actuar)
        // ==========================================================
        Optional<PqrsDTO> resultado = pqrsService.responderPqrs(pqrsIdInexistente, respuestaTexto);

        // ==========================================================
        // 3. Assert (Afirmar)
        // ==========================================================
        assertTrue(resultado.isEmpty());
        verify(pqrsRepository, never()).save(any(Pqrs.class));
    }
}
