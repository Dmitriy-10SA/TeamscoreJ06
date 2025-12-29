package manufacturer;

import common.DevicesAndDeviceSensorsInitializer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.UtilsForTests;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class SensorReaderTest {
    private static EntityManagerFactory factory;
    private static SensorReader sensorReader;

    @BeforeAll
    static void init() {
        factory = UtilsForTests.getEntityManagerFactory();
        UtilsForTests.clearDbData(factory);
        DevicesAndDeviceSensorsInitializer.initialize(factory);
        sensorReader = new SensorReader(factory);
    }

    @AfterAll
    static void clearDbDataAndCloseFactory() {
        UtilsForTests.clearDbData(factory);
        factory.close();
    }

    @Test
    void whenMoreThanOneThreadStartThenThrowException() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> sensorReader.start());
        assertThrows(IllegalArgumentException.class, () -> sensorReader.start());
        sensorReader.stop();
        assertFalse(sensorReader.isRunning());
        executorService.shutdown();
    }

    @Test
    void correctStartStopAndSaveSensorReadingsInDatabase() throws InterruptedException {
        try (EntityManager entityManager = factory.createEntityManager()) {
            long count = entityManager
                    .createQuery("SELECT COUNT(s) FROM SensorReading s", Long.class)
                    .getSingleResult();
            assertEquals(0, count);
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        assertFalse(sensorReader.isRunning());
        executorService.execute(() -> sensorReader.start());
        Thread.sleep(1_000);
        sensorReader.stop();
        try (EntityManager entityManager = factory.createEntityManager()) {
            long count = entityManager
                    .createQuery("SELECT COUNT(s) FROM SensorReading s", Long.class)
                    .getSingleResult();
            assertTrue(count > 0);
            assertFalse(sensorReader.isRunning());
            executorService.execute(() -> sensorReader.start());
            Thread.sleep(1_000);
            sensorReader.stop();
            executorService.shutdown();
            long countAgain = entityManager
                    .createQuery("SELECT COUNT(s) FROM SensorReading s", Long.class)
                    .getSingleResult();
            assertTrue(countAgain > count);
            assertFalse(sensorReader.isRunning());
        }
    }
}