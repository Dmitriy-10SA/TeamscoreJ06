package consumer.data.creator;

import common.entity.Sensor;
import common.entity.SensorReading;
import common.entity.sensor.data.SensorData;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SensorDataCreatorTest {
    private static final LocalDateTime NOW = LocalDateTime.now();

    private static class TestSensorData extends SensorData {
        protected TestSensorData() {
            super(new Sensor("testId", Sensor.SensorType.LIGHT), NOW);
        }
    }

    private final SensorDataCreator<?> nullCreator = new SensorDataCreator<>() {
        @Override
        public SensorData createAndGetSensorDataForSave(SensorReading sensorReading) {
            return null;
        }
    };

    private final SensorDataCreator<TestSensorData> testCreator = new SensorDataCreator<>() {
        @Override
        public TestSensorData createAndGetSensorDataForSave(SensorReading sensorReading) {
            return new TestSensorData();
        }
    };

    private final SensorDataCreator<?> exceptionCreator = new SensorDataCreator<>() {
        @Override
        public SensorData createAndGetSensorDataForSave(SensorReading sensorReading) throws Exception {
            throw new Exception("Test exception");
        }
    };

    @Test
    void createAndGetSensorDataForSaveReturnsNull() throws Exception {
        SensorData data = nullCreator.createAndGetSensorDataForSave(null);
        assertNull(data);
    }

    @Test
    void createAndGetTestSensorData() throws Exception {
        SensorReading reading = new SensorReading(null, LocalDateTime.now(), "{}");
        TestSensorData data = testCreator.createAndGetSensorDataForSave(reading);
        assertNotNull(data);
        assertEquals(Sensor.SensorType.LIGHT, data.getSensor().getType());
        assertEquals("testId", data.getSensor().getId());
        assertEquals(NOW, data.getMeasureAt());
    }

    @Test
    void createAndGetSensorDataForSaveThrowsException() {
        Exception exception = assertThrows(
                Exception.class,
                () -> exceptionCreator.createAndGetSensorDataForSave(null)
        );
        assertEquals("Test exception", exception.getMessage());
    }
}