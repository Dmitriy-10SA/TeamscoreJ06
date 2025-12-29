package aggregator.data.provider;

import common.entity.sensor.data.BarometerData;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import utils.UtilsForTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BarometerSensorDataProviderTest {
    private final EntityManagerFactory factory = UtilsForTests.getEntityManagerFactory();
    private final BarometerSensorDataProvider provider = new BarometerSensorDataProvider(factory);

    @Test
    void shouldReturnCorrectEntityName() {
        assertEquals("BarometerData", provider.getEntityName());
    }

    @Test
    void shouldReturnCorrectEntityClass() {
        assertEquals(BarometerData.class, provider.getEntityClass());
    }
}