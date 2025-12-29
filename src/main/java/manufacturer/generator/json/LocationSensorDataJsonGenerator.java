package manufacturer.generator.json;

import common.entity.sensor.data.LocationData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для генерации рандомных данных датчика типа LOCATION в виде Json
 *
 * @see SensorDataJsonGenerator
 */
public class LocationSensorDataJsonGenerator extends SensorDataJsonGenerator<LocationData> {
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";

    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;

    @Override
    public String generate() throws Exception {
        Map<String, Double> data = new HashMap<>();
        double longitude = ThreadLocalRandom.current().nextDouble(MIN_LONGITUDE, MAX_LONGITUDE);
        double latitude = ThreadLocalRandom.current().nextDouble(MIN_LATITUDE, MAX_LATITUDE);
        data.put(LONGITUDE, longitude);
        data.put(LATITUDE, latitude);
        return objectMapper.writeValueAsString(data);
    }
}
