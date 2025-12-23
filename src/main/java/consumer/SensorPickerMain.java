package consumer;

import common.entities.Device;
import common.entities.Sensor;
import common.entities.SensorReading;
import common.entities.sensor.data.AccelerometerData;
import common.entities.sensor.data.BarometerData;
import common.entities.sensor.data.LightData;
import common.entities.sensor.data.LocationData;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;

public class SensorPickerMain {
    public static void main(String[] args) {
        EntityManagerFactory factory = new Configuration()
                .addAnnotatedClass(AccelerometerData.class)
                .addAnnotatedClass(BarometerData.class)
                .addAnnotatedClass(LightData.class)
                .addAnnotatedClass(LocationData.class)
                .addAnnotatedClass(Device.class)
                .addAnnotatedClass(Sensor.class)
                .addAnnotatedClass(SensorReading.class)
                .buildSessionFactory();
        try (factory) {
            SensorPicker sensorPicker = new SensorPicker(factory);
            sensorPicker.start();
        }
    }
}
