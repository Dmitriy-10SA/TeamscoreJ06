package manufacturer.generator.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationSensorDataJsonGeneratorTest {
    private final LocationSensorDataJsonGenerator generator = new LocationSensorDataJsonGenerator();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void generateProducesValidJsonWithinExpectedRange() throws Exception {
        for (int i = 0; i < 10_000; i++) {
            String json = assertDoesNotThrow(generator::generate);
            assertNotNull(json);
            JsonNode node = objectMapper.readTree(json);
            assertTrue(node.has("longitude"));
            assertTrue(node.has("latitude"));
            double longitude = node.get("longitude").asDouble();
            double latitude = node.get("latitude").asDouble();
            assertTrue(longitude >= -180.0 && longitude <= 180.0);
            assertTrue(latitude >= -90.0 && latitude <= 90.0);
        }
    }
}