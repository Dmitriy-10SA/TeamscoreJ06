package common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerFactoryProviderTest {
    @Test
    void getFactoryShouldNotReturnNull() {
        EntityManagerFactory factory = EntityManagerFactoryProvider.getFactory();
        assertNotNull(factory);
    }

    @Test
    void getFactoryShouldReturnSameInstanceEveryTime() {
        EntityManagerFactory factory1 = EntityManagerFactoryProvider.getFactory();
        EntityManagerFactory factory2 = EntityManagerFactoryProvider.getFactory();
        assertEquals(factory1, factory2);
    }

    @Test
    void factoryShouldBeOpen() {
        EntityManagerFactory factory = EntityManagerFactoryProvider.getFactory();
        assertTrue(factory.isOpen());
    }

    @Test
    void factoryShouldCreateEntityManager() {
        EntityManagerFactory factory = EntityManagerFactoryProvider.getFactory();
        try (EntityManager em = factory.createEntityManager()) {
            assertNotNull(em);
            assertTrue(em.isOpen());
        }
    }
}