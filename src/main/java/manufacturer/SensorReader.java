package manufacturer;

import common.entities.SensorReading;
import jakarta.persistence.EntityManagerFactory;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Читатель датчиков, который записывает в БД данные (в таблицу SensorReading)
 */
public class SensorReader {
    private final SensorReadingGenerator sensorReadingGenerator;
    private final SensorReadingSaver sensorReadingSaver;

    private boolean isRunning;

    public SensorReader(EntityManagerFactory factory) {
        this.sensorReadingGenerator = new SensorReadingGenerator(factory);
        this.sensorReadingSaver = new SensorReadingSaver(factory);
        this.isRunning = true;
    }

    private void readAndSave() {
        try {
            SensorReading sensorReading = sensorReadingGenerator.generate();
            sensorReadingSaver.save(sensorReading);
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 2500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Ошибка в SensorReader: " + e.getMessage());
        }
    }

    public void start() {
        while (isRunning) {
            readAndSave();
        }
    }

    public void stop() {
        isRunning = false;
    }
}