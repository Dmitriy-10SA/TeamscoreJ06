package aggregator.data.provider;

import common.entity.sensor.data.AccelerometerData;
import jakarta.persistence.EntityManagerFactory;

/**
 * Класс для получения данных с датчика ACCELEROMETER
 *
 * @see SensorDataProvider
 */
public class AccelerometerSensorDataProvider extends SensorDataProvider<AccelerometerData> {
    public AccelerometerSensorDataProvider(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    protected String getEntityName() {
        return "AccelerometerData";
    }

    @Override
    protected Class<AccelerometerData> getEntityClass() {
        return AccelerometerData.class;
    }
}
