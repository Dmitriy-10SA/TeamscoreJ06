package consumer.data.creator;

import common.entity.Sensor;
import common.entity.SensorReading;
import common.entity.sensor.data.LocationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocationSensorDataCreatorTest {
    private LocationSensorDataCreator creator;
    private Sensor testSensor;
    private LocalDateTime measuredAt;

    @BeforeEach
    void setUp() {
        creator = new LocationSensorDataCreator();
        testSensor = new Sensor("sensorId", Sensor.SensorType.LOCATION);
        measuredAt = LocalDateTime.now();
    }

    @Test
    void createAndGetSensorDataForSaveProducesCorrectLocationData() {
        String json = "{\"longitude\": 45.123, \"latitude\": -12.456}";
        SensorReading reading = new SensorReading(testSensor, measuredAt, json);
        LocationData locationData = assertDoesNotThrow(() -> creator.createAndGetSensorDataForSave(reading));
        assertNotNull(locationData);
        assertEquals(testSensor, locationData.getSensor());
        assertEquals(measuredAt, locationData.getMeasureAt());
        assertEquals(45.123, locationData.getLongitude());
        assertEquals(-12.456, locationData.getLatitude());
    }

    @Test
    void createAndGetSensorDataForSaveThrowsOnInvalidJson() {
        String invalidJson = "{\"lon\": 45.123, \"lat\": -12.456}";
        SensorReading reading = new SensorReading(testSensor, measuredAt, invalidJson);
        assertThrows(Exception.class, () -> creator.createAndGetSensorDataForSave(reading));
    }

    @Test
    void createAndGetSensorDataForSaveThrowsOnNullJson() {
        SensorReading reading = new SensorReading(testSensor, measuredAt, null);
        assertThrows(Exception.class, () -> creator.createAndGetSensorDataForSave(reading));
    }

    @Test
    void createAndGetSensorDataForSaveHandlesExtraFields() {
        String json = "{\"longitude\": 10.0, \"latitude\": 20.0, \"altitude\": 100}";
        SensorReading reading = new SensorReading(testSensor, measuredAt, json);
        LocationData locationData = assertDoesNotThrow(() -> creator.createAndGetSensorDataForSave(reading));
        assertEquals(10.0, locationData.getLongitude());
        assertEquals(20.0, locationData.getLatitude());
    }
}