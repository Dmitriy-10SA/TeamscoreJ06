package manufacturer.generator;

import common.entity.Sensor;
import common.entity.SensorReading;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import manufacturer.generator.json.AccelerometerSensorDataJsonGenerator;
import manufacturer.generator.json.BarometerSensorDataJsonGenerator;
import manufacturer.generator.json.LightSensorDataJsonGenerator;
import manufacturer.generator.json.LocationSensorDataJsonGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для генерации показаний датчиков
 */
public class SensorReadingGenerator {
    private static final String SELECT_ALL_SENSORS = "SELECT s FROM Sensor s";

    private final AccelerometerSensorDataJsonGenerator accelerometerSensorDataJsonGenerator;
    private final BarometerSensorDataJsonGenerator barometerSensorDataJsonGenerator;
    private final LightSensorDataJsonGenerator lightSensorDataJsonGenerator;
    private final LocationSensorDataJsonGenerator locationSensorDataJsonGenerator;
    private final List<Sensor> sensors;

    public SensorReadingGenerator(EntityManagerFactory entityManagerFactory) {
        this.accelerometerSensorDataJsonGenerator = new AccelerometerSensorDataJsonGenerator();
        this.barometerSensorDataJsonGenerator = new BarometerSensorDataJsonGenerator();
        this.lightSensorDataJsonGenerator = new LightSensorDataJsonGenerator();
        this.locationSensorDataJsonGenerator = new LocationSensorDataJsonGenerator();
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            this.sensors = entityManager.createQuery(SELECT_ALL_SENSORS, Sensor.class).getResultList();
        }
    }

    /**
     * Генерирует и сохраняет показание случайного датчика
     */
    public SensorReading generate() throws Exception {
        Sensor sensor = sensors.get(ThreadLocalRandom.current().nextInt(sensors.size()));
        LocalDateTime measuredAt = LocalDateTime.now();
        String jsonValue = switch (sensor.getType()) {
            case ACCELEROMETER -> accelerometerSensorDataJsonGenerator.generate();
            case BAROMETER -> barometerSensorDataJsonGenerator.generate();
            case LIGHT -> lightSensorDataJsonGenerator.generate();
            case LOCATION -> locationSensorDataJsonGenerator.generate();
        };
        return new SensorReading(sensor, measuredAt, jsonValue);
    }
}