package aggregator.data.provider;

import common.entity.Device;
import common.entity.Sensor;
import common.entity.sensor.data.AccelerometerData;
import common.entity.sensor.data.BarometerData;
import common.entity.sensor.data.LightData;
import common.entity.sensor.data.LocationData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import utils.UtilsForTests;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SensorDataProviderTest {
    private static EntityManagerFactory factory;

    @BeforeAll
    static void init() {
        factory = UtilsForTests.getEntityManagerFactory();
    }

    @AfterAll
    static void clearDbDataAndCloseFactory() {
        UtilsForTests.clearDbData(factory);
        factory.close();
    }

    @BeforeEach
    void clearDbData() {
        UtilsForTests.clearDbData(factory);
    }

    @ParameterizedTest
    @EnumSource(Sensor.SensorType.class)
    void testGetDataForAllSensorTypes(Sensor.SensorType type) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus3Days = now.plusDays(3);
        SensorDataProvider<?> dataProvider = switch (type) {
            case LOCATION -> new LocationSensorDataProvider(factory);
            case BAROMETER -> new BarometerSensorDataProvider(factory);
            case ACCELEROMETER -> new AccelerometerSensorDataProvider(factory);
            case LIGHT -> new LightSensorDataProvider(factory);
        };
        switch (type) {
            case LIGHT -> assertEquals("LightData", dataProvider.getEntityName());
            case LOCATION -> assertEquals("LocationData", dataProvider.getEntityName());
            case BAROMETER -> assertEquals("BarometerData", dataProvider.getEntityName());
            case ACCELEROMETER -> assertEquals("AccelerometerData", dataProvider.getEntityName());
        }
        switch (type) {
            case LIGHT -> assertEquals(LightData.class, dataProvider.getEntityClass());
            case LOCATION -> assertEquals(LocationData.class, dataProvider.getEntityClass());
            case BAROMETER -> assertEquals(BarometerData.class, dataProvider.getEntityClass());
            case ACCELEROMETER -> assertEquals(AccelerometerData.class, dataProvider.getEntityClass());
        }
        try (EntityManager entityManager = factory.createEntityManager()) {
            Device device = new Device("name" + type.name());
            Sensor sensor = new Sensor("id" + type.name(), type);
            device.addSensor(sensor);
            entityManager.getTransaction().begin();
            entityManager.persist(device);
            switch (type) {
                case LIGHT -> {
                    entityManager.persist(new LightData(sensor, now, 100));
                    entityManager.persist(new LightData(sensor, nowPlus3Days, 100));
                }
                case LOCATION -> {
                    entityManager.persist(new LocationData(sensor, now, 100.0, 100.0));
                    entityManager.persist(new LocationData(sensor, nowPlus3Days, 100.0, 100.0));
                }
                case BAROMETER -> {
                    entityManager.persist(new BarometerData(sensor, now, 100));
                    entityManager.persist(new BarometerData(sensor, nowPlus3Days, 100));
                }
                case ACCELEROMETER -> {
                    entityManager.persist(new AccelerometerData(sensor, now, 100, 100, 100));
                    entityManager.persist(new AccelerometerData(sensor, nowPlus3Days, 100, 100, 100));
                }
            }
            entityManager.getTransaction().commit();
        }
        try (EntityManager entityManager = factory.createEntityManager()) {
            long count = entityManager
                    .createQuery("SELECT COUNT(*) FROM " + dataProvider.getEntityName(), Long.class)
                    .getSingleResult();
            assertEquals(2, count);
            List<?> allData = assertDoesNotThrow(
                    () -> dataProvider.getData(now.minusDays(1), now.plusDays(4), null)
            );
            List<?> notAllData = assertDoesNotThrow(
                    () -> dataProvider.getData(now.minusDays(1), now.plusDays(1), null)
            );
            List<?> unknownNameData = assertDoesNotThrow(
                    () -> dataProvider.getData(now.minusDays(1), now.plusDays(4), "unknownName")
            );
            List<?> unknownDatesData = assertDoesNotThrow(
                    () -> dataProvider.getData(now.minusDays(4), now.minusDays(1), null)
            );
            assertEquals(2, allData.size());
            assertEquals(1, notAllData.size());
            assertTrue(unknownNameData.isEmpty());
            assertTrue(unknownDatesData.isEmpty());
        }
    }
}