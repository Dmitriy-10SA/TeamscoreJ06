package aggregator;

import common.entities.Sensor;
import common.entities.sensor.data.AccelerometerData;
import common.entities.sensor.data.BarometerData;
import common.entities.sensor.data.LightData;
import common.entities.sensor.data.LocationData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс для получения данных датчиков
 */
public class SensorDataProvider {
    private static final String START = "start";
    private static final String END = "end";
    private static final String DEVICE_NAME = "deviceName";

    private final EntityManagerFactory factory;

    public SensorDataProvider(EntityManagerFactory factory) {
        this.factory = factory;
    }

    /**
     * Создание строки, содержащей запрос к данным нужного датчика
     * в нужном промежутке времени и по нужному имени устройства
     * (имя устройства опционально, м.б. null или пустое)
     */
    private String createQueryString(Sensor.SensorType type, String deviceName) {
        StringBuilder queryString = new StringBuilder();
        queryString.append(
                switch (type) {
                    case LIGHT -> "SELECT data FROM LightData data";
                    case ACCELEROMETER -> "SELECT data FROM AccelerometerData data";
                    case LOCATION -> "SELECT data FROM LocationData data";
                    case BAROMETER -> "SELECT data FROM BarometerData data";
                }
        );
        queryString.append(" WHERE data.measureAt BETWEEN :start AND :end");
        if (deviceName != null && !deviceName.isEmpty()) {
            queryString.append(" AND data.sensor.device.name = :deviceName");
        }
        return queryString.toString();
    }

    /**
     * Устанавливаем параметры в запрос данных нужного датчика
     */
    private <T> void setParamsInQuery(TypedQuery<T> query, LocalDateTime start, LocalDateTime end, String deviceName) {
        query.setParameter(START, start);
        query.setParameter(END, end);
        if (deviceName != null && !deviceName.isEmpty()) {
            query.setParameter(DEVICE_NAME, deviceName);
        }
    }

    /**
     * Получение данных датчика LIGHT
     */
    public List<LightData> getLightData(LocalDateTime start, LocalDateTime end, String deviceName) {
        try (EntityManager entityManager = factory.createEntityManager()) {
            String queryString = createQueryString(Sensor.SensorType.LIGHT, deviceName);
            TypedQuery<LightData> query = entityManager.createQuery(queryString, LightData.class);
            setParamsInQuery(query, start, end, deviceName);
            return query.getResultList();
        }
    }

    /**
     * Получение данных датчика ACCELEROMETER
     */
    public List<AccelerometerData> getAccelerometerData(LocalDateTime start, LocalDateTime end, String deviceName) {
        try (EntityManager entityManager = factory.createEntityManager()) {
            String queryString = createQueryString(Sensor.SensorType.ACCELEROMETER, deviceName);
            TypedQuery<AccelerometerData> query = entityManager.createQuery(queryString, AccelerometerData.class);
            setParamsInQuery(query, start, end, deviceName);
            return query.getResultList();
        }
    }

    /**
     * Получение данных датчика BAROMETER
     */
    public List<BarometerData> getBarometerData(LocalDateTime start, LocalDateTime end, String deviceName) {
        try (EntityManager entityManager = factory.createEntityManager()) {
            String queryString = createQueryString(Sensor.SensorType.BAROMETER, deviceName);
            TypedQuery<BarometerData> query = entityManager.createQuery(queryString, BarometerData.class);
            setParamsInQuery(query, start, end, deviceName);
            return query.getResultList();
        }
    }

    /**
     * Получение данных датчика LOCATION
     */
    public List<LocationData> getLocationData(LocalDateTime start, LocalDateTime end, String deviceName) {
        try (EntityManager entityManager = factory.createEntityManager()) {
            String queryString = createQueryString(Sensor.SensorType.LOCATION, deviceName);
            TypedQuery<LocationData> query = entityManager.createQuery(queryString, LocationData.class);
            setParamsInQuery(query, start, end, deviceName);
            return query.getResultList();
        }
    }
}
