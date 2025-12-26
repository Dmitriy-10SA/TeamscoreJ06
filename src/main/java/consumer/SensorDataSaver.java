package consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.entity.Sensor;
import common.entity.SensorReading;
import common.entity.sensor.data.AccelerometerData;
import common.entity.sensor.data.BarometerData;
import common.entity.sensor.data.LightData;
import common.entity.sensor.data.LocationData;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Класс для сохранения данных датчика в нужную таблицу БД из таблицы SensorReading
 */
public class SensorDataSaver {
    private static final String LIGHT = "light";
    private static final String AIR_PRESSURE = "air_pressure";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Сохранение данных LIGHT датчика в таблицу БД LightData из таблицы SensorReading
     */
    private void saveLightData(
            Map<String, Object> data,
            Sensor sensor,
            LocalDateTime measuredAt,
            EntityManager entityManager
    ) {
        int light = ((Number) data.get(LIGHT)).intValue();
        LightData lightData = new LightData(sensor, measuredAt, light);
        entityManager.persist(lightData);
    }

    /**
     * Сохранение данных ACCELEROMETER датчика в таблицу БД AccelerometerData из таблицы SensorReading
     */
    private void saveAccelerometerData(
            Map<String, Object> data,
            Sensor sensor,
            LocalDateTime measuredAt,
            EntityManager entityManager
    ) {
        double x = ((Number) data.get(X)).doubleValue();
        double y = ((Number) data.get(Y)).doubleValue();
        double z = ((Number) data.get(Z)).doubleValue();
        AccelerometerData accelerometerData = new AccelerometerData(sensor, measuredAt, x, y, z);
        entityManager.persist(accelerometerData);
    }

    /**
     * Сохранение данных BAROMETER датчика в таблицу БД BarometerData из таблицы SensorReading
     */
    private void saveBarometerData(
            Map<String, Object> data,
            Sensor sensor,
            LocalDateTime measuredAt,
            EntityManager entityManager
    ) {
        double airPressure = ((Number) data.get(AIR_PRESSURE)).doubleValue();
        BarometerData barometerData = new BarometerData(sensor, measuredAt, airPressure);
        entityManager.persist(barometerData);
    }

    /**
     * Сохранение данных LOCATION датчика в таблицу БД LocationData из таблицы SensorReading
     */
    private void saveLocationData(
            Map<String, Object> data,
            Sensor sensor,
            LocalDateTime measuredAt,
            EntityManager entityManager
    ) {
        double longitude = ((Number) data.get(LONGITUDE)).doubleValue();
        double latitude = ((Number) data.get(LATITUDE)).doubleValue();
        LocationData locationData = new LocationData(sensor, measuredAt, longitude, latitude);
        entityManager.persist(locationData);
    }

    /**
     * Сохранение данных датчика в нужную таблицу БД из таблицы SensorReading
     */
    public void saveSensorData(SensorReading sensorReading, EntityManager entityManager) throws Exception {
        Map<String, Object> data = objectMapper.readValue(sensorReading.getValueJson(), Map.class);
        Sensor sensor = sensorReading.getSensor();
        LocalDateTime measuredAt = sensorReading.getMeasuredAt();
        switch (sensor.getType()) {
            case LIGHT -> saveLightData(data, sensor, measuredAt, entityManager);
            case ACCELEROMETER -> saveAccelerometerData(data, sensor, measuredAt, entityManager);
            case BAROMETER -> saveBarometerData(data, sensor, measuredAt, entityManager);
            case LOCATION -> saveLocationData(data, sensor, measuredAt, entityManager);
        }
    }
}
