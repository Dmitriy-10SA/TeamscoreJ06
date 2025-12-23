package manufacturer.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.entities.Sensor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для генерации рандомных данных в виде Json для датчика
 */
public class SensorJsonGenerator {
    private static final String LIGHT = "light";
    private static final String AIR_PRESSURE = "air_pressure";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Генерация для типа LIGHT
     */
    private void generateLightSensorJson(Map<String, Object> data) {
        int light = ThreadLocalRandom.current().nextInt(1024);
        data.put(LIGHT, light);
    }

    /**
     * Генерация для типа BAROMETER
     */
    private void generateBarometerSensorJson(Map<String, Object> data) {
        double airPressure = 95000 + ThreadLocalRandom.current().nextDouble() * 10000;
        data.put(AIR_PRESSURE, airPressure);
    }

    /**
     * Генерация для типа LOCATION
     */
    private void generateLocationSensorJson(Map<String, Object> data) {
        double longitude = -180 + ThreadLocalRandom.current().nextDouble() * 360;
        double latitude = -90 + ThreadLocalRandom.current().nextDouble() * 180;
        data.put(LONGITUDE, longitude);
        data.put(LATITUDE, latitude);
    }

    /**
     * Генерация для типа ACCELEROMETER
     */
    private void generateAccelerometerSensorJson(Map<String, Object> data) {
        double x = -20 + ThreadLocalRandom.current().nextDouble() * 40;
        double y = -20 + ThreadLocalRandom.current().nextDouble() * 40;
        double z = -20 + ThreadLocalRandom.current().nextDouble() * 40;
        data.put(X, x);
        data.put(Y, y);
        data.put(Z, z);
    }

    /**
     * Генерация рандомных данных в виде Json по типу датчика
     */
    public String generateSensorJson(Sensor.SensorType type) throws Exception {
        Map<String, Object> data = new HashMap<>();
        switch (type) {
            case LIGHT -> generateLightSensorJson(data);
            case BAROMETER -> generateBarometerSensorJson(data);
            case LOCATION -> generateLocationSensorJson(data);
            case ACCELEROMETER -> generateAccelerometerSensorJson(data);
        }
        return objectMapper.writeValueAsString(data);
    }
}