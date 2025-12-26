package aggregator.data.provider;

import common.entity.sensor.data.SensorData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Абстрактный класс для получения данных датчика
 */
public abstract class SensorDataProvider<T extends SensorData> {
    private static final String START = "start";
    private static final String END = "end";
    private static final String DEVICE_NAME = "deviceName";

    private final EntityManagerFactory factory;

    public SensorDataProvider(EntityManagerFactory factory) {
        this.factory = factory;
    }

    /**
     * Получение имени сущности (датчика) для части запроса вида: "SELECT data FROM <имя-сущности> data"
     */
    protected abstract String getEntityName();

    /**
     * Получение класса сущности (датчика)
     */
    protected abstract Class<T> getEntityClass();

    /**
     * Создание строки, содержащей запрос к данным нужного датчика
     * в нужном промежутке времени и по нужному имени устройства
     * (имя устройства опционально, м.б. null или пустое)
     */
    private String createQueryString(String deviceName) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT data FROM ");
        queryString.append(getEntityName());
        queryString.append(" data");
        queryString.append(" WHERE data.measureAt BETWEEN :start AND :end");
        if (deviceName != null && !deviceName.isEmpty()) {
            queryString.append(" AND data.sensor.device.name = :deviceName");
        }
        return queryString.toString();
    }

    /**
     * Устанавливаем параметры в запрос данных нужного датчика
     */
    private void setParamsInQuery(TypedQuery<T> query, LocalDateTime start, LocalDateTime end, String deviceName) {
        query.setParameter(START, start);
        query.setParameter(END, end);
        if (deviceName != null && !deviceName.isEmpty()) {
            query.setParameter(DEVICE_NAME, deviceName);
        }
    }

    /**
     * Получение данных датчика
     */
    public List<T> getData(LocalDateTime start, LocalDateTime end, String deviceName) {
        try (EntityManager entityManager = factory.createEntityManager()) {
            String queryString = createQueryString(deviceName);
            TypedQuery<T> query = entityManager.createQuery(queryString, getEntityClass());
            setParamsInQuery(query, start, end, deviceName);
            return query.getResultList();
        }
    }
}