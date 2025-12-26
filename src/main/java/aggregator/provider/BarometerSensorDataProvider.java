package aggregator.provider;

import common.entities.sensor.data.BarometerData;
import jakarta.persistence.EntityManagerFactory;

/**
 * Класс для получения данных с датчика BAROMETER
 *
 * @see SensorDataProvider
 */
public class BarometerSensorDataProvider extends SensorDataProvider<BarometerData> {
    public BarometerSensorDataProvider(EntityManagerFactory factory) {
        super(factory);
    }

    @Override
    protected String getEntityName() {
        return "BarometerData";
    }

    @Override
    protected Class<BarometerData> getEntityClass() {
        return BarometerData.class;
    }
}
