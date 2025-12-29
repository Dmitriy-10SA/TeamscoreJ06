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
        while (!sensorReader.isRunning()) {
            Thread.yield();
        }
        assertThrows(IllegalArgumentException.class, () -> sensorReader.start());
        sensorReader.stop();
        assertFalse(sensorReader.isRunning());
        executorService.shutdown();
    }

    @Test
    void correctStartStopAndSaveSensorReadingsInDatabase() {
        try (EntityManager entityManager = factory.createEntityManager()) {
            long count = entityManager
                    .createQuery("SELECT COUNT(s) FROM SensorReading s", Long.class)
                    .getSingleResult();
            assertEquals(0, count);
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        assertFalse(sensorReader.isRunning());
        executorService.execute(sensorReader::start);
        try {
            while (true) {
                try (EntityManager entityManager = factory.createEntityManager()) {
                    long count = entityManager
                            .createQuery("SELECT COUNT(s) FROM SensorReading s", Long.class)
                            .getSingleResult();
                    if (count > 0) break;
                }
                Thread.yield();
            }
        } finally {
            sensorReader.stop();
        }
        long countAfterFirstRun;
        try (EntityManager entityManager = factory.createEntityManager()) {
            countAfterFirstRun = entityManager
                    .createQuery("SELECT COUNT(s) FROM SensorReading s", Long.class)
                    .getSingleResult();
            assertTrue(countAfterFirstRun > 0);
            assertFalse(sensorReader.isRunning());
        }
        executorService.execute(sensorReader::start);
        try {
            while (true) {
                try (EntityManager entityManager = factory.createEntityManager()) {
                    long count = entityManager
                            .createQuery("SELECT COUNT(s) FROM SensorReading s", Long.class)
                            .getSingleResult();
                    if (count > countAfterFirstRun) break;
                }
                Thread.yield();
            }
        } finally {
            sensorReader.stop();
            executorService.shutdown();
        }
        try (EntityManager entityManager = factory.createEntityManager()) {
            long countAfterSecondRun = entityManager
                    .createQuery("SELECT COUNT(s) FROM SensorReading s", Long.class)
                    .getSingleResult();
            assertTrue(countAfterSecondRun > countAfterFirstRun);
            assertFalse(sensorReader.isRunning());
        }
    }
}