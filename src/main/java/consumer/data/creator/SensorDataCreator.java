package consumer.data.creator;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.entity.SensorReading;
import common.entity.sensor.data.SensorData;

/**
 * Абстрактный класс для создания нужной сущности для сохранения в БД по данным датчика из таблицы SensorReading
 */
public abstract class SensorDataCreator<T extends SensorData> {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Создание и получение по данным из таблицы SensorReading нужной сущности для сохранения в БД
     */
    public abstract T createAndGetSensorDataForSave(SensorReading sensorReading) throws Exception;
}
