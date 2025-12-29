package manufacturer.generator.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SensorDataJsonGeneratorTest {
    private final SensorDataJsonGenerator<?> helloWorldGenerator = new SensorDataJsonGenerator<>() {
        @Override
        public String generate() {
            return "Hello World!";
        }
    };

    private final SensorDataJsonGenerator<?> emptyGenerator = new SensorDataJsonGenerator<>() {
        @Override
        public String generate() {
            return "";
        }
    };

    private final SensorDataJsonGenerator<?> nullGenerator = new SensorDataJsonGenerator<>() {
        @Override
        public String generate() {
            return null;
        }
    };

    private final SensorDataJsonGenerator<?> jsonGenerator = new SensorDataJsonGenerator<>() {
        @Override
        public String generate() {
            return "{\"sensor\": \"temperature\", \"value\": 23.5}";
        }
    };

    private final SensorDataJsonGenerator<?> throwsExceptionGenerator = new SensorDataJsonGenerator<>() {
        @Override
        public String generate() {
            throw new RuntimeException("Test exception");
        }
    };

    @Test
    void testGenerateHelloWorld() {
        String generatedJson = assertDoesNotThrow(helloWorldGenerator::generate);
        assertEquals("Hello World!", generatedJson);
    }

    @Test
    void testGenerateEmpty() {
        String generatedJson = assertDoesNotThrow(emptyGenerator::generate);
        assertEquals("", generatedJson);
    }

    @Test
    void testGenerateNull() {
        String generatedJson = assertDoesNotThrow(nullGenerator::generate);
        assertNull(generatedJson);
    }

    @Test
    void testGenerateJson() {
        String generatedJson = assertDoesNotThrow(jsonGenerator::generate);
        assertEquals("{\"sensor\": \"temperature\", \"value\": 23.5}", generatedJson);
    }

    @Test
    void testGenerateThrowsException() {
        assertThrows(RuntimeException.class, throwsExceptionGenerator::generate);
    }
}