package com.feature5.pqrs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class Feature5PqrsApplicationTest {

    @Test
    void contextLoads(ApplicationContext context) {
        // Esta es la prueba de "humo" más simple.
        // Si la aplicación no puede arrancar, esta prueba fallará.

        // 1. Arrange (El contexto es inyectado por @SpringBootTest)

        // 2. Act (No se necesita ninguna acción, el arranque es la acción)

        // 3. Assert
        // Verificamos que el contexto de la aplicación no es nulo.
        assertNotNull(context, "El contexto de la aplicación no debería ser nulo.");
    }

    @Test
    void main() {
        // Esta prueba cubre la ejecución del método main() para la cobertura
        Feature5PqrsApplication.main(new String[]{});
    }
}