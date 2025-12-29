package common;

import common.entity.Device;
import common.entity.Sensor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.UtilsForTests;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DevicesAndDeviceSensorsInitializerTest {
    private static EntityManagerFactory factory;

    @BeforeAll
    static void initFactory() {
        factory = UtilsForTests.getEntityManagerFactory();
    }

    @BeforeEach
    void clearDb() {
        UtilsForTests.clearDbData(factory);
    }

    @AfterAll
    static void clearDbDataAndCloseFactory() {
        UtilsForTests.clearDbData(factory);
        factory.close();
    }

    @Test
    void shouldCreateAllDevices() {
        DevicesAndDeviceSensorsInitializer.initialize(factory);
        try (EntityManager entityManager = factory.createEntityManager()) {
            List<Device> devices = entityManager
                    .createQuery("SELECT d FROM Device d", Device.class)
                    .getResultList();
            assertEquals(5, devices.size());
            List<String> names = devices.stream().map(Device::getName).toList();
            assertTrue(names.containsAll(List.of(
                    "SmallRicePro99",
                    "УмДомМой",
                    "SmartPhone2024",
                    "IoTDevice01",
                    "SensorHub"
            )));
        }
    }

    @Test
    void eachDeviceShouldHaveAllSensorTypes() {
        DevicesAndDeviceSensorsInitializer.initialize(factory);
        try (EntityManager entityManager = factory.createEntityManager()) {
            List<Device> devices = entityManager
                    .createQuery("SELECT d FROM Device d", Device.class)
                    .getResultList();
            assertFalse(devices.isEmpty());
            for (Device device : devices) {
                List<Sensor> sensors = device.getSensors();
                assertEquals(Sensor.SensorType.values().length, sensors.size());
                var sensorTypes = sensors.stream().map(Sensor::getType).collect(Collectors.toSet());
                assertEquals(Sensor.SensorType.values().length, sensorTypes.size());
            }
        }
    }

    @Test
    void initializeShouldBeIdempotent() {
        DevicesAndDeviceSensorsInitializer.initialize(factory);
        DevicesAndDeviceSensorsInitializer.initialize(factory);
        try (EntityManager entityManager = factory.createEntityManager()) {
            Long deviceCount = entityManager
                    .createQuery("SELECT COUNT(d) FROM Device d", Long.class)
                    .getSingleResult();
            Long sensorCount = entityManager
                    .createQuery("SELECT COUNT(s) FROM Sensor s", Long.class)
                    .getSingleResult();
            assertEquals(5, deviceCount);
            assertEquals(5L * Sensor.SensorType.values().length, sensorCount);
        }
    }

    @Test
    void sensorsShouldBelongToTheirDevice() {
        DevicesAndDeviceSensorsInitializer.initialize(factory);
        try (EntityManager entityManager = factory.createEntityManager()) {
            List<Sensor> sensors = entityManager
                    .createQuery("SELECT s FROM Sensor s", Sensor.class)
                    .getResultList();
            assertFalse(sensors.isEmpty());
            for (Sensor sensor : sensors) {
                assertNotNull(sensor.getDevice());
            }
        }
    }
}