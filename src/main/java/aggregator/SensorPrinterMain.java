package aggregator;

import common.DevicesAndDeviceSensorsInitializer;
import common.EntityManagerFactoryProvider;
import jakarta.persistence.EntityManagerFactory;

import java.util.Scanner;

public class SensorPrinterMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EntityManagerFactory factory = EntityManagerFactoryProvider.getFactory();
        try (scanner; factory) {
            DevicesAndDeviceSensorsInitializer.initialize(factory);
            SensorPrinter sensorPrinter = new SensorPrinter(scanner, factory);
            sensorPrinter.start();
        }
    }
}
