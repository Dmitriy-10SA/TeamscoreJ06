package common;

import common.entities.Device;
import common.entities.Sensor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Класс для заполнения БД устройствами и датчиками в этих устройствах
 */
public class DevicesAndDeviceSensorsInitializer {
    private static final String[] DEVICE_NAMES = {
            "SmallRicePro99",
            "УмДомМой",
            "SmartPhone2024",
            "IoTDevice01",
            "SensorHub"
    };

    private static final String SELECT_SENSORS_BY_DEVICE_ID = "SELECT s FROM Sensor s WHERE s.device.id = :deviceId";
    private static final String SELECT_DEVICE_BY_NAME = "SELECT d FROM Device d WHERE d.name = :name";

    private static final String NAME = "name";
    private static final String DEVICE_ID = "deviceId";

    /**
     * Добавляем у устройства датчики (добавляем все типы)
     */
    private static void initializeDeviceSensors(EntityManager entityManager, Device device) {
        List<Sensor> deviceSensors = entityManager.createQuery(SELECT_SENSORS_BY_DEVICE_ID, Sensor.class)
                .setParameter(DEVICE_ID, device.getId())
                .getResultList();
        for (Sensor.SensorType type : Sensor.SensorType.values()) {
            Optional<Sensor> existingSensor = deviceSensors.stream()
                    .filter(s -> s.getType() == type)
                    .findFirst();
            if (existingSensor.isEmpty()) {
                String sensorId = UUID.randomUUID().toString();
                Sensor sensor = new Sensor(sensorId, type);
                device.addSensor(sensor);
                entityManager.persist(sensor);
            }
        }
    }

    /**
     * Добавляем устройства в БД
     */
    private static void initializeDevicesAndDeviceSensors(EntityManager entityManager) {
        for (String deviceName : DEVICE_NAMES) {
            Device device = entityManager.createQuery(SELECT_DEVICE_BY_NAME, Device.class)
                    .setParameter(NAME, deviceName)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (device == null) {
                device = new Device(deviceName);
                entityManager.persist(device);
            }
            initializeDeviceSensors(entityManager, device);
        }
    }

    /**
     * Добавляем устройства в БД и датчики у каждого устройства (всех типов)
     */
    public static void initialize(EntityManagerFactory entityManagerFactory) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            try {
                entityManager.getTransaction().begin();
                initializeDevicesAndDeviceSensors(entityManager);
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RuntimeException(e);
            }
        }
    }
}
