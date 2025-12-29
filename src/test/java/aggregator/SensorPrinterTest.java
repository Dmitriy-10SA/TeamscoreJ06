package aggregator;

import common.DevicesAndDeviceSensorsInitializer;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.UtilsForTests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SensorPrinterTest {
    private static EntityManagerFactory factory;
    private static SensorPrinter sensorPrinter;

    @BeforeAll
    static void init() {
        factory = UtilsForTests.getEntityManagerFactory();
        UtilsForTests.clearDbData(factory);
        DevicesAndDeviceSensorsInitializer.initialize(factory);
    }

    @AfterAll
    static void clearDbDataAndCloseFactory() {
        UtilsForTests.clearDbData(factory);
        factory.close();
    }

    @Test
    void whenMoreThanOneThreadStartThenThrowException() {
        sensorPrinter = new SensorPrinter(new Scanner(System.in), factory);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(sensorPrinter::start);
        while (!sensorPrinter.isRunning()) {
            Thread.yield();
        }
        assertThrows(IllegalArgumentException.class, () -> sensorPrinter.start());
        sensorPrinter.stop();
        assertFalse(sensorPrinter.isRunning());
        executor.shutdown();
    }

    @Test
    void correctStart() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Scanner scanner = new Scanner(System.in);
        sensorPrinter = new SensorPrinter(scanner, factory);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> sensorPrinter.start());
        while (!sensorPrinter.isRunning()) {
            Thread.yield();
        }
        sensorPrinter.stop();
        executor.shutdown();
        assertFalse(sensorPrinter.isRunning());
        String output = outContent.toString();
        assertFalse(output.isEmpty());
    }
}