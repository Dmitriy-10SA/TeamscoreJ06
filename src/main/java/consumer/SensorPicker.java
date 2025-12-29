package consumer;

import common.entity.SensorReading;
import consumer.data.creator.AccelerometerSensorDataCreator;
import consumer.data.creator.BarometerSensorDataCreator;
import consumer.data.creator.LightSensorDataCreator;
import consumer.data.creator.LocationSensorDataCreator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Разборщик сообщений от датчиков (сохраняет данные в отдельные таблицы БД)
 */
public class SensorPicker {
    private static final String SELECT_ALL_NOT_SAVED_SENSOR_READINGS = """
            SELECT sr FROM SensorReading sr WHERE savedAt IS NULL
            """;

    private final EntityManagerFactory factory;
    private final AccelerometerSensorDataCreator accelerometerSensorDataCreator;
    private final BarometerSensorDataCreator barometerSensorDataCreator;
    private final LightSensorDataCreator lightSensorDataCreator;
    private final LocationSensorDataCreator locationSensorDataCreator;

    @Getter
    private volatile boolean isRunning;

    public SensorPicker(EntityManagerFactory factory) {
        this.factory = factory;
        this.accelerometerSensorDataCreator = new AccelerometerSensorDataCreator();
        this.barometerSensorDataCreator = new BarometerSensorDataCreator();
        this.lightSensorDataCreator = new LightSensorDataCreator();
        this.locationSensorDataCreator = new LocationSensorDataCreator();
        this.isRunning = false;
    }

    /**
     * Сохранение данных
     */
    private void saveData(SensorReading sensorReading, EntityManager entityManager) throws Exception {
        entityManager.merge(sensorReading.getSensor());
        entityManager.merge(sensorReading.getSensor().getDevice());
        entityManager.persist(
                switch (sensorReading.getSensor().getType()) {
                    case ACCELEROMETER -> accelerometerSensorDataCreator.createAndGetSensorDataForSave(sensorReading);
                    case BAROMETER -> barometerSensorDataCreator.createAndGetSensorDataForSave(sensorReading);
                    case LIGHT -> lightSensorDataCreator.createAndGetSensorDataForSave(sensorReading);
                    case LOCATION -> locationSensorDataCreator.createAndGetSensorDataForSave(sensorReading);
                }
        );
        sensorReading.setSavedAt(LocalDateTime.now());
        entityManager.merge(sensorReading);
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
                    saveData(sensorReading, entityManager);
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
        if (isRunning) {
            throw new IllegalArgumentException("SensorPicker уже запущен!");
        }
        isRunning = true;
        while (isRunning && !Thread.currentThread().isInterrupted()) {
            processPendingSensorReadings();
        }
    }

    /**
     * Остановка разборщика
     */
    public void stop() {
        isRunning = false;
    }
}