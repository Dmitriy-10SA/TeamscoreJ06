package manufacturer.generator.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LightSensorDataJsonGeneratorTest {
    private final LightSensorDataJsonGenerator generator = new LightSensorDataJsonGenerator();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generateProducesValidJsonWithinExpectedRange() throws Exception {
        for (int i = 0; i < 10_000; i++) {
            String json = assertDoesNotThrow(generator::generate);
            assertNotNull(json);
            JsonNode node = objectMapper.readTree(json);
            assertTrue(node.has("light"));
            int light = node.get("light").asInt();
            assertTrue(light >= 0 && light < 1024);
        }
    }
}