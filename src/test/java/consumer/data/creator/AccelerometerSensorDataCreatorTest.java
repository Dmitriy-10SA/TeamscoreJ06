package consumer.data.creator;

import common.entity.Sensor;
import common.entity.SensorReading;
import common.entity.sensor.data.AccelerometerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccelerometerSensorDataCreatorTest {
    private AccelerometerSensorDataCreator creator;
    private Sensor testSensor;
    private LocalDateTime measuredAt;

    @BeforeEach
    void setUp() {
        creator = new AccelerometerSensorDataCreator();
        testSensor = new Sensor("sensorId", Sensor.SensorType.ACCELEROMETER);
        measuredAt = LocalDateTime.now();
    }

    @Test
    void createAndGetSensorDataForSaveProducesCorrectAccelerometerData() {
        String json = "{\"x\": 0.1, \"y\": -0.2, \"z\": 9.81}";
        SensorReading reading = new SensorReading(testSensor, measuredAt, json);
        AccelerometerData data = assertDoesNotThrow(() -> creator.createAndGetSensorDataForSave(reading));
        assertNotNull(data);
        assertEquals(testSensor, data.getSensor());
        assertEquals(measuredAt, data.getMeasureAt());
        assertEquals(0.1, data.getX());
        assertEquals(-0.2, data.getY());
        assertEquals(9.81, data.getZ());
    }

    @Test
    void createAndGetSensorDataForSaveThrowsOnInvalidJson() {
        String invalidJson = "{\"x\": 0.1, \"y\": 0.2}";
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
        String json = "{\"x\": 1.0, \"y\": 2.0, \"z\": 3.0, \"w\": 4.0}";
        SensorReading reading = new SensorReading(testSensor, measuredAt, json);
        AccelerometerData data = assertDoesNotThrow(() -> creator.createAndGetSensorDataForSave(reading));
        assertEquals(1.0, data.getX());
        assertEquals(2.0, data.getY());
        assertEquals(3.0, data.getZ());
    }
}