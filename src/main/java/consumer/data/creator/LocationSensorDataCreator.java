package consumer.data.creator;

import common.entity.SensorReading;
import common.entity.sensor.data.LocationData;

import java.util.Map;

/**
 * Класс для создания LocationData сущности для сохранения в БД по данным датчика из таблицы SensorReading
 *
 * @see SensorDataCreator
 */
public class LocationSensorDataCreator extends SensorDataCreator<LocationData> {
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";

    @Override
    public LocationData createAndGetSensorDataForSave(SensorReading sensorReading) throws Exception {
        Map<String, Double> data = objectMapper.readValue(sensorReading.getValueJson(), Map.class);
        double longitude = data.get(LONGITUDE);
        double latitude = data.get(LATITUDE);
        return new LocationData(sensorReading.getSensor(), sensorReading.getMeasuredAt(), longitude, latitude);
    }
}