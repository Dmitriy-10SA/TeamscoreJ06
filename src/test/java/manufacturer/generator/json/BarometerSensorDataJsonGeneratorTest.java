package manufacturer.generator.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BarometerSensorDataJsonGeneratorTest {
    private final BarometerSensorDataJsonGenerator generator = new BarometerSensorDataJsonGenerator();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generateProducesValidJsonWithinExpectedRange() throws Exception {
        for (int i = 0; i < 10_000; i++) {
            String json = assertDoesNotThrow(generator::generate);
            assertNotNull(json);
            JsonNode node = objectMapper.readTree(json);
            assertTrue(node.has("air_pressure"));
            double pressure = node.get("air_pressure").asDouble();
            assertTrue(pressure >= 95_000.0 && pressure <= 105_000.0);
        }
    }
}