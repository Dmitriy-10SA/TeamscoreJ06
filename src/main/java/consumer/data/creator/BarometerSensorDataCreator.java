package consumer.data.creator;

import common.entity.SensorReading;
import common.entity.sensor.data.BarometerData;

import java.util.Map;

/**
 * Класс для создания BarometerData сущности для сохранения в БД по данным датчика из таблицы SensorReading
 *
 * @see SensorDataCreator
 */
public class BarometerSensorDataCreator extends SensorDataCreator<BarometerData> {
    private static final String AIR_PRESSURE = "air_pressure";

    @Override
    public BarometerData createAndGetSensorDataForSave(SensorReading sensorReading) throws Exception {
        Map<String, Double> data = objectMapper.readValue(sensorReading.getValueJson(), Map.class);
        double airPressure = data.get(AIR_PRESSURE);
        return new BarometerData(sensorReading.getSensor(), sensorReading.getMeasuredAt(), airPressure);
    }
}
