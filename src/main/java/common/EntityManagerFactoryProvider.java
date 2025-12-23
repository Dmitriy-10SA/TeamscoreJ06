package common;

import common.entities.Device;
import common.entities.Sensor;
import common.entities.SensorReading;
import common.entities.sensor.data.AccelerometerData;
import common.entities.sensor.data.BarometerData;
import common.entities.sensor.data.LightData;
import common.entities.sensor.data.LocationData;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;

/**
 * Класс для получения экземпляра EntityManagerFactory (Singleton)
 */
public class EntityManagerFactoryProvider {
    private static final EntityManagerFactory FACTORY = new Configuration()
            .addAnnotatedClass(AccelerometerData.class)
            .addAnnotatedClass(BarometerData.class)
            .addAnnotatedClass(LightData.class)
            .addAnnotatedClass(LocationData.class)
            .addAnnotatedClass(Device.class)
            .addAnnotatedClass(Sensor.class)
            .addAnnotatedClass(SensorReading.class)
            .buildSessionFactory();

    private EntityManagerFactoryProvider() {
    }

    /**
     * Получение экземпляра EntityManagerFactory (Singleton)
     */
    public static EntityManagerFactory getFactory() {
        return FACTORY;
    }
}
