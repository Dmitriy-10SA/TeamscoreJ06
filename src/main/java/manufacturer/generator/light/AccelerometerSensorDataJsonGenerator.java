package manufacturer.generator.light;

import common.entity.sensor.data.AccelerometerData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для генерации рандомных данных датчика типа ACCELEROMETER в виде Json
 *
 * @see SensorDataJsonGenerator
 */
public class AccelerometerSensorDataJsonGenerator extends SensorDataJsonGenerator<AccelerometerData> {
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";

    private static final double MIN_ACCELERATION = -20.0;
    private static final double MAX_ACCELERATION = 20.0;

    @Override
    public String generate() throws Exception {
        Map<String, Double> data = new HashMap<>();
        double x = ThreadLocalRandom.current().nextDouble(MIN_ACCELERATION, MAX_ACCELERATION);
        double y = ThreadLocalRandom.current().nextDouble(MIN_ACCELERATION, MAX_ACCELERATION);
        double z = ThreadLocalRandom.current().nextDouble(MIN_ACCELERATION, MAX_ACCELERATION);
        data.put(X, x);
        data.put(Y, y);
        data.put(Z, z);
        return objectMapper.writeValueAsString(data);
    }
}
