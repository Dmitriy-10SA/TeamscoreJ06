package consumer.data.creator;

import common.entity.Sensor;
import common.entity.SensorReading;
import common.entity.sensor.data.BarometerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BarometerSensorDataCreatorTest {
    private BarometerSensorDataCreator creator;
    private Sensor testSensor;
    private LocalDateTime measuredAt;

    @BeforeEach
    void setUp() {
        creator = new BarometerSensorDataCreator();
        testSensor = new Sensor("sensorId", Sensor.SensorType.BAROMETER);
        measuredAt = LocalDateTime.now();
    }

    @Test
    void createAndGetSensorDataForSaveProducesCorrectBarometerData() {
        String json = "{\"air_pressure\": 1013.25}";
        SensorReading reading = new SensorReading(testSensor, measuredAt, json);
        BarometerData data = assertDoesNotThrow(() -> creator.createAndGetSensorDataForSave(reading));
        assertNotNull(data);
        assertEquals(testSensor, data.getSensor());
        assertEquals(measuredAt, data.getMeasureAt());
        assertEquals(1013.25, data.getAirPressure());
    }

    @Test
    void createAndGetSensorDataForSaveThrowsOnInvalidJson() {
        String invalidJson = "{\"pressure\": 1013.25}";
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
        String json = "{\"air_pressure\": 1000.0, \"temp\": 25}";
        SensorReading reading = new SensorReading(testSensor, measuredAt, json);
        BarometerData data = assertDoesNotThrow(() -> creator.createAndGetSensorDataForSave(reading));
        assertEquals(1000.0, data.getAirPressure());
    }
}