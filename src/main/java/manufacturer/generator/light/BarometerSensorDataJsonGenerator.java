package manufacturer.generator.light;

import common.entity.sensor.data.BarometerData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для генерации рандомных данных датчика типа BAROMETER в виде Json
 *
 * @see SensorDataJsonGenerator
 */
public class BarometerSensorDataJsonGenerator extends SensorDataJsonGenerator<BarometerData> {
    private static final String AIR_PRESSURE = "air_pressure";

    private static final double MIN_AIR_PRESSURE_PA = 95_000.0;
    private static final double MAX_AIR_PRESSURE_PA = 105_000.0;

    @Override
    public String generate() throws Exception {
        Map<String, Double> data = new HashMap<>();
        double airPressure = ThreadLocalRandom.current().nextDouble(MIN_AIR_PRESSURE_PA, MAX_AIR_PRESSURE_PA);
        data.put(AIR_PRESSURE, airPressure);
        return objectMapper.writeValueAsString(data);
    }
}
