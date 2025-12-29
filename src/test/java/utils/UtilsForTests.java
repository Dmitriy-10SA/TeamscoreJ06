package utils;

import common.entity.Device;
import common.entity.Sensor;
import common.entity.SensorReading;
import common.entity.sensor.data.AccelerometerData;
import common.entity.sensor.data.BarometerData;
import common.entity.sensor.data.LightData;
import common.entity.sensor.data.LocationData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Класс с вспомогательными методами для тестов
 */
public class UtilsForTests {
    private UtilsForTests() {
    }

    /**
     * Получение экземпляра EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return new Configuration()
                .configure("hibernate-test.cfg.xml")
                .addAnnotatedClass(AccelerometerData.class)
                .addAnnotatedClass(BarometerData.class)
                .addAnnotatedClass(LightData.class)
                .addAnnotatedClass(LocationData.class)
                .addAnnotatedClass(Device.class)
                .addAnnotatedClass(Sensor.class)
                .addAnnotatedClass(SensorReading.class)
                .buildSessionFactory();
    }

    /**
     * Отчистка данных БД
     */
    public static void clearDbData(EntityManagerFactory factory) {
        try (EntityManager entityManager = factory.createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager
                    .createNativeQuery(Files.readString(Paths.get("src/test/scripts/truncate_test_all.sql")))
                    .executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
