package consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.entities.Sensor;
import common.entities.SensorReading;
import common.entities.sensor.data.AccelerometerData;
import common.entities.sensor.data.BarometerData;
import common.entities.sensor.data.LightData;
import common.entities.sensor.data.LocationData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Разборщик сообщений от датчиков (сохраняет данные в отдельные таблицы БД)
 */
public class SensorPicker {
    private static final String SELECT_ALL_NOT_SAVED_SENSOR_READINGS = """
            SELECT sr FROM SensorReading sr WHERE savedAt IS NULL
            """;

    private final EntityManagerFactory factory;
    private final SensorDataSaver sensorDataSaver;

    private boolean isRunning;

    public SensorPicker(EntityManagerFactory factory) {
        this.factory = factory;
        this.sensorDataSaver = new SensorDataSaver();
        this.isRunning = true;
    }

    /**
     * Обрабатываем все ожидающие SensorReading и обновляем информацию в таблицах Device и Sensor
     */
    private void processPendingSensorReadings() {
        try (EntityManager entityManager = factory.createEntityManager()) {
            try {
                entityManager.getTransaction().begin();
                List<SensorReading> sensorReadings = entityManager
                        .createQuery(SELECT_ALL_NOT_SAVED_SENSOR_READINGS, SensorReading.class)
                        .getResultList();
                for (SensorReading sensorReading : sensorReadings) {
                    entityManager.merge(sensorReading.getSensor());
                    entityManager.merge(sensorReading.getSensor().getDevice());
                    sensorDataSaver.saveSensorData(sensorReading, entityManager);
                    sensorReading.setSavedAt(LocalDateTime.now());
                    entityManager.merge(sensorReading);
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RuntimeException("Ошибка в SensorPicker: " + e.getMessage());
            }
        }
    }

    /**
     * Запуск разборщика
     */
    public void start() {
        while (isRunning) {
            processPendingSensorReadings();
        }
    }

    /**
     * Остановка разборщика
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Класс для сохранения данных датчика в нужную таблицу БД из таблицы SensorReading
     */
    private static class SensorDataSaver {
        private static final String LIGHT = "light";
        private static final String AIR_PRESSURE = "air_pressure";
        private static final String LONGITUDE = "longitude";
        private static final String LATITUDE = "latitude";
        private static final String X = "x";
        private static final String Y = "y";
        private static final String Z = "z";

        private final ObjectMapper objectMapper = new ObjectMapper();

        /**
         * Сохранение данных датчика в нужную таблицу БД из таблицы SensorReading
         */
        private void saveSensorData(SensorReading sensorReading, EntityManager entityManager) throws Exception {
            Map<String, Object> data = objectMapper.readValue(sensorReading.getValueJson(), Map.class);
            Sensor sensor = sensorReading.getSensor();
            LocalDateTime measuredAt = sensorReading.getMeasuredAt();
            switch (sensor.getType()) {
                case LIGHT -> {
                    int light = ((Number) data.get(LIGHT)).intValue();
                    LightData lightData = new LightData(sensor, measuredAt, light);
                    entityManager.persist(lightData);
                }
                case ACCELEROMETER -> {
                    double x = ((Number) data.get(X)).doubleValue();
                    double y = ((Number) data.get(Y)).doubleValue();
                    double z = ((Number) data.get(Z)).doubleValue();
                    AccelerometerData accelerometerData = new AccelerometerData(sensor, measuredAt, x, y, z);
                    entityManager.persist(accelerometerData);
                }
                case BAROMETER -> {
                    double airPressure = ((Number) data.get(AIR_PRESSURE)).doubleValue();
                    BarometerData barometerData = new BarometerData(sensor, measuredAt, airPressure);
                    entityManager.persist(barometerData);
                }
                case LOCATION -> {
                    double longitude = ((Number) data.get(LONGITUDE)).doubleValue();
                    double latitude = ((Number) data.get(LATITUDE)).doubleValue();
                    LocationData locationData = new LocationData(sensor, measuredAt, longitude, latitude);
                    entityManager.persist(locationData);
                }
            }
        }
    }
}