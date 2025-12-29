package manufacturer.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.DevicesAndDeviceSensorsInitializer;
import common.entity.Sensor;
import common.entity.SensorReading;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.UtilsForTests;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SensorReadingGeneratorTest {
    private static EntityManagerFactory factory;
    private static SensorReadingGenerator sensorReadingGenerator;

    @BeforeAll
    static void init() {
        factory = UtilsForTests.getEntityManagerFactory();
        DevicesAndDeviceSensorsInitializer.initialize(factory);
        sensorReadingGenerator = new SensorReadingGenerator(factory);
    }

    @AfterAll
    static void clearDbDataAndCloseFactory() {
        UtilsForTests.clearDbData(factory);
        factory.close();
    }

    //просто проверяем, что генерируется верный SensorReading с существующим типом датчика, JSON для
    //этого типа датчика и SensorReading содержит null в поле savedAt
    //т.к. по сути он просто вызывает генераторы JSON, к которым уже есть тесты, и возвращает SensorReading
    @Test
    void generateCorrectGeneratedSensorReading() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<Sensor.SensorType> types = Arrays.stream(Sensor.SensorType.values()).collect(Collectors.toSet());
        for (int i = 0; i < 10_000; i++) {
            SensorReading sensorReading = assertDoesNotThrow(sensorReadingGenerator::generate);
            assertNotNull(sensorReading);
            assertNotNull(sensorReading.getSensor());
            assertNotNull(sensorReading.getSensor().getDevice());
            Sensor.SensorType generatedType = sensorReading.getSensor().getType();
            assertTrue(types.contains(generatedType));
            assertNotNull(sensorReading.getValueJson());
            assertNotNull(sensorReading.getMeasuredAt());
            assertNull(sensorReading.getSavedAt());
            JsonNode node = objectMapper.readTree(sensorReading.getValueJson());
            switch (generatedType) {
                case ACCELEROMETER -> {
                    assertTrue(node.has("x"));
                    assertTrue(node.has("y"));
                    assertTrue(node.has("z"));
                }
                case LIGHT -> assertTrue(node.has("light"));
                case BAROMETER -> assertTrue(node.has("air_pressure"));
                case LOCATION -> {
                    assertTrue(node.has("longitude"));
                    assertTrue(node.has("latitude"));
                }
            }
        }
    }
}