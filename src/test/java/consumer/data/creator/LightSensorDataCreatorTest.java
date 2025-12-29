package consumer.data.creator;

import common.entity.Sensor;
import common.entity.SensorReading;
import common.entity.sensor.data.LightData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LightSensorDataCreatorTest {

    private LightSensorDataCreator creator;
    private Sensor testSensor;
    private LocalDateTime measuredAt;

    @BeforeEach
    void setUp() {
        creator = new LightSensorDataCreator();
        testSensor = new Sensor("sensorId", Sensor.SensorType.LIGHT);
        measuredAt = LocalDateTime.now();
    }

    @Test
    void createAndGetSensorDataForSaveProducesCorrectLightData() {
        String json = "{\"light\": 350}";
        SensorReading reading = new SensorReading(testSensor, measuredAt, json);
        LightData lightData = assertDoesNotThrow(() -> creator.createAndGetSensorDataForSave(reading));
        assertNotNull(lightData);
        assertEquals(testSensor, lightData.getSensor());
        assertEquals(measuredAt, lightData.getMeasureAt());
        assertEquals(350, lightData.getLight());
    }

    @Test
    void createAndGetSensorDataForSaveThrowsOnInvalidJson() {
        String invalidJson = "{\"brightness\": 350}";
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
        String json = "{\"light\": 200, \"extra\": 123}";
        SensorReading reading = new SensorReading(testSensor, measuredAt, json);
        LightData lightData = assertDoesNotThrow(() -> creator.createAndGetSensorDataForSave(reading));
        assertEquals(200, lightData.getLight());
    }
}