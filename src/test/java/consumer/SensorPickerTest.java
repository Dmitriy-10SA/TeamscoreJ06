package consumer;

import common.DevicesAndDeviceSensorsInitializer;
import common.entity.Device;
import common.entity.Sensor;
import common.entity.SensorReading;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import utils.UtilsForTests;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class SensorPickerTest {
    private static EntityManagerFactory factory;
    private static SensorPicker sensorPicker;

    @BeforeAll
    static void init() {
        factory = UtilsForTests.getEntityManagerFactory();
        UtilsForTests.clearDbData(factory);
        DevicesAndDeviceSensorsInitializer.initialize(factory);
        sensorPicker = new SensorPicker(factory);
    }

    @AfterAll
    static void clearDbDataAndCloseFactory() {
        UtilsForTests.clearDbData(factory);
        factory.close();
    }

    @Test
    void whenMoreThanOneThreadStartThenThrowException() throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> sensorPicker.start());
        Thread.sleep(1_000);
        assertThrows(IllegalArgumentException.class, () -> sensorPicker.start());
        sensorPicker.stop();
        assertFalse(sensorPicker.isRunning());
        executorService.shutdown();
    }

    @ParameterizedTest
    @EnumSource(Sensor.SensorType.class)
    void correctStartStopAndProcessSensorReadingsForAllSensorTypes(
            Sensor.SensorType type
    ) throws InterruptedException {
        String json = switch (type) {
            case LOCATION -> "{\"longitude\": 10.0, \"latitude\": 20.0}";
            case LIGHT -> "{\"light\": 123}";
            case BAROMETER -> "{\"air_pressure\": 1013.25}";
            case ACCELEROMETER -> "{\"x\": 1.0, \"y\": 2.0, \"z\": 3.0}";
        };
        String dataEntityName = switch (type) {
            case LOCATION -> "LocationData";
            case LIGHT -> "LightData";
            case BAROMETER -> "BarometerData";
            case ACCELEROMETER -> "AccelerometerData";
        };
        try (EntityManager entityManager = factory.createEntityManager()) {
            entityManager.getTransaction().begin();
            Device device = new Device("testDevice" + type.name());
            Sensor sensor = new Sensor(type.name(), type);
            device.addSensor(sensor);
            entityManager.persist(device);
            entityManager.persist(new SensorReading(new Sensor(type.name(), type), LocalDateTime.now(), json));
            entityManager.getTransaction().commit();
        }
        try (EntityManager entityManager = factory.createEntityManager()) {
            long notSaved = entityManager
                    .createQuery("SELECT COUNT(sr) FROM SensorReading sr WHERE sr.savedAt IS NULL", Long.class)
                    .getSingleResult();
            long dataCount = entityManager
                    .createQuery("SELECT COUNT(d) FROM " + dataEntityName + " d", Long.class)
                    .getSingleResult();
            assertEquals(1, notSaved);
            assertEquals(0, dataCount);
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(sensorPicker::start);
        Thread.sleep(1_000);
        sensorPicker.stop();
        executor.shutdown();
        try (EntityManager entityManager = factory.createEntityManager()) {
            long notSavedAfter = entityManager
                    .createQuery("SELECT COUNT(sr) FROM SensorReading sr WHERE sr.savedAt IS NULL", Long.class)
                    .getSingleResult();
            long dataCountAfter = entityManager
                    .createQuery("SELECT COUNT(d) FROM " + dataEntityName + " d", Long.class)
                    .getSingleResult();
            assertEquals(0, notSavedAfter);
            assertEquals(1, dataCountAfter);
            assertFalse(sensorPicker.isRunning());
        }
    }
}