package aggregator.data.provider;

import common.entity.sensor.data.LightData;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import utils.UtilsForTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LightSensorDataProviderTest {
    private final EntityManagerFactory factory = UtilsForTests.getEntityManagerFactory();
    private final LightSensorDataProvider provider = new LightSensorDataProvider(factory);

    @Test
    void shouldReturnCorrectEntityName() {
        assertEquals("LightData", provider.getEntityName());
    }

    @Test
    void shouldReturnCorrectEntityClass() {
        assertEquals(LightData.class, provider.getEntityClass());
    }
}