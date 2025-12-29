package consumer.data.creator;

import common.entity.SensorReading;
import common.entity.sensor.data.AccelerometerData;

import java.util.Map;

/**
 * Класс для создания AccelerometerData сущности для сохранения в БД по данным датчика из таблицы SensorReading
 *
 * @see SensorDataCreator
 */
public class AccelerometerSensorDataCreator extends SensorDataCreator<AccelerometerData> {
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";

    @Override
    public AccelerometerData createAndGetSensorDataForSave(SensorReading sensorReading) throws Exception {
        Map<String, Double> data = objectMapper.readValue(sensorReading.getValueJson(), Map.class);
        double x = data.get(X);
        double y = data.get(Y);
        double z = data.get(Z);
        return new AccelerometerData(sensorReading.getSensor(), sensorReading.getMeasuredAt(), x, y, z);
    }
}
