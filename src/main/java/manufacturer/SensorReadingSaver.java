package manufacturer;

import common.entities.SensorReading;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Класс для сохранения показаний датчиков в БД (в таблицу SensorReading)
 */
public class SensorReadingSaver {
    private final EntityManagerFactory entityManagerFactory;

    public SensorReadingSaver(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Сохраняет показание датчика в БД (в таблицу SensorReading)
     */
    public void save(SensorReading sensorReading) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            try {
                entityManager.getTransaction().begin();
                entityManager.persist(sensorReading);
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw e;
            }
        }
    }
}

