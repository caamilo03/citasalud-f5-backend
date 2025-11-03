package com.feature5.pqrs.config;

import com.feature5.pqrs.entities.Rol;
import com.feature5.pqrs.entities.Usuario;
import com.feature5.pqrs.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils; // <-- IMPORTACIÓN AÑADIDA

import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_conUsuarioValido_debeRetornarUserDetails() {
        // 1. Arrange
        Rol rol = new Rol(3L, "Usuario");
        Usuario usuarioMock = new Usuario();
        usuarioMock.setNickname("testuser");
        usuarioMock.setPassword("encodedpass");
        usuarioMock.setRol(rol);

        when(usuarioRepository.findByNickname("testuser")).thenReturn(Optional.of(usuarioMock));

        // 2. Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // 3. Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedpass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USUARIO")));
    }

    @Test
    void loadUserByUsername_conUsuarioInvalido_debeLanzarExcepcion() {
        // 1. Arrange
        when(usuarioRepository.findByNickname(anyString())).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nouser");
        });
    }

    @Test
    void loadUserByUsername_conAdminEspecial_debeRetornarAdminDetails() {
        // 1. Arrange
        // --- INICIO DE LA CORRECCIÓN ---
        // Inyectamos manualmente los valores que Spring @Value haría
        ReflectionTestUtils.setField(customUserDetailsService, "adminUsername", "admin_test_user");
        ReflectionTestUtils.setField(customUserDetailsService, "adminPassword", "admin_test_pass");
        // --- FIN DE LA CORRECCIÓN ---

        when(passwordEncoder.encode("admin_test_pass")).thenReturn("adminpass_encoded");

        // 2. Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        // 3. Assert
        assertNotNull(userDetails);
        assertEquals("admin_test_user", userDetails.getUsername()); // Ahora sí podemos verificarlo
        assertEquals("adminpass_encoded", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}