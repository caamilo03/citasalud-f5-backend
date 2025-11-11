package com.feature5.pqrs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class Feature5PqrsApplicationTest {

    @Test
    void contextLoads(ApplicationContext context) {


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