package manufacturer.generator.light;

import common.entity.sensor.data.LightData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для генерации рандомных данных датчика типа LIGHT в виде Json
 *
 * @see SensorDataJsonGenerator
 */
public class LightSensorDataJsonGenerator extends SensorDataJsonGenerator<LightData> {
    private static final String LIGHT = "light";

    private static final int MAX_LIGHT_LEVEL = 1024;

    @Override
    public String generate() throws Exception {
        Map<String, Integer> data = new HashMap<>();
        int light = ThreadLocalRandom.current().nextInt(MAX_LIGHT_LEVEL);
        data.put(LIGHT, light);
        return objectMapper.writeValueAsString(data);
    }
}
