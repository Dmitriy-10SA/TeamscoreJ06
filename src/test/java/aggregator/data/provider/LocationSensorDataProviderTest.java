package aggregator.data.provider;

import common.entity.sensor.data.LocationData;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import utils.UtilsForTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationSensorDataProviderTest {
    private final EntityManagerFactory factory = UtilsForTests.getEntityManagerFactory();
    private final LocationSensorDataProvider provider = new LocationSensorDataProvider(factory);

    @Test
    void shouldReturnCorrectEntityName() {
        assertEquals("LocationData", provider.getEntityName());
    }

    @Test
    void shouldReturnCorrectEntityClass() {
        assertEquals(LocationData.class, provider.getEntityClass());
    }
}