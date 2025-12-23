package consumer;

import common.entities.SensorReading;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

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
}