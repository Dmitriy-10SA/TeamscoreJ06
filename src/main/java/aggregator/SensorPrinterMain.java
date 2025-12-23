package aggregator;

import common.entities.Device;
import common.entities.Sensor;
import common.entities.SensorReading;
import common.entities.sensor.data.AccelerometerData;
import common.entities.sensor.data.BarometerData;
import common.entities.sensor.data.LightData;
import common.entities.sensor.data.LocationData;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;

import java.util.Scanner;

public class SensorPrinterMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EntityManagerFactory factory = new Configuration()
                .addAnnotatedClass(AccelerometerData.class)
                .addAnnotatedClass(BarometerData.class)
                .addAnnotatedClass(LightData.class)
                .addAnnotatedClass(LocationData.class)
                .addAnnotatedClass(Device.class)
                .addAnnotatedClass(Sensor.class)
                .addAnnotatedClass(SensorReading.class)
                .buildSessionFactory();
        try (scanner; factory) {
            SensorPrinter sensorPrinter = new SensorPrinter(scanner, factory);
            sensorPrinter.start();
        }
    }
}
