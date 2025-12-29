package manufacturer;

import common.entity.Device;
import common.entity.Sensor;
import common.entity.SensorReading;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.UtilsForTests;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class SensorReadingSaverTest {
    private static SensorReadingSaver saver;
    private static EntityManagerFactory factory;

    @BeforeAll
    static void init() {
        factory = UtilsForTests.getEntityManagerFactory();
        saver = new SensorReadingSaver(factory);
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    @BeforeEach
    void clearDB() {
        UtilsForTests.clearDbData(factory);
    }

    @Test
    void saveCorrectSaveSensorReadingWithNullSavedAt() {
        Device device = new Device("testDevice");
        Sensor sensor = new Sensor("sensorId", Sensor.SensorType.LIGHT);
        device.addSensor(sensor);
        LocalDateTime measureAt = LocalDateTime.now().minusMonths(2);
        String jsonValue = "{\"light\": 123}";
        try (EntityManager entityManager = factory.createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(device);
            entityManager.getTransaction().commit();
        }

        SensorReading sensorReading = new SensorReading(sensor, measureAt, jsonValue);
        saver.save(sensorReading);

        try (EntityManager entityManager = factory.createEntityManager()) {
            SensorReading found = entityManager.find(SensorReading.class, sensorReading.getId());
            assertEquals(sensor.getId(), found.getSensor().getId());
            assertEquals(sensor.getType(), found.getSensor().getType());
            assertEquals(device.getId(), found.getSensor().getDevice().getId());
            assertEquals(measureAt.truncatedTo(ChronoUnit.MILLIS), found.getMeasuredAt());
            assertEquals(jsonValue, found.getValueJson());
            assertNull(sensorReading.getSavedAt());
        }
    }

    @Test
    void saveRollbackOnExceptionAndInDBNoData() {
        SensorReading sensorReading = new SensorReading(null, null, null);
        assertThrows(Exception.class, () -> saver.save(sensorReading));

        try (EntityManager entityManager = factory.createEntityManager()) {
            long count = entityManager
                    .createQuery("SELECT COUNT(r) FROM SensorReading r", Long.class)
                    .getSingleResult();
            assertEquals(0, count);
        }
    }
}