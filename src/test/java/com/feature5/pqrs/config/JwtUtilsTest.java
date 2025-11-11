package com.feature5.pqrs.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
// Le doy valores de prueba a las propiedades de JWT
@TestPropertySource(properties = {
        "jwt.secret=testsecretkeyparalaspruebasunitariasqueesbienlarga",
        "jwt.expiration=3600000"
})
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void generateToken_debeCrearUnTokenValido() {
        // 1. Arrange
        String username = "testuser";

        // 2. Act
        String token = jwtUtils.generateToken(username);

        // 3. Assert
        assertNotNull(token);
        assertTrue(token.length() > 50); // Un JWT real es largo
    }

    @Test
    void extractUsername_conTokenValido_debeRetornarUsername() {
        // 1. Arrange
        String username = "testuser";
        String token = jwtUtils.generateToken(username);

        // 2. Act
        String extractedUsername = jwtUtils.extractUsername(token);

        // 3. Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_conTokenValido_debeRetornarTrue() {
        // 1. Arrange
        String token = jwtUtils.generateToken("testuser");

        // 2. Act
        boolean isValid = jwtUtils.validateToken(token);

        // 3. Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_conTokenInvalido_debeRetornarFalse() {
        // 1. Arrange
        String tokenInvalido = "esto.no.es.un.token";

        // 2. Act
        boolean isValid = jwtUtils.validateToken(tokenInvalido);

        // 3. Assert
        assertFalse(isValid);
    }
}