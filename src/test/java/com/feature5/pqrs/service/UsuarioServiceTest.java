package com.feature5.pqrs.service;

import com.feature5.pqrs.DTO.RolDTO;
import com.feature5.pqrs.DTO.UsuarioDTO;
import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.mapper.UsuarioMapper;
import com.feature5.pqrs.repository.RolRepository;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    // --- Mocks para las dependencias ---
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    // --- Inyectar mocks en el servicio ---
    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void registrarUsuario_conDatosValidos_debeGuardarUsuarioConRolUser() {
        // 1. Arrange (Organizar)
        UsuarioDTO dtoEntrada = new UsuarioDTO();
        dtoEntrada.setEmail("test@example.com");
        dtoEntrada.setNickname("testuser");
        dtoEntrada.setPassword("password123");

        Usuario usuarioMapeado = new Usuario();

        Rol rolUser = new Rol(3L, "Usuario");

        // --- INICIO DE LA CORRECCIÓN ---
        // Creamos un objeto 'usuarioGuardado' más realista, que ya incluye el rol.
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setIdUsuario(1L);
        usuarioGuardado.setRol(rolUser); // <-- ¡ESTA ES LA LÍNEA CLAVE!
        // --- FIN DE LA CORRECCIÓN ---

        UsuarioDTO dtoEsperado = new UsuarioDTO();
        dtoEsperado.setIdUsuario(1L);

        // Configuración de Mocks
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByNickname(anyString())).thenReturn(false);
        when(usuarioMapper.toEntity(dtoEntrada)).thenReturn(usuarioMapeado);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(rolRepository.findById(3L)).thenReturn(Optional.of(rolUser));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado); // Ahora devolvemos el objeto completo
        when(usuarioMapper.toDto(usuarioGuardado)).thenReturn(dtoEsperado);

        // 2. Act (Actuar)
        UsuarioDTO resultado = usuarioService.registrarUsuario(dtoEntrada);

        // 3. Assert (Afirmar)
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdUsuario());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(rolRepository, times(1)).findById(3L);
    }

    @Test
    void registrarUsuario_conEmailDuplicado_debeLanzarExcepcion() {
        // 1. Arrange
        UsuarioDTO dtoEntrada = new UsuarioDTO();
        dtoEntrada.setEmail("duplicate@example.com");

        when(usuarioRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        // 2. Act & 3. Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(dtoEntrada);
        });

        assertEquals("El correo ya está registrado.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void login_conCredencialesCorrectas_debeRetornarUsuarioDTO() {
        // 1. Arrange
        String nickname = "testuser";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";

        Usuario usuarioEncontrado = new Usuario();
        usuarioEncontrado.setPassword(encodedPassword);

        UsuarioDTO dtoEsperado = new UsuarioDTO();
        dtoEsperado.setNickname(nickname);

        when(usuarioRepository.findByNickname(nickname)).thenReturn(Optional.of(usuarioEncontrado));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(usuarioMapper.toDto(usuarioEncontrado)).thenReturn(dtoEsperado);

        // 2. Act
        UsuarioDTO resultado = usuarioService.login(nickname, rawPassword);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals(nickname, resultado.getNickname());
    }

    @Test
    void login_conPasswordIncorrecta_debeRetornarNull() {
        // 1. Arrange
        String nickname = "testuser";
        String wrongPassword = "wrongpassword";

        Usuario usuarioEncontrado = new Usuario();
        usuarioEncontrado.setPassword("encodedPassword");

        when(usuarioRepository.findByNickname(nickname)).thenReturn(Optional.of(usuarioEncontrado));
        when(passwordEncoder.matches(wrongPassword, "encodedPassword")).thenReturn(false);

        // 2. Act
        UsuarioDTO resultado = usuarioService.login(nickname, wrongPassword);

        // 3. Assert
        assertNull(resultado);
        verify(usuarioMapper, never()).toDto(any(Usuario.class));
    }
}