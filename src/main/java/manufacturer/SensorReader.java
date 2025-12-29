package manufacturer;

import common.entity.SensorReading;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import manufacturer.generator.SensorReadingGenerator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Читатель датчиков, который записывает в БД данные (в таблицу SensorReading)
 */
public class SensorReader {
    private static final int MIN_SLEEP_DELAY_MS = 50;
    private static final int MAX_SLEEP_DELAY_MS = 800;

    private final SensorReadingGenerator sensorReadingGenerator;
    private final SensorReadingSaver sensorReadingSaver;

    @Getter
    private volatile boolean isRunning;

    public SensorReader(EntityManagerFactory factory) {
        this.sensorReadingGenerator = new SensorReadingGenerator(factory);
        this.sensorReadingSaver = new SensorReadingSaver(factory);
        this.isRunning = false;
    }

    /**
     * Чтение (имитация, на самом деле генерируем данные сами) и запись данных в БД (в таблицу SensorReading)
     */
    private void readAndSave() {
        try {
            SensorReading sensorReading = sensorReadingGenerator.generate();
            sensorReadingSaver.save(sensorReading);
            Thread.sleep(ThreadLocalRandom.current().nextInt(MIN_SLEEP_DELAY_MS, MAX_SLEEP_DELAY_MS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            isRunning = false;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в SensorReader: " + e.getMessage());
        }
    }

    /**
     * Запуск читателя датчиков
     */
    public void start() {
        if (isRunning) {
            throw new IllegalArgumentException("SensorReader уже запущен!");
        }
        isRunning = true;
        while (isRunning && !Thread.currentThread().isInterrupted()) {
            readAndSave();
        }
    }

    /**
     * Остановка читателя датчиков
     */
    public void stop() {
        isRunning = false;
    }
}