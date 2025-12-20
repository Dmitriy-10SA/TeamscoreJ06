import entities.Device;
import entities.Sensor;
import entities.SensorReading;
import entities.sensor.data.AccelerometerData;
import entities.sensor.data.BarometerData;
import entities.sensor.data.LightData;
import entities.sensor.data.LocationData;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;

class SensorsMain {
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

        }
    }
}
