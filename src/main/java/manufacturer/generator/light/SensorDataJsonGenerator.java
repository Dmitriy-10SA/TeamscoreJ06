package manufacturer.generator.light;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.entity.sensor.data.SensorData;

/**
 * Абстрактный класс для генерации рандомных данных в виде Json для датчиков
 */
public abstract class SensorDataJsonGenerator<T extends SensorData> {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Генерация рандомных данных в виде Json
     */
    public abstract String generate() throws Exception;
}