package aggregator.data.provider;

import common.entity.sensor.data.AccelerometerData;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import utils.UtilsForTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccelerometerSensorDataProviderTest {
    private final EntityManagerFactory factory = UtilsForTests.getEntityManagerFactory();
    private final AccelerometerSensorDataProvider provider = new AccelerometerSensorDataProvider(factory);

    @Test
    void shouldReturnCorrectEntityName() {
        assertEquals("AccelerometerData", provider.getEntityName());
    }

    @Test
    void shouldReturnCorrectEntityClass() {
        assertEquals(AccelerometerData.class, provider.getEntityClass());
    }
}