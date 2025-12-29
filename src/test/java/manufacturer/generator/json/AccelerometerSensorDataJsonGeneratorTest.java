package manufacturer.generator.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccelerometerSensorDataJsonGeneratorTest {
    private final AccelerometerSensorDataJsonGenerator generator = new AccelerometerSensorDataJsonGenerator();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generateProducesValidJsonWithinExpectedRange() throws Exception {
        for (int i = 0; i < 10_000; i++) {
            String json = assertDoesNotThrow(generator::generate);
            assertNotNull(json);
            JsonNode node = objectMapper.readTree(json);
            assertTrue(node.has("x"));
            assertTrue(node.has("y"));
            assertTrue(node.has("z"));
            double x = node.get("x").asDouble();
            double y = node.get("y").asDouble();
            double z = node.get("z").asDouble();
            assertTrue(x >= -20.0 && x <= 20.0);
            assertTrue(y >= -20.0 && y <= 20.0);
            assertTrue(z >= -20.0 && z <= 20.0);
        }
    }
}