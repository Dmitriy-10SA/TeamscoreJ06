package manufacturer.generator;

import common.entities.Sensor;
import common.entities.SensorReading;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс для генерации показаний датчиков
 */
public class SensorReadingGenerator {
    private static final String SELECT_ALL_SENSORS = "SELECT s FROM Sensor s";

    private final SensorJsonGenerator sensorJsonGenerator;
    private final List<Sensor> sensors;

    public SensorReadingGenerator(EntityManagerFactory entityManagerFactory) {
        this.sensorJsonGenerator = new SensorJsonGenerator();
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
        String jsonValue = sensorJsonGenerator.generateSensorJson(sensor.getType());
        return new SensorReading(sensor, measuredAt, jsonValue);
    }
}