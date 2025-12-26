package common;

import common.entity.Device;
import common.entity.Sensor;
import common.entity.SensorReading;
import common.entity.sensor.data.AccelerometerData;
import common.entity.sensor.data.BarometerData;
import common.entity.sensor.data.LightData;
import common.entity.sensor.data.LocationData;
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
