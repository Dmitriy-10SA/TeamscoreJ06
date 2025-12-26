package consumer.data.creator;

import common.entity.SensorReading;
import common.entity.sensor.data.LightData;

import java.util.Map;

/**
 * Класс для создания LightData сущности для сохранения в БД по данным датчика из таблицы SensorReading
 *
 * @see SensorDataCreator
 */
public class LightSensorDataCreator extends SensorDataCreator<LightData> {
    private static final String LIGHT = "light";

    @Override
    public LightData createAndGetSensorDataForSave(SensorReading sensorReading) throws Exception {
        Map<String, Integer> data = objectMapper.readValue(sensorReading.getValueJson(), Map.class);
        int light = data.get(LIGHT);
        return new LightData(sensorReading.getSensor(), sensorReading.getMeasuredAt(), light);
    }
}
